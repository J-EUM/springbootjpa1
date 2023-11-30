package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller @ResponseBody 합친거
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers(); //@RestController라서 리스트 멤버를 쭉 가지고 제이슨으로 쫙 바로 바뀔거란말이에여
        //이것의문제점:
        //주문까지 포함해서 외부에 노출됨-엔티티에 orders에 @JsonIgnore하면됨
        //근디 이렇게하면 답이없다 다른api에는 오더 필요할수도?
        //그리고 엔티티노출하지말라고 망합니다 엔티티바꾸면 api스펙바뀜니다 장애난다
        //엔티티직접반환노노노노ㅗ노노노노
        //* - 추가로 컬렉션을 직접 반환하면 항후 API 스펙을 변경하기 어렵다.(별도의 Result 클래스 생성으
        //로 해결)
        //[
        //    {
        //        "id": 1,
        //        "name": "이름변경",
        //        "address": null,
        //        "orders": []
        //    },
        //    {
        //        "id": 2,
        //        "name": "2름변경",
        //        "address": null,
        //        "orders": []
        //    }
        //] 이거노노
        //기본적으로 밖에 오브젝트있고
        //{
        //    "count": 4,
        //    "data": []
        //} 이런식으로 count넣는등 확장가능
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers(); //가져와서 멤버dto로 바꿔서 넘긴다

        //List<Member> -> List<MemberDto> 바꾸기
        //for (Member member: members){ new MemberDto() } //이렇게해도되고
        List<MemberDto> collect = findMembers.stream().map(m -> new MemberDto(m.getName())).toList();//m.getName()멤버엔티티에서 이름을꺼내와서 new MemberDto(m.getName())멤버dto에넣고
        return new Result(collect.size(), collect);
    }
    
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }
    
    @Data
    @AllArgsConstructor
    static class MemberDto{
        String name; //귀찮으니까 이름만 넘긴다 치기
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { //엔티티를 외부에서넘어오는 제이슨에다 바인딩하는데쓰면안된다 dto만들어야된다. 그리고 요즘에 회원가입로그인 방법 개많다 간편로그인 페이스북가입 이런거 엔티티 한개로 다 못한다 다따로해야된다 걍 api만들때 엔티티를 파라미터로 받지말고 엔티티를 외부에 노츨하는것도 안된다
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) { //별도의 dto CreateMemberRequest 사용->엔티티변경해도 api스펙이 안바뀐다
        //엔티티랑 프레젠테이션계층을위한로직분리할수있다
        //엔티티변경돼도 api스펙변경안된다
        //실무에서는절대로엔티티외부에노충하면안된다 엔티티를파라미터로그대로받으면안된다
        //클래스만들기귀찮다고그냥하다가나중에큰장애를만나게될것이다..저주?
        //진짜로나중에망한다
        //api는 외부에서 들어오고나가는거 절대절대 엔티티사용하지않는다
        //dto객체사용해라

        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) { //등록이랑 수정은 api스펙 거의 다르다 수정이 더 제한적 한번등록하면 수정안되는거이써서

        memberService.update(id, request.getName()); //커맨드-얘는 업데이트만. 여기서 그냥 member리턴해도 되는데 그러면 어쩌구저꺼고..커맨드랑 쿼리가 같이있어...해서 쿼리 분리한다
        Member findMember = memberService.findOne(id); //쿼리-얘는 다시조회
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());

    }

    @Data
    static class UpdateMemberRequest {
        private String name;

    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;

    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name; //어드레스걍생략
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}
