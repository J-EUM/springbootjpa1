package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	
	@Transactional // 얘는 리드온리하면 안되니까
	public void saveItem(Item item) {
		itemRepository.save(item); //업데이트시 머지
	}
	
//	@Transactional
//	public Item updateItem(Long itemId, Book bookParam) {
//		Item findItem = itemRepository.findOne(itemId); //id로 영속상태엔티티찾아옴
//		findItem.setPrice(bookParam.getPrice());
//		findItem.setName(bookParam.getName());
//		findItem.setStockQuantity(bookParam.getStockQuantity());
//		//itemRepository.save(findItem); 이거해서 엔티티매니저 persist나 merge 호출할 필요가없다
//		//걍끝내면 트랜잭션 커밋하면서 jpa가 플러시 날려서 영속성컨텍스트에있는 엔티티중에 변경된거 찾는다 찾아서 디비 업데이트날림
//		//변경감지
//		//머지말고 Item findItem = itemRepository.findOne(itemId)이걸로 직접조회해서 업데이트할것만 set해야된다
//		//근디사실 set해놓는거보다
//		//findItem.change(price, name, stockQuantity); 이런거 save같은거 엔티티에 만들어놓는게조음 
//		return findItem;
//	}
	
	/**
	 * 영속성 컨텍스트가 자동 변경
	 */
	 @Transactional
	 public void updateItem(Long id, String name, int price, int stockQuantity)	{
		 
		 Item item = itemRepository.findOne(id); //트랜잭션안에서 엔티티조회해야 영속상태로조회되고 거기다가값변경해야 더티체킹
		 item.setName(name);
		 item.setPrice(price);
		 item.setStockQuantity(stockQuantity);
	 }
//	 컨트롤러에서 어설프게 엔티티를 생성하지 마세요.
//	 트랜잭션이 있는 서비스 계층에 식별자( id )와 변경할 데이터를 명확하게 전달하세요.(파라미터 or dto)
//	 트랜잭션이 있는 서비스 계층에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경하세요.
//	 트랜잭션 커밋 시점에 변경 감지가 실행됩니다
	
	public List<Item> findItems() {
		return itemRepository.findAll();
	}
	
	public Item findOne(Long itemId) {
		return itemRepository.findOne(itemId);
	}
}
