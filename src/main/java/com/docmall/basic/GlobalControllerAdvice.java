package com.docmall.basic;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.docmall.basic.admin.category.AdminCategoryService;
import com.docmall.basic.admin.category.CategoryVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
//카테고리가 사용되는 컨트롤러의 패키지를 설정. 모든 컨트롤러에서 발생되는 예외를 처리하는 목적, 공통적으로 사용되는 코드를 한번에 처리
//가본페이지인 com.docmall.basic이걸 넣으면 모든 패키지에 적용이 되어(전체에 영향) 성능이 저하가 된다.
@ControllerAdvice(basePackages = {"com.docmall.basic.product", "com.docmall.basic.cart", "com.docmall.basic.order"})  // 카테고리가 사용되는 컨트롤러의 패키지를 설정.
public class GlobalControllerAdvice {
	
	private final AdminCategoryService adminCategoryService;

	@ModelAttribute
	public void comm_test(Model model) {
		log.info("공통코드 실행");
		List<CategoryVO> cate_list = adminCategoryService.getFirstCategoryList();
		model.addAttribute("user_cate_list", cate_list);
		
	}
}
