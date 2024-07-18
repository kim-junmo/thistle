package com.docmall.basic.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.docmall.basic.cart.CartMapper;
import com.docmall.basic.payinfo.PayInfoMapper;
import com.docmall.basic.payinfo.PayInfoVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

	private final OrderMapper orderMapper;
	private final PayInfoMapper payInfoMapper;
	private final CartMapper cartMapper;
	
	//카카오 페이 주문정보 트래지션에 사용하기 위해 만듬. 
	// 1) 주문테이블(insert) 2) 주문 상세테이블(insert) 3) 결제 테이블(insert) 4) 장바구니 테이블(delete)
	@Transactional //데이터베이스에서 작업하는 것은 복잡해지기 때문에 여기서 작업하게 된다.
	public void order_process(OrderVO vo, String user_id, String paymethod, String p_status, String payinfo) {
		
		// 1) 주문테이블(insert)
		vo.setUser_id(user_id);
		orderMapper.order_insert(vo);
		
		// 2) 주문 상세테이블(insert)
		orderMapper.orderDetail_insert(vo.getOrd_code(), user_id);
		
		// 3) 결제 테이블(insert) 이 작업을 하기 위해 payinfoMapper, Service작업을 함.
		PayInfoVO p_vo = PayInfoVO.builder()
				.ord_code(vo.getOrd_code())
				.user_id(user_id)
				.p_price(vo.getOrd_price())
				.paymethod(paymethod)
				.payinfo(payinfo)
				.p_status(p_status)
				.build();
		
		payInfoMapper.payinfo_insert(p_vo);
		
		// 4) 장바구니 테이블(delete)
		cartMapper.cart_empty(user_id);
	}
}
