package jpabook.jpashop.domain.item;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 
@DiscriminatorColumn(name = "dtype")
@Getter @Setter // 이거 재고 변경할일있으면 세터로 할게아니라 addStock, removeStock처럼 안에서 ->객체지향
public abstract class Item {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_id")
	private Long id;
	
	private String name;
	
	private int price;
	
	private int stockQuantity;
	
	@ManyToMany(mappedBy = "items")
	private List<Category> categories = new ArrayList<>();
	
	// 아이템->오더아이템 타고갈일없어서 걍 안해줌
//	@OneToMany(mappedBy = "item")
//	private List<Order> orderItems = new ArrayList<>();
	
	
	//==비즈니스로직==/
	// 도메인주도설계 엔티티자체가해결할수있는것들
	// stockQuantity가지고있으니까~ 데이터를가지고있는쪽에 매서드있는거 좋다 응집력있다
	/**
	 * stock 증가
	 * @param quantity
	 */
	public void addStock(int quantity ) {
		this.stockQuantity += quantity;
	}
	
	/**
	 * stock 감소
	 */
	public void removeStock(int quantity) {
		int restStock = this.stockQuantity - quantity;
		if (restStock < 0) {
			throw new NotEnoughStockException("need more stock");
		}
		this.stockQuantity = restStock;
	}
}
