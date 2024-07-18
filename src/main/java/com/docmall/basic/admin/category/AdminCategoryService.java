package com.docmall.basic.admin.category;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminCategoryService {
	
	private final AdminCategoryMapper adminCategoryMapper;
	
	public List<CategoryVO> getFirstCategoryList() {
		return adminCategoryMapper.getFirstCategoryList();
	}
	
	//2차 카테고리 작업
	public List<CategoryVO> getsecondCategoryList(int cate_prtcode) {
		return adminCategoryMapper.getsecondCategoryList(cate_prtcode);
	}
	
	public CategoryVO getFirstCategoryBySecondCategory(int cate_code) {
		return adminCategoryMapper.getFirstCategoryBySecondCategory(cate_code);
	}
	

}
