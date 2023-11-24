//package jpabook.jpashop;
//
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
////@RunWith(SpringRunner.class) // junit한테 스프링관련테스트할거다알려주기
////Replace @Test with @Test from org.junit.jupiter.api.
////Remove @RunWith(SpringRunner.class) as JUnit Jupiter does not use this annotation.
////Keep @SpringBootTest for Spring Boot integration testing.
//
//@SpringBootTest
//public class MemberRepositoryTest {
//
//	@Autowired MemberRepository memberRepository;
//	
//
//	@Test
//	@Transactional // 엔티티매니저를통한모든변경은트랜잭션안에서이루어져야한다, @이거 테스트에있으면 테스트끝나고롤백
//	@Rollback(false)
//	public void testMember() throws Exception {
//		Member member = new Member();
//		member.setUsername("memberA");
//		
//		
//		Long savedId = memberRepository.save(member);	
//		Member findMember = memberRepository.find(savedId);
//		
//		
//		Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
//		Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
//	}
//	
//}
