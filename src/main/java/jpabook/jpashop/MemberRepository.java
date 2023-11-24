//package jpabook.jpashop;
//
//import org.springframework.stereotype.Repository;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//
//@Repository
//public class MemberRepository {
//
//	@PersistenceContext
//	private EntityManager em; // 위에 @Per~이 엔티티매니저주입해줌
//	
//	public Long save(Member member) {
//		em.persist(member);
//		return member.getId();
//	}
//	
//	public Member find(Long id) {
//		return em.find(Member.class, id);
//	}
//}
