package jpabook.jpashop.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.*;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;

@SpringBootTest
@Transactional //테스트 하나하나 끝날때마다 롤백
class OrderServiceTest {
	
	@Autowired EntityManager em; // 테스트니까 그냥 바로 persist하려고 바로 가져오기
	@Autowired OrderService orderService;
	@Autowired OrderRepository orderRepository;

	@Test
	public void 상품주문() {
		//given
		Member member = createMember();
		
		Item book = createBook("시골JPA", 10000, 10);
		
		int orderCount = 2;
		
		//when
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
		
		//then
		Order getOrder = orderRepository.findOne(orderId);
		
		assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
		assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다");
		assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
		assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
	}
	
	@Test //(expected = NotEnoughStockException.class)// junit4
	public void 상품주문_재고수량초과() throws Exception {
		//given
		Member member = createMember();
		Item item = createBook("시골JPA", 10000, 10);
		
		int orderCount = 11;
		
		//when
		//orderService.order(member.getId(), item.getId(), orderCount);		
		
		//then
		//fail("재고 수량 부족 예외가 발생해야 한다."); // 위에서 exception안터지고 여기로 내려오면 안된다는뜻
		assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), item.getId(), orderCount));
		
	} // 이런 통합테스트보다는 item.removeStock(count) 에 대한 단위테스트를 하는게 더 좋다

	@Test
	public void 주문취소() {
		//given
		Member member = createMember();
		Item item = createBook("시골JPA", 10000, 10);
		
		int orderCount = 2;
		
		Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
		
		//when
		orderService.cancelOrder(orderId);
		
		//then
		Order getOrder = orderRepository.findOne(orderId);
		
		assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소시 상태는 CANCEL이다.");
		assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 재고가 원복되어야 한다.");
	}
	
	private Member createMember() {
		Member member = new Member();
		member.setName("회원1");
		member.setAddress(new Address("서울", "경기", "12345"));
		em.persist(member);
		return member;
	}
	
	private Book createBook(String name, int price, int stockQuantity) {
		Book book = new Book();
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(stockQuantity);
		em.persist(book);
		
		return book;
	}
	
}
