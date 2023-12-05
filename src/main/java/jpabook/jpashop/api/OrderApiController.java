package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

//    public List<Order> ordersV1() {
//        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
//        for (Order order : all) {
//
//        }
//    }


    //오더조회->2개
    //1멤버
    //1주소
    //1오더아이템(2개)
    //1-1 1번아이템
    //1-2 2번아이템
    //....
    //컬렉션은 쿼리 더마니나감
    //성능개망함
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // ordersV2이랑  ordersV3랑 컨트롤러 코드는 똑같음
    // 근디 쿼리 10개->1개날라감
    // jpa안쓰고 쿼리직접쓰면 10개->1개바뀌면 그거에맞춰서 코드만들고 전용dto만들고 해야댐
    // jpa 패치조인쓰면 객체그래프만 내가 찍어주면 distinct까지 고려해서 최적의결과반환 쿼리한방에
    // 단점: 일대다 패치조인 페이징불가능 디비에서 페이징해서 안가져오고 다가져와서 메모리에서 페이징함 망함
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
//
//        for (Order order : orders) {
//            System.out.println("order ref= " + order + "id= " + order.getId());
//
//        }//Hibernate 6.0부터는 distinct가 자동 적용이였네유...
//        // 암튼 자동적용안되면 뻥튀기된거 찍어보면 레퍼런스까지 똑같음

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;

    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        //먼저 ToOne(OneToOne, ManyToOne) 관계를 모두 페치조인 한다. ToOne 관계는 row수를 증가시키지 않
        //으므로 페이징 쿼리에 영향을 주지 않는다.
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        // -> 오더,멤버,딜리버리까지 쿼리1개로가져옴
        // -> OrderDto가 루프돌면서 orderItems = order.getOrderItems().stream()
        //                    .map(orderItem -> new OrderItemDto(orderItem))
        //                    .collect(Collectors.toList()); 오더아이템스
        // 오더아이템스 n+1
        // 아이템 n+1
        // 총6번 1+n+m
        //근데 컬렉션은 지연 로딩으로 조회한다.
        //지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size , @BatchSize 를 적용한다.
        //hibernate.default_batch_fetch_size: 글로벌 설정
        //@BatchSize: 개별 최적화
        //이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size 만큼 IN 쿼리로 조회한다.
        // 이거하면 오오더,멤버,딜리버리 쿼리1+오더아이템1+아이템1 이렇게 세번 날아감(batch_fetch_size 값따라서..10으로해놨는데 100개있으면 10번날아감)

        //v3단점: 쿼리는1갠데 중복데이터짱많음 뻥튀기데이터 다 애플리케이션으로 전송됨 용량많은데이터
        //v3.1은
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;

    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address; //이건밸류오브젝트니까 상관없음
        //private List<OrderItem> orderItems; //엔티티노출.엔티티의존완전끊어야함 얘도 dto로 다 바꿔야됨
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
//            orderItems = order.getOrderItems(); //이렇게하면
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());

        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

}
