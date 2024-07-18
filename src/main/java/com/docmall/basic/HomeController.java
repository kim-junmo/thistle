package com.docmall.basic;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.docmall.basic.admin.category.AdminCategoryService;
import com.docmall.basic.admin.category.CategoryVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Controller
public class HomeController {
	
	private final AdminCategoryService adminCategoryService;

	//@ResponseBody // 이 어노테이션이 사용 안되면,return "index";는 데이터가 아니라, 타임리프 파일명으로 인식. 
	@GetMapping("/")
	public String index(Model model) {
		log.info("기본주소");
		List<CategoryVO> cate_list = adminCategoryService.getFirstCategoryList();
		model.addAttribute("user_cate_list", cate_list);
		
		return "index";
	}
}
