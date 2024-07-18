package com.docmall.basic.admin.order;

import java.util.List;

import org.springframework.stereotype.Service;

import com.docmall.basic.admin.product.ProductVO;
import com.docmall.basic.commom.dto.Criteria;
import com.docmall.basic.order.OrderVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminOrderService {

	private final AdminOrderMapper adminOrderMapper;
	
	public List<OrderVO> order_list(Criteria cri) {
		return adminOrderMapper.order_list(cri);
	}
	
	public int getTotalCount(Criteria cri) {
		return adminOrderMapper.getTotalCount(cri);
	}
	
	public OrderVO order_info(Long ord_code) {
		return adminOrderMapper.order_info(ord_code);
	}
}
