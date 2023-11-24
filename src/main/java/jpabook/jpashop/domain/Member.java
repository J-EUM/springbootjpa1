package jpabook.jpashop.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;
	
	private String name;
	
	@Embedded // 내장타입포함했다
	private Address address;
	
	@OneToMany(mappedBy = "member") // 멤버-오더에서 외래키있는 오더가 주인. Order의 member에의해 맵핑됐다~읽기전용
	private List<Order> orders = new ArrayList<>();
	
}
