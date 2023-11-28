package jpabook.jpashop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_item_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;
	
	private int orderPrice;
	
	private int count;

	//protected OrderItem() {} // new OrderItem() 못하게하는거. createOrderItem으로만 만들수있게
	//@NoArgsConstructor(access = AccessLevel.PROTECTED)이걸로대체가능
	
	//==생성 매서드==//
	public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
		OrderItem orderItem = new OrderItem();
		orderItem.setItem(item);
		orderItem.setOrderPrice(orderPrice);
		orderItem.setCount(count);
		
		item.removeStock(count);
		return orderItem;
	}
	
	
	//==비즈니스 로직==//
	public void cancel() {
		// 아이템 재고수량 원복
		getItem().addStock(count);
		
	}

	//==조회로직==//
	public int getTotalPrice() {
		// TODO Auto-generated method stub
		return getOrderPrice() * getCount();
	}

}
