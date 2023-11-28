package jpabook.jpashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HomeController {
	
	//Logger log = LoggerFactory.getLogger(getClass()); // 이거대신 @Slf4j

	@RequestMapping("/")
	public String home() {
		log.info("home controller");
		return "home"; //home.html연결 src/main/resources/templates
	}
}
