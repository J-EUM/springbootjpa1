package jpabook.jpashop.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {

	@NotEmpty(message = "회원 이름은 필수 입니다.")
	private String name;
	
	private String city;
	private String street;
	private String zipcode;
}
//Member 엔티티 말고 MemberForm쓰는거: 디비왔다갔다데이터랑 화면이랑 다른경으가 많으니까 Form따로 만들어서 컨트롤러에서 정제해서 쓰는거 추천
//엔티티를 쓰면 엔티티에 화면종속적인기능이계속생겨버림 그러면 화면기능때매 엔티티가지저분해짐->유지보수어려워짐
//엔티티를 최대한 순수하게 유지해야됨 핵심비즈니스로직에만 디펜던시 있도록 해애ㅑ함
//실무에서는 엔티티는 화면에맞는로직은없어야함 화면용은 폼이나 dto사용해야함
//안그러면 눙물을 흘리게될것,,
//핵심비즈니스로직 호출하는데 화면로직있음, 핵심비즈니스로직 고쳤더니 화면깨짐 이런거, 화면고칠라했는데 핵심비즈니스로직안돌아감 이럼안됨
