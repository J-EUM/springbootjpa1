package jpabook.jpashop.repository.order.simplequery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;


    //	public List<OrderSimpleApiController.SimpleOrderDto> findOrderDtos() {
//	}//레포에서 컨트롤러 의존관계 생기면 망함 한방향으로가야댐
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" + //select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o) 안됨, 딜리버리는 값타입이라서  d.address됨
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
//OrderRepository는 순수하게 엔티티조회에만쓰고
//이거는 화면에 박혀버린거라서
//쿼리도 복잡하고
//쿼리서비스나 쿼리레포지토리 이런거 새로만들어서쓰는게좋다 조회전용으로 화면에맞춰서쓰는용
//유지보수위해
//이런게 그냥 레포지포리에있으면 용도가 애매해지니까
//조회전용으로 화면에맞춰서쓰는용 분리 권장