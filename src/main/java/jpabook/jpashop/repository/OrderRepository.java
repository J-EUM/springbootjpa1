package jpabook.jpashop.repository;

import java.util.ArrayList;
import java.util.List;

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
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

	private final EntityManager em;
	
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

	public List<Order> findAllWithMemberDelivery() {
		return em.createQuery(
				"select o from Order o" +
						" join fetch o.member" + // order 가져올때 member까지 객체 그래프로(?) 한방에 가져오려고하는거
						" join fetch o.delivery d", Order.class
				// LAZY무시하고 다채워서가져옴 패치조인 fetch는 jpa문법 성능최적화때매 자주사용하는거니까 책이나강좌보셈추천
		).getResultList();
	}




}
