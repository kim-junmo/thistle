package com.docmall.basic.kakaopay;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.docmall.basic.cart.CartProductVO;
import com.docmall.basic.cart.CartService;
import com.docmall.basic.order.OrderService;
import com.docmall.basic.order.OrderVO;
import com.docmall.basic.user.UserVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@Slf4j
@RequestMapping("/kakao/*")
@Controller
public class KakaoPayController {

	private final KakaoPayService kakaoPayService;
	private final CartService cartService;
	private final OrderService orderService;
	
	private OrderVO vo;
	private String user_id;
	
	@GetMapping("/kakaopayrequest")
	public void kakaopayrequest() {
		
	}
	
	@ResponseBody
	@GetMapping(value = "/kakaopay")
	public ReadyResponse kakaopay(OrderVO vo, HttpSession session) {
		
		log.info("주문자 정보1 : " + vo);
		
		//1) 결제 준비 요청(ready)
		// ready(String partnerOrderId, String partnerUserId, String itemName, int quantity,
		//  int totalAmount, int taxFreeAmount, int vatAmount
		
		String user_id = ((UserVO) session.getAttribute("login_status")).getUser_id();
		this.user_id = user_id;
		
		List<CartProductVO> cart_list = cartService.cart_list(user_id);
		
		String itemName = "";
		int quantity = 0;
		int totalAmount = 0;
		int taxFreeAmount = 0;
		int vatAmount = 0;
		
		for(int i=0; i < cart_list.size(); i++) {
			itemName += cart_list.get(i).getPro_name() + ", ";
			quantity += cart_list.get(i).getCart_amount();
			totalAmount += cart_list.get(i).getPro_price() * cart_list.get(i).getCart_amount();
			
		}
		
		String partnerOrderId = user_id;
		String partnerUserId = user_id;
				
		//1)카카오 페이 결제 준비 요청
		ReadyResponse readyResponse = kakaoPayService.ready(partnerOrderId, partnerUserId, itemName, 
				quantity, totalAmount, taxFreeAmount, vatAmount);
		
		log.info("응답 데이터 : " + readyResponse);
		
		//주문정보
		this.vo = vo;
		
		return readyResponse;
	}
	
	//성공
	@GetMapping("approval")
	public void approval(String pg_token) {
		log.info("pg_token : " + pg_token);
		
		//2) 결제 승인 요청
		String approveResponse = kakaoPayService.approve(pg_token);
		log.info("최종결과 : " + approveResponse);
		
		//주문 정보 저장작업.
		//aid값이 존재하면 
		
		//트랜잭션으로 처리 : 주문테이블, 주문 상세테이블, 결제테이블 ,장바구니 비우기
		//하나의 작업 안에서 여러 db 작업이 들어가는것, 하나의 문제가 발생 시 전체 중단. 
		if(approveResponse.contains("aid")) {
			log.info("주문자 정보2 : " + vo);
			orderService.order_process(vo, user_id, "kakaopay", "결제완료", "kakaopay");
		}
		
	}
	
	//취소
	@GetMapping("/cancel")
	public void cancel() {
		
	}
	
	//실패
	@GetMapping("/fail")
	public void fail() {
		
	}
	
}
