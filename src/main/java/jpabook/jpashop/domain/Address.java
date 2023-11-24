package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable // 내장될수있다
@Getter		// 값타입은 변경안되게 setter노노
public class Address {
	
	private String city;
	private String street;
	private String zipcode;
	
	// 기본생성자 만들어: JPA 스펙상 엔티
//	티나 임베디드 타입( @Embeddable )은 자바 기본 생성자(default constructor)를 public 또는
//	protected 로 설정해야 한다. public 으로 두는 것 보다는 protected 로 설정하는 것이 그나마 더 안전
//	하다
	// > JPA가 이런 제약을 두는 이유는 JPA 구현 라이브러리가 객체를 생성할 때 리플랙션 같은 기술을 사용할 수
	// 있도록 지원해야 하기 때문이다.
	// 아무렇게나 new 생성하면 안되니까 protected
	protected Address() {
	}

	// 생성자에서 값을 모두 초기화해서 변경 불가능한 클래스를 만들자.
	public Address(String city, String street, String zipcode) {
		this.city = city;
		this.street = street;
		this.zipcode = zipcode;
	}
}
