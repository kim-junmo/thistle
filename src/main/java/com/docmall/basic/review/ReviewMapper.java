package com.docmall.basic.review;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.docmall.basic.commom.dto.Criteria;


public interface ReviewMapper {
	
	List<ReviewVO> rev_list(@Param("pro_num") Integer pro_num, @Param("cri") Criteria cri);
	
	int getCountReviewByPro_num(Integer pro_num);
	
	void review_save(ReviewVO vo);
	
	ReviewVO review_modify(Long re_code);
	
	void review_update(ReviewVO vo);
	
	void review_delete(Long re_code);
}
