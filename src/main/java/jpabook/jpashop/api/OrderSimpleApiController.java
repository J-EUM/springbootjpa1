package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

//    @GetMapping("api/v1/simple-orders")
//    public List<Order> ordersV1() {
//        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
//        return all;
//        //->무한루프
//        //Order에 멤버가 있음
//        //멤버에 orders가있음
//        //order에 멤머가 있음
//        //....무한루프
//        //양방향연관관계에있는하나 @JsonIgnore해야됨
//        //Member orders
//        //OrderItem order
//        //Delivery order
//        //->
//        //Order의 member (fetch=LAZY)돼있음 디비에서 안끌고옴 Order만가져옴 근데 member없다고 null너둘순없음 그래서 하이버네이트에서 프록시멤버를만들어서넣어놈 그래서 멤버에 ByteBuddyInterceptor 프록시객체들어가있음 프록시객체 가짜로넣어놓고 멤버객체에 손대면 그때 디비꺼가져와서채워줌
//        //근디 제이슨이 ByteBuddyInterceptor이거가지고 머 할수 없어서 에러남
//        //어쩌구저쩌고
//        //걍 하지마이렇게
//참고: 앞에서 계속 강조했듯이 정말 간단한 애플리케이션이 아니면 엔티티를 API 응답으로 외부로 노출하는 것은
//좋지 않다. 따라서 Hibernate5Module 를 사용하기 보다는 DTO로 변환해서 반환하는 것이 더 좋은 방법이
//다.
//    }

    @GetMapping("api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() { //List<SimpleOrderDto> 이거 반환하면 안되고 {}이러케 감싸서 보내야되는데 예제니까 걍 대충 함

        //ORDER 2개
        //N + 1 -> 1 + 회원N + 배송N
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        //오더 하나당 멤버, 주소쿼리 2개씩
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o)) //맵: a를 b로바꾸는거
                .collect(Collectors.toList());

        return result;
    } //꼭이러케dto로바꿔야됨 문제: 레이지로딩때매 쿼리가 너무많이호출됨
    // 주문 하나에 쿼리 두개씩더
    // ORDER -> SQL 1번 -> 결과 주문수 2개
    //엔티티를 DTO로 변환하는 일반적인 방법이다
    //쿼리가 총 1 + N + N번 실행된다. (v1과 쿼리수 결과는 같다.)
    //order 조회 1번(order 조회 결과 수가 N이 된다.)
    //order -> member 지연 로딩 조회 N 번
    //order -> delivery 지연 로딩 조회 N 번
    //예) order의 결과가 4개면 최악의 경우 1 + 4 + 4번 실행된다.(최악의 경우) //바로디비확인안하고 영속성컨텍스트먼저확인하니까 같은유저가주문한거면 member조회 줄어든다
    //지연로딩은 영속성 컨텍스트에서 조회하므로, 이미 조회된 경우 쿼리를 생략한다




    //v2랑 쿼리만다름
    //v2 쿼리 5번
    //v3 쿼리 1번
    @GetMapping("api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {

        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        //ㄴ멤버객체랑 딜리버리객체 같이조회돼서나오는거->지연로딩X
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o)) //맵: a를 b로바꾸는거
                .collect(Collectors.toList());

        return result;
    }    //실무 jpa성능문제 90퍼가 n+1문제다 패치조인 정확히 이해하면 -> 기본LAZY로다깔고 필요한것만 패치조인으로 객체그래프묶어서 디비에서 한방에가져오면 대부분성능문제해결된다
//    엔티티를 페치 조인(fetch join)을 사용해서 쿼리 1번에 조회
//    페치 조인으로 order -> member , order -> delivery 는 이미 조회 된 상태 이므로 지연로딩X
    // 단점: 다끌고와서 엔티티찍어서조회하는것이단점


    //엔티티조회해서 dto변환 안하고 바로 dto로 꺼내는거
    //v3보다 select 조금함 필요한것만 select해서
    //단점: v3패치조인보다 재사용성떨어짐 OrderSimpleQueryDto용만 가능
    //엔티티 아니고 dto로조회했으니까 머 변경 이런거 안됨
    //    SELECT 절에서 원하는 데이터를 직접 선택하므로 DB 애플리케이션 네트웍 용량 최적화(생각보다 미비)
    //    리포지토리 재사용성 떨어짐, API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점
    @GetMapping("api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {

        return orderSimpleQueryRepository.findOrderDtos();

    }


    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) { //dto에서는 엔티티 파라미터로받아도됨
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY초기화: 영속성컨텍스트가 멤버아이디가지고 영속성컨택스트찾아보는데없자나 그래서 쿼리날림
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }

    }
}
//          쿼리 방식 선택 권장 순서
//   v2     1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.
//   v3     2. 필요하면 페치 조인으로 성능을 최적화 한다. 대부분의 성능 이슈가 해결된다.
//   v4     3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
//        4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용한다.