package jpabook.jpashop.repository.order.simplequery;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) { //dto에서는 엔티티 파라미터로받아도됨
        this.orderId = orderId;
        this.name = name; //LAZY초기화: 영속성컨텍스트가 멤버아이디가지고 영속성컨택스트찾아보는데없자나 그래서 쿼리날림
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address; //LAZY 초기화
    }

}
