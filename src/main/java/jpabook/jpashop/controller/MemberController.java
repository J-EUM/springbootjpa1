package jpabook.jpashop.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	
	@GetMapping("/members/new")
	public String createForm(Model model) {
		model.addAttribute("memberForm", new MemberForm()); //컨트롤러에서 뷰로 넘어갈때 이 데이터를 실어서 넘긴다. memberForm이라는 빈껍데기 MemberForm()들고감. 빈화면이지만 밸리데이션같은거 해주기때매 빈껍데기라도 들고감
		return "members/createMemberForm";
	}
	
	@PostMapping("/members/new")
	public String create(@Valid MemberForm form, BindingResult result) { //@Valid하면 MemberForm에 @NotEmpty(message = "회원 이름은 필수 입니다.")이거 밸리데이션해줌
		// BindingResult하면 Valid한 에러가 안튕기고 result에 담겨서 아래코드 실행됨
		if(result.hasErrors()) {
			return "members/createMemberForm";
			// 밸리데이션 에러가 있으면 다시 폼으로 보내버리는데
			// 스프링에서 바인딩리절트를 폼에서 다시 쓸수있게해준다->createMemberForm.html에 에러처리
			// 도시 거리 우편번호 데이터 form에 넣었던거 그대로 다시 화면에 가져감
		}
		
		//Member 엔티티 말고 MemberForm쓰는거: 디비왔다갔다데이터랑 화면이랑 다른경으가 많으니까 Form따로 만들어서 컨트롤러에서 정제해서 쓰는거 추천
		
		Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
		
		Member member = new Member();
		member.setName(form.getName());
		member.setAddress(address);
		
		memberService.join(member);
		return "redirect:/"; // 저장하고나면 재로딩되면 안좋으니까 리다이렉트로 홈에 보내버림
	}
	
	@GetMapping("/members")
	public String list(Model model) {
		List<Member> members = memberService.findMembers();
		//이것도 Member엔티티 그대로 뿌리는거보다는 dto로변환해서 화면에필요한것만 뿌리는게좋다
		//근데 템플릿엔진에서 랜더링할때는 서버안에서 기능이 돌기때매 멤버를 화면 템플릿에 전달해도 괜찮다 어차피 원하는 데이터만 찍어서 출력하기때매(그래도dto로변환해서주는게젤조음)
		//근데 api만들때는 무조건 이유불문 엔티티넘기면안됨 절대절대 외부에 반환하면 안됨
		//왜?api는하나의스펙인데 멘버엔티티를 반환하게되면 ex Member에 패스워드 추가한다고 치면->패스워드 노출위험, 엔티티에 로직하나추가해서 api스펙이변하게됨
		model.addAttribute("members", members);
		return "members/memberList";
	}
}
