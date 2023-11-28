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
		
		Book book = new Book();
		book.setId(form.getId()); //이거 아이디 체크하는거 유저가 이 아이템에대한 권한이 있는지 체크하는거 서비스나 어딘가에 있어야함 원래
		book.setName(form.getName());
		book.setPrice(form.getPrice());
		book.setStockQuantity(form.getStockQuantity());
		book.setAuthor(form.getAuthor());
		book.setIsbn(form.getIsbn());
		
		itemService.saveItem(book);
		return "redirect:/items";
	}
}
