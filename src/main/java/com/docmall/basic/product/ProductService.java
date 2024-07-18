package com.docmall.basic.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.docmall.basic.admin.product.ProductVO;
import com.docmall.basic.commom.dto.Criteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {

	private final ProductMapper productMapper;
	
	public List<ProductVO> pro_list(int cate_code, Criteria cri) {
		return productMapper.pro_list(cate_code, cri); // 반환값을 명시적으로 반환
	}
	
	public int getCountProductByCategory(int cate_code) {
		return productMapper.getCountProductByCategory(cate_code);
	}
	
	public ProductVO pro_info(int pro_num) {
		return productMapper.pro_info(pro_num);
	}
}