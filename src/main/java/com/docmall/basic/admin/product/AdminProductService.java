package com.docmall.basic.admin.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.docmall.basic.commom.dto.Criteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminProductService {
	
	private final AdminProductMapper adminProductMapper;
	
	public void pro_insert(ProductVO vo) {
		adminProductMapper.pro_insert(vo);
	}
	
	public List<ProductVO> pro_list(Criteria cri) {
		try {
	        log.info("Criteria in pro_list: {}", cri);
	        return adminProductMapper.pro_list(cri);
	    } catch (Exception e) {
	        log.error("Error in pro_list: ", e);
	        throw e;
	    }
	}
	
	int getTotalCount(Criteria cri) {
		return adminProductMapper.getTotalCount(cri);
	}
	
	public ProductVO pro_edit(Integer pro_num) {
		return adminProductMapper.pro_edit(pro_num);
	}
	
	public void pro_edit_ok(ProductVO vo) {
		adminProductMapper.pro_edit_ok(vo);
	}
	
	public void pro_delete(Integer pro_num) {
		adminProductMapper.pro_delete(pro_num);
	}
	
	//체크된 개수만큼 반복문이 동작이 되어, 커넥션 객체수가 진행이 되기 때문에 성능적으로는 권장할 사항은 아니다.
	//이게 사용자에서 요청되는 작업인 경우에는 많은 사용로자로 인한 동시작업에는 서버에 부화가 발생하기 때문에 성능이 좋지 않다. 
	//관리자에서 요청되는 작업에는 문제가 되지 않는다.
	public void pro_checked_modify1(List<Integer> pro_num_arr, List<Integer> pro_price_arr, List<String> pro_buy_arr) {
		for(int i=0; i<pro_num_arr.size(); i++) {
			adminProductMapper.pro_checked_modify1(pro_num_arr.get(i), pro_price_arr.get(i), pro_buy_arr.get(i));
		}

	}
	
	public void pro_checked_modify2(List<Integer> pro_num_arr, List<Integer> pro_price_arr, List<String> pro_buy_arr) {
		
		List<ProductDTO> pro_modify_list = new ArrayList<>();
		
		for(int i=0; i<pro_num_arr.size(); i++) {
			ProductDTO productDTO = new ProductDTO(pro_num_arr.get(i), pro_price_arr.get(i), pro_buy_arr.get(i));
			pro_modify_list.add(productDTO);
		}
		adminProductMapper.pro_checked_modify2(pro_modify_list);
		
	}

}
