package jpabook.jpashop.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@NotEmpty //화면을위한검증로직이 엔티티에들어가있다 노노 어떤api는 notempty아닐수도있음.따로따로 api마다 만들어야됨
	private String name;
	
	@Embedded // 내장타입포함했다
	private Address address;

	@JsonIgnore
	@OneToMany(mappedBy = "member") // 멤버-오더에서 외래키있는 오더가 주인. Order의 member에의해 맵핑됐다~읽기전용
	private List<Order> orders = new ArrayList<>();
	
}
