package jpabook.jpashop.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;

@Repository // 스프링빈으로 스프링이 등록해주죠..
public class MemberRepository {
	
	@PersistenceContext // jpa가제공하는표준어노테이션. 이거있으면 jpa엔티티매니저를 스프..jpa를 순수하게쓰면 내가 엔티티매니저팩토리에서 직접 엔티티매니저를막꺼내고써야되는데 그럴필요없이스프링이다해결해준다
	private EntityManager em; // 스프링이 엔티티매니저를만들어서 주입해준다
	
	// 만약에 엔티티매니저팩토리를직접주입하고싶으면
	// @PersistenceUnit
	// private EntityManagerFactory emf;
	// 근디이렇게쓸일없겠져
	
	public void save(Member member) {
		em.persist(member); 
		// jpa가 멤버 저장함 persist 하면 영속성컨텍스트에 멤버객체를넣음->트랜잭션커밋하는시점에 디비에반영 insert쿼리. 
		// 키랑 밸류가 있는데 키=pk. @GeneratedValue면 id 항상 생성되는게 보장된다. em.persist할때. 아직 디비에 들어가는 시점이 아닐때도 id 있다
	}
	
	public Member findOne(Long id) {
		return em.find(Member.class, id); // 단건조회 (타입, pk)
	}
	
	public List<Member> findAll() {
		// 전부다 찾을때는 jpql작성해야된다
		// sql은 테이블대상으로쿼리, jpql은 엔티티 객체대상으로
		// (jpql, 반환타입)
		return em.createQuery("select m from Member m", Member.class)
			.getResultList();		
	}
	
	public List<Member> findByName(String name) {
		return em.createQuery("select m from Member m where m.name = :name", Member.class)	// :name->파라미터바인딩, Member.class->조회타입
				.setParameter("name", name)
				.getResultList();
	}

}
