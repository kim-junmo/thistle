package com.docmall.basic.admin.order;

import java.util.List;

import com.docmall.basic.commom.dto.Criteria;
import com.docmall.basic.order.OrderVO;

public interface AdminOrderMapper {
	
	List<OrderVO> order_list(Criteria cri);
	
	int getTotalCount(Criteria cri);
	
	OrderVO order_info(Long ord_code);
	
	List<OrderDetailInfoVO> order_detail_info(Long ord_code);

}
