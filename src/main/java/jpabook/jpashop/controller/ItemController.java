package jpabook.jpashop.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;
	
	@GetMapping("/items/new")
	public String createForm(Model model) {
		model.addAttribute("form", new BookForm());
		return "items/createItemForm";
	}
	
	@PostMapping("/items/new")
	public String create(BookForm form) {
		
		Book book = new Book();
		// set...말고 걍 createBook같은거 하나 만들어서 파라미터넘기는게더나은설계. setter제거하는게좋은거 order처럼
		book.setName(form.getName());
		book.setPrice(form.getPrice());
		book.setStockQuantity(form.getStockQuantity());
		book.setAuthor(form.getAuthor());
		book.setIsbn(form.getIsbn());
		
		itemService.saveItem(book);
		return "redirect:/items";
	}
	
	@GetMapping("/items")
	public String list(Model model) {
		List<Item> items = itemService.findItems();
		model.addAttribute("items", items);
		return "items/itemList";
	}
	
	@GetMapping("items/{itemId}/edit")
	public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
		Book item = (Book) itemService.findOne(itemId); //(Book)캐스팅하는거 별로 좋지 않은데 예제단순화 위해 걍 씀
		
		BookForm form = new BookForm(); // 업데이트할때 북엔티티 말고 폼을 보낼거다
		form.setId(item.getId());
		form.setName(item.getName());
		form.setPrice(item.getPrice());
		form.setStockQuantity(item.getStockQuantity());
		form.setAuthor(item.getAuthor());
		form.setIsbn(item.getIsbn());
		
		model.addAttribute("form", form);
		return "items/updateItemForm";
	}
	
	@PostMapping("items/{itemId}/edit")
	public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form) { //@ModelAttribute
//		준영속 엔티티?
//		영속성 컨텍스트가 더는 관리하지 않는 엔티티를 말한다.
//		(여기서는 itemService.saveItem(book) 에서 수정을 시도하는 Book 객체다. Book 객체는 이미 DB
//		에 한번 저장되어서 식별자가 존재한다. 이렇게 임의로 만들어낸 엔티티도 기존 식별자를 가지고 있으면 준
//		영속 엔티티로 볼 수 있다.)
		// 준영속엔티티 문제 jpa가관리안한다. 영속엔티티는 변경감지함 jpa가 머변경됐는지 알고 트랜잭션커밋때 바꿀수있는데
		// 밑에 book은 내가걍 new로만든거라 jpa가관리안한다 그래서 걍 set해서바꺼노면 디비업데이트안된다
//		Book book = new Book();
//		book.setId(form.getId()); //이거 아이디 체크하는거 유저가 이 아이템에대한 권한이 있는지 체크하는거 서비스나 어딘가에 있어야함 원래
//		book.setName(form.getName());
//		book.setPrice(form.getPrice());
//		book.setStockQuantity(form.getStockQuantity());
//		book.setAuthor(form.getAuthor());
//		book.setIsbn(form.getIsbn());
//		itemService.saveItem(book);
		//컨트롤러에서 어설프게 엔티티를 생성하지 마세요
		// 필요한것만 보내라
		// 필요한거 너무 많으면 걍 UpdateItemDto가튼거 하나 만들어라
		itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
		return "redirect:/items";
	}
}
