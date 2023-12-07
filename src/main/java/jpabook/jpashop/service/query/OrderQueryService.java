package jpabook.jpashop.service.query; //핵심비지니스 service에놓고 단순 조회용 패키지 따로 만들기
//OrderService: 핵심 비즈니스 로직
//OrderQueryService: 화면이나 API에 맞춘 서비스 (주로 읽기 전용 트랜잭션 사용)

import org.springframework.transaction.annotation.Transactional;

//spring.jpa.open-in-view: false 이거해놓고~
@Transactional(readOnly = true)
public class OrderQueryService {
    //...
}

