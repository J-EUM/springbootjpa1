package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service // Service어노테이션에 @Component있음, 컴포넌트스캔대상이돼서 자동으로스프링빈으로등록이됨니다..
@Transactional(readOnly = true) // jpa 데이터변경이나 로직들 가급적이면 트랜잭션 안에서 실행돼야한다
@RequiredArgsConstructor
public class MemberService {

//	@Autowired // 이거쓰면 인젝션된다. 다른더좋은방법도있음. @Autowired하면 스프링이 스프링빈에등록돼있는 멤버리포지토리를 주입해준다 인젝션
//	private MemberRepository memberRepository;
	// 어쩌구저쩌구 해서 (pdf참고) final, 클래스에 @RequiredArgsConstructor하는것이 좋다
	private final MemberRepository memberRepository;
	
	
	/** 
	 * 회원가입
	 * @param member
	 * @return
	 */
	@Transactional // 읽기에(readOnly = true)하면 최적화, 쓰는거에다가는 하면 데이터 안바뀜. 그래서 지금은 읽기용이 더 많으니까 위에 클래스에다가 read only 해놓고 쓰는거에다가 따로 트랜잭션(리드온리 false가 디폴트라서 걍 트랜잭셔널 해노면됨)
	public Long join(Member member) {
		validateDuplicateMember(member); // 중복회원검증
		memberRepository.save(member);
		return member.getId(); // 멤버 id에 @GeneratedValue해서 id있어서 id 꺼내줄수있다. id라도 돌려줘야 뭐저장됐는지 아니까 id주기
	}
	
	private void validateDuplicateMember(Member member) {
		List<Member> findMembers = memberRepository.findByName(member.getName());
		if (!findMembers.isEmpty()) { // 이거보다 숫자세서 0보다크면 안된다하는게 최적화되겠지만 예제에선이정도로하겟슴다
			throw new IllegalStateException("이미 존재하는 회원입니다.");		//EXEPTION
		}
		// 이렇게해놔도 동시에하는거 고려해서 디비에 멤버 name에 유니크키 걸어놓는것이좋다 안전하게
	}
	
	// 회원전체조회
	public List<Member> findMembers() {
		return memberRepository.findAll();
	}
	
	// 회원하나조회
	public Member findOne(Long memberId) {
		return memberRepository.findOne(memberId);
	}

	@Transactional
	public void update(Long id, String name) {
		Member member = memberRepository.findOne(id);
		member.setName(name); //변경감지
	}
}
