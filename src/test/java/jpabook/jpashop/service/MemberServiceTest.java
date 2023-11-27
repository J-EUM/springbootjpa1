package jpabook.jpashop.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

// @RunWith(SpringRunner.class) //(junit4) 순수단위테스트아니고 디비까지 엮어서 스프링이랑 엮어서 테스트. 근데 JUnit Jupiter does not use this annotation.
@SpringBootTest // @SpringBootTest for Spring Boot integration testing.
@Transactional
class MemberServiceTest {
	
	@Autowired MemberService memberService;
	@Autowired MemberRepository memberRepository;
	@Autowired EntityManager em; // **트랜잭션 걍 롤백은 하지만 로그로 인서트 찍히는거 보고싶을때

	@Test//@Rollback(false) // 스프링 테스트 @Transactional 기본적으로 롤백해버리니까 눈으로 테스트 데이터 들어간거 보고싶으면 이거 추가하면 커밋한다
	public void 회원가입() throws Exception {
		//given  // given 이렇게주어졌을때 when 이렇게하면 then 이렇게된다 검증해라
		Member member = new Member();
		member.setName("kim");
		
		//when
		Long savedId = memberService.join(member);
		
		//then
		em.flush(); // **트랜잭션 걍 롤백은 하지만 로그로 인서트 찍히는거 보고싶을때. 영속성컨텍스트에 멤버객체 들어가고(위에 memberService.join(member)에서), 플러시는 영속성컨텍스트 db에반영. 강제로 쿼리를 날리고 @Transactional이 롤백해서 인서트됐던게 롤백
		assertEquals(member, memberRepository.findOne(savedId));
		// @Transactional jpa에서 같은 트랜잭션 안에서 같은 엔티티 id(pk)값이 같으면 같은 영속성컨텍스트 안에서 하나로관리됨
	} // 이거 돌리면 로그보면 db인서트 안함. 이유:em.persist에서 insert안하고 @GeneratedValue전략에서는 트랜잭션이 commit 될때 flush되면서 insert한다 jpa는 이러케동작한다
	
	@Test //(expected = IllegalStateException.class) // junit4
	public void 중복_회원_예외() throws Exception {
		//given
		Member member1 = new Member();
		member1.setName("kim");
		
		Member member2 = new Member();
		member2.setName("kim");
		
		//when
		memberService.join(member1);
//		try {
//			memberService.join(member2); // 예외발생해야함
//		} catch(IllegalStateException e) {
//			return;
//		}
		
		
		//assertThatThrownBy(() -> memberService.join(member2)).isInstanceOf(IllegalStateException.class);
		assertThrows(IllegalStateException.class, () -> memberService.join(member2));
		// junit5 두개중에하나
		
		//then
		//fail("예외발생해야함"); // fail: 여기 오면 실패. 위에 join(member2)에서 예외처리하고 나가야됨 여기까지오면안됨
	}

}
