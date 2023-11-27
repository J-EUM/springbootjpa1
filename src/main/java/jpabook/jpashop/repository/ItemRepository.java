package jpabook.jpashop.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
	
	private final EntityManager em;
	
	public void save(Item item) {
		if (item.getId() == null) {
			em.persist(item); // 처음에 id 없는거 ->새로등록하는거 jpa가 제공하는 persist
		} else {
			em.merge(item); // id있으면 이미 디비에 있는거 가져온거 ->merge업데이트비슷한거
		}
	}

	public Item findOne(Long id) {
		return em.find(Item.class, id);
	}
	
	public List<Item> findAll() {
		return em.createQuery("select i from Item i", Item.class)
				.getResultList();
	}
}
