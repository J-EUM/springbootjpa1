package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
//스프링 데이터 JPA 소개
//https://spring.io/projects/spring-data-jpa
//스프링 데이터 JPA는 JPA를 사용할 때 지루하게 반복하는 코드를 자동화 해준다. 이미 라이브러리는 포함되어 있다.
//기존의 MemberRepository 를 스프링 데이터 JPA로 변경해보
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {//타입, pk타입
    //findAll, save, 등등등등등등....다있다
    //구현체를 스프링빈인젝션해줘야되지않나요?->구현체를 스프링데이터jpa가 알아서 만들어서 다 넣어줍니다

    List<Member> findByName(String name); //이게끝임 아래처럼 안해도됨;;
    //public List<Member> findByName(String name) {
    //		return em.createQuery("select m from Member m where m.name = :name", Member.class)	// :name->파라미터바인딩, Member.class->조회타입
    //				.setParameter("name", name)
    //				.getResultList();
    //	}
    //List<Member> findByName(String name);이거를 가지고 findBy 뒤에 Name 있으면 select m from Member m where m.name = :name 이걸 만들어버림
    //아무렇게나 findByNamenamae 이렇게하면안됨

    //스프링 데이터 JPA는 JpaRepository 라는 인터페이스를 제공하는데, 여기에 기본적인 CRUD 기능이 모두
    //제공된다. (일반적으로 상상할 수 있는 모든 기능이 다 포함되어 있다.)
    //findByName 처럼 일반화 하기 어려운 기능도 메서드 이름으로 정확한 JPQL 쿼리를 실행한다.
    //select m from Member m where m.name = :name
    //개발자는 인터페이스만 만들면 된다. 구현체는 스프링 데이터 JPA가 애플리케이션 실행시점에 주입해준다.
}
