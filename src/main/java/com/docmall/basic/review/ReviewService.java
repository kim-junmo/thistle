package com.docmall.basic.review;

import java.util.List;

import org.springframework.stereotype.Service;

import com.docmall.basic.commom.dto.Criteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewService {

	private final ReviewMapper reviewMapper;
	
	public List<ReviewVO> rev_list(Integer pro_num, Criteria cri) {
		return reviewMapper.rev_list(pro_num, cri);
	}
	
	public int getCountReviewByPro_num(Integer pro_num) {
		return reviewMapper.getCountReviewByPro_num(pro_num);
	}
	
	public void review_save(ReviewVO vo) {
		reviewMapper.review_save(vo);
	}
	
	public ReviewVO review_modify(Long re_code) {
		return reviewMapper.review_modify(re_code);
	}
	
	public void review_update(ReviewVO vo) {
		reviewMapper.review_update(vo);
	}
	
	public void review_delete(Long re_code) {
		reviewMapper.review_delete(re_code);
	}
	
}
