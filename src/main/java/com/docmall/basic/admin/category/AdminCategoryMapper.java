package com.docmall.basic.admin.category;

import java.util.List;

public interface AdminCategoryMapper {

	
	//1차 카테고리 작업
	List<CategoryVO> getFirstCategoryList();
	
	//2차 카테고리 목록
	List<CategoryVO> getsecondCategoryList(int cate_prtcode);
	
	//2차 카테고리정보를 이용한 1차 카테고리 정보
	CategoryVO getFirstCategoryBySecondCategory(int cate_code);
}
