package jpabook.jpashop.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // createOrder로만 생성, new Order();금지
public class Order {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id") // 멤버-오더 에서 주인 외래키있는 오더. 여기가만히놔두고 멤버에 오더리스트에다가 mappedBy="member"
	private Member member;

	@BatchSize(size = 100) //개별적용. xToOne관계일때는 클래스에다가적어야됨
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // orderItems에다가 데이터 넣고 order저장하면 oder_item도 같이 저장(삭제할때도)
	private List<OrderItem> orderItems = new ArrayList<>();
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // 주문->배송으로 타고들어갈일이 많음녀 주문에다가 외래키 두고 얘가 주인
	@JoinColumn(name = "delivery_id")
	private Delivery delivery;
	
	private LocalDateTime orderDate;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status; // 주문,캔슬
	
	//==연관관계메서드==//
	// 해노면 예를들어서
	// 	main(){
	//		Member member = new Member();
	//		Order order = new Order();
	//	
			// 원래 밑에 두줄 다 해야되는데
	//		//member.getOrders().add(order); // 이거없어도됨
	//		order.setMember(member);		// 이것만하면됨
	// }
	public void setMember(Member member) {
		this.member = member;
		member.getOrders().add(this);
	}
	
	public void addOrderItem(OrderItem orderItem) {
		orderItems.add(orderItem);
		orderItem.setOrder(this);
	}
	
	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
		delivery.setOrder(this);
	}
	
	
	//==생성메서드==/
	// 주문하면 오더-오더아이템-딜리버리 다만들어야되니까
	// 나중에 주문생성에 변경할거있으면 이거 하나만 바꾸면 된다
	// 하나하나set하지말고 createOrder에서 한번에
	public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
		Order order = new Order();
		order.setMember(member);
		order.setDelivery(delivery);
		for(OrderItem orderItem : orderItems) {
			order.addOrderItem(orderItem);
		}
		order.setStatus(OrderStatus.ORDER);
		order.setOrderDate(LocalDateTime.now());
		return order;
	}
	
	//==비즈니스로직==//
	/**
	 * 주문취소
	 */
	public void cancel() {
		if (delivery.getStatus() == DeliveryStatus.COMP) {
			throw new IllegalStateException("이미 배송완료된 상품은 취소 불가능");
		}
		
		this.setStatus(OrderStatus.CANCEL);
		for (OrderItem orderItem : orderItems) {
			orderItem.cancel(); // 여러개 주문하고 하나만 취소할수있따
		}
	}
	
	//==조회로직==//
	/**
	 * 전체주문가격조회
	 * @return
	 */
	public int getTotalPrice() {
		int totalPrice = 0;
		for(OrderItem orderItem : orderItems) {
			totalPrice += orderItem.getTotalPrice();
		}
		return totalPrice;
		
		// 한줄
		// return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
	}
	
}
