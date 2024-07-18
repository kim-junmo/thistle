package com.docmall.basic.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.docmall.basic.admin.product.ProductVO;
import com.docmall.basic.commom.dto.Criteria;

public interface ProductMapper {
	
	List<ProductVO> pro_list(@Param("cate_code") int cate_code, @Param("cri") Criteria cri);
	
	int getCountProductByCategory(int cate_code);
	
	// 상품 팝업및 상세설명
	ProductVO pro_info(int pro_num);

}
