package jpabook.jpashop.repository;

import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import static jpabook.jpashop.domain.QMember.*;
import static jpabook.jpashop.domain.QOrder.*;

@Repository
//@RequiredArgsConstructor
public class OrderRepository {

	private final EntityManager em;
	private final JPAQueryFactory query;

	public OrderRepository(EntityManager em) {
		this.em = em;
		this.query = new JPAQueryFactory(em);
	}
	
	public void save(Order order) {
		em.persist(order);
	}
	
	public Order findOne(Long id) {
		return em.find(Order.class, id);
	}
	
	// 검색
//	/**
//	 * 
//	 * @param orderSearch
//	 * @return
//	 */
//	public List<Order> findAll(OrderSearch orderSearch) {
//		
////		return em.createQuery("select o from Order o join o.member m" + 
////					" where o.status = :status " +
////					" and m.name like :name "
////					, Order.class)
////				.setParameter("status", orderSearch.getOrderStatus())
////				.setParameter("name", orderSearch.getMemberName())
//////				.setFirstResult(100) //100부터시작해서 offset
//////				.setMaxResults(1000) //최대1000개 limit
////			.getResultList();
//		
//		//querydsl
//		
//		
//	}
	
	public List<Order> findAllByCriteria(OrderSearch orderSearch) {
		 CriteriaBuilder cb = em.getCriteriaBuilder();
		 CriteriaQuery<Order> cq = cb.createQuery(Order.class);
		 Root<Order> o = cq.from(Order.class);
		 Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
		 List<Predicate> criteria = new ArrayList<>();
		 //주문 상태 검색
		 if (orderSearch.getOrderStatus() != null) {
		 Predicate status = cb.equal(o.get("status"),
		orderSearch.getOrderStatus());
		 criteria.add(status);
		 }
		 //회원 이름 검색
		 if (StringUtils.hasText(orderSearch.getMemberName())) {
		 Predicate name =
		 cb.like(m.<String>get("name"), "%" +
		orderSearch.getMemberName() + "%");
		 criteria.add(name);
		 }
		 cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
		 TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대	1000건
		 return query.getResultList();
		}


	//QueryDSL
	//문자로작성하면 돌려봐야 잘못된거 알수있는데
	//얘는 컴파일시점에 쿼리오류 잡을수있다
	public List<Order> findAll(OrderSearch orderSearch) {

		//JPAQueryFactory query = new JPAQueryFactory(em); //얘도 OrderRepository 컨스트럭터 만들어서 지우기->코드간결

		//Q파일생성해야됨 build.gradle
		//그리고 genereated된 파일들 gitignore에 추가. 빌드타임에 generate되면 되기때문
//		QOrder order = QOrder.order;
//		QMember member = QMember.member; //없어도 아래 query.select(order)... static import해서 쓸수있다
		//import static jpabook.jpashop.domain.QMember.*;
		//import static jpabook.jpashop.domain.QOrder.*;

		return query.select(order)
				.from(order)
				.join(order.member, member) //오더의 멤버를 조인하고 alias를 QMember member = QMember.member; 이걸로준다
				.where(statusEq(orderSearch.getOrderStatus()),  //statusEq 여기서 null오면 where에서 안쓴다 //정적쿼리 .where(order.status.eq(orderSearch.getOrderStatus())))
						nameLike(orderSearch.getMemberName()))  // 위에꺼 아래꺼 and조건
				.limit(1000)
				.fetch();
		//jpql이랑똑같이생겼다 jpql로 바껴서 실행된다
		//자바코드라서 오타가 다 잡힌다

	}

	private BooleanExpression nameLike(String memberName) { //BooleanExpression는 쿼리dsl꺼 임포트 import com.querydsl.core.types.dsl.BooleanExpression;
		if (!StringUtils.hasText(memberName)) {
			return null;
		}
		//return member.name.like(memberName); 		// -> like "name"
		return member.name.contains(memberName);  	// -> like "%name%"
	}

	private BooleanExpression statusEq(OrderStatus statusCond) {
		if (statusCond == null){
			return null;

		}
		return order.status.eq(statusCond);
	}

	public List<Order> findAllWithMemberDelivery() {
		return em.createQuery(
				"select o from Order o" +
						" join fetch o.member" + // order 가져올때 member까지 객체 그래프로(?) 한방에 가져오려고하는거
						" join fetch o.delivery d", Order.class
				// LAZY무시하고 다채워서가져옴 패치조인 fetch는 jpa문법 성능최적화때매 자주사용하는거니까 책이나강좌보셈추천
		).getResultList();
	}




	public List<Order> findAllWithItem() {
		return em.createQuery(
				"select distinct o from Order o" + //distinct하면 db sql distinct(근데 db distinct 해도 한줄 다똑같아야되는건데암튼 sql에도 distinct날려줌) + Order(루트 엔티티)가져올때 같은id값이면 중복제거해줌
						" join fetch o.member m" +
						" join fetch o.delivery d" +
						" join fetch o.orderItems oi" + //Order랑 Orderitems조인하면 데이터 4개됨(오더1에 아이템2, 오더2에아이템2) 일대다에서 다만큼 (근데 api보내니까 뻥튀기안됨 업데이트됐나..)->Hibernate 6.0부터는 distinct가 자동 적용이였네유...
						" join fetch oi.item i", Order.class)
				.setFirstResult(1) //0부터시작->2번째데이터부터
				.setMaxResults(100)//100개가져와라
				//페이징안됨
				//WARN 43776 --- [nio-8080-exec-2] org.hibernate.orm.query                  : HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
				//디비에서 다가져온다음에 메모리에서페이징처리
				//왜냐면 쿼리에서 페이징하면 오더1 오더1 오더2 오더2 이렇게있으니까 여기서 페이징하면 오더기준으로 페이징이아니라 뻥튀기기준으로(오더아이템기준)페이징하니까
				//하이버네이트는 메모리에서페이징한다
				//데이터개많으면 망함
				//십만개가져온다음에 메모리에서 페이징..
				//일대다는 페이징하지마셈
				//그리고 컬렉션패치조인 1개만해야된다 (orderItems)
				.getResultList();
	}

	public List<Order> findAllWithMemberDelivery(int offset, int limit) {
		return em.createQuery(
				"select o from Order o" +
						" join fetch o.member" + // order 가져올때 member까지 객체 그래프로(?) 한방에 가져오려고하는거
						" join fetch o.delivery d", Order.class
				// LAZY무시하고 다채워서가져옴 패치조인 fetch는 jpa문법 성능최적화때매 자주사용하는거니까 책이나강좌보셈추천
		)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}
}
