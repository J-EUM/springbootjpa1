package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;


    //findOrders 1번
    //findOrderItems 오더아이템-아이템 to one이니까 한번에 *2(오더 두개니까)
    //총3번쿼리
    //1+2 n+1문제있다
    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        //select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
        //아이템안채워서 하나씩넣어줘야함
        //row 수가 증가하지 않는 ToOne 관계는 조인으로 최적화 하기 쉬우므로 한번에 조회하고, ToMany 관계는 최적
        //화 하기 어려우므로 findOrderItems() 같은 별도의 메서드로 조회한다.
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);

        });

        return result;
    }

    //위에꺼 findOrderQueryDtos v4는 루프돌때마다 쿼리날렸는데
    // 얘는 쿼리한번날리고(in) 쿼리한번날린다음에 메모리에 맵으로 다 가져온다음에
    //메모리에서 매칭해서값세팅 result.forEach(o -> o....
    //쿼리총2번 findOrders()이걸로1(루트), findOrderItemMap(orderIds)이걸로1(컬렉션)
    //Query: 루트 1번, 컬렉션 1번
    //ToOne 관계들을 먼저 조회하고, 여기서 얻은 식별자 orderId로 ToMany 관계인 OrderItem 을 한꺼번에 조
    //회
    //MAP을 사용해서 매칭 성능 향상(O(1))
    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();
        
        //이전꺼 단점 루프도는거 안하고 한방에
        List<Long> orderIds = toOrderIds(result);

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId()))); //orderItemMap에서 키o.getOrderId()로 찾아서

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" + //얘도 querydsl로 간단하게바꿀수있음
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class) //오더아이디 =말고 in으로
                .setParameter("orderIds", orderIds)
                .getResultList();

        //orderItems 그냥써도좋지만 최적화할라고 맵으로바꾸기
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));//오더아이디기준으로 맵으로바꾸기 키가.getOrderId( 값은 OrderItemQueryDto
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
    }


    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" + //걍조인 오더아이템수만큼 데이터뻥튀기될것
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
