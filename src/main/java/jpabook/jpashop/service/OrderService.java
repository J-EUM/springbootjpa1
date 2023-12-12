package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.CascadeType;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;
	private final ItemRepository itemRepository;
	
	/**
	 * 주문
	 */
	@Transactional
	public Long order(Long memberId, Long itemId, int count) {
		// 엔티티조회
		// 트랜잭션안에서 조회해서 영속성컨텍스트에너놓는게좋다
		//Member member = memberRepository.findOne(memberId); //MemberRepositoryOld
		Member member = memberRepository.findById(memberId).get();
		Item item = itemRepository.findOne(itemId);
		
		// 배송정보생성
		Delivery delivery = new Delivery();
		delivery.setAddress(member.getAddress());
		
		//주문상품생성
		OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
		
		//주문생성
		Order order = Order.createOrder(member, delivery, orderItem);
		
		//주문 저장
		orderRepository.save(order);
		// 딜리버리, 오더아이템 따로 persist해야되는데 오더 하나만 save했다
		// ->엔티티 cascade = CascadeType.ALL 때매
		// 오더만 오더아이템 관리하고 오더만 딜리버리관리하는 지금이런정도에서만 써야된다. 다른데서 오더아이템이랑 딜리버리 안쓰고있음 이럴때만
		// 아니면 따로 딜리버리랑 오더아이템도 레포 생성해서 persist따로
		
		return order.getId();
	}
	

	/**
	 * 취소
	 */
	@Transactional
	public void cancelOrder(Long orderId) {
		//주문 엔티티 조회
		Order order = orderRepository.findOne(orderId);
		//주문 취소
		order.cancel(); //this.setStatus(OrderStatus.CANCEL);이러케 데이터만 바꾸고 따로 디비에 업데이트 안날려주는데(엔티티매니저.update나merge가튼거) 바뀐다. 왜냐면 jpa가 트랜잭션 커밋시점에 바뀐애 찾아서 디비 업데이트문 날리고 트랜잭션커밋한다. 플러시할때 더티체킹해서
	}
	
//	참고: 주문 서비스의 주문과 주문 취소 메서드를 보면 비즈니스 로직 대부분이 엔티티에 있다. 서비스 계층
//	은 단순히 엔티티에 필요한 요청을 위임하는 역할을 한다. 이처럼 엔티티가 비즈니스 로직을 가지고 객체 지
//	향의 특성을 적극 활용하는 것을 도메인 모델 패턴(http://martinfowler.com/eaaCatalog/
//	domainModel.html)이라 한다. 반대로 엔티티에는 비즈니스 로직이 거의 없고 서비스 계층에서 대부분
//	의 비즈니스 로직을 처리하는 것을 트랜잭션 스크립트 패턴(http://martinfowler.com/eaaCatalog/
//	transactionScript.html)이라 한다
	
	// 검색
	//이런거는걍 컨트롤러에서 레포지토리 바로불러도됨
	public List<Order> findOrders(OrderSearch orderSearch){
		//return orderRepository.findAllByCriteria(orderSearch);
		return orderRepository.findAll(orderSearch);
	}
}
