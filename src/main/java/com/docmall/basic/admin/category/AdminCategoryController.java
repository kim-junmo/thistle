package com.docmall.basic.admin.category;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/category/*")
@Controller
public class AdminCategoryController {

	private final AdminCategoryService adminCategoryService;
	
	//2차 카테고리 목록
	@GetMapping("/secondcategory/{cate_prtcode}")
	public ResponseEntity<List<CategoryVO>> getSecondCategoryList(@PathVariable("cate_prtcode") int cate_prtcode) throws Exception {
		
		log.info("1차 카테고리 : " + cate_prtcode);
		
		ResponseEntity<List<CategoryVO>> entity = null;
		
		entity = new ResponseEntity<List<CategoryVO>>(adminCategoryService.getsecondCategoryList(cate_prtcode), HttpStatus.OK);
				
		return entity;
	}
}
