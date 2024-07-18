package com.docmall.basic.admin.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.commom.dto.Criteria;
import com.docmall.basic.commom.dto.PageDTO;
import com.docmall.basic.order.OrderVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin/order/*")
@RequiredArgsConstructor
@Controller
public class AdminOrderController {
	
	private final AdminOrderService adminOrderService;
	
	//상품 이미지 경로
	@Value("${file.product.image.dir}")
	private String uploadPath;
	
	
	@GetMapping("/order_list")
	public void order_listForm(Criteria cri, Model model) throws Exception {
		
		cri.setAmount(2);
		List<OrderVO> order_list = adminOrderService.order_list(cri);
		
		int totalCount = adminOrderService.getTotalCount(cri);
		
		log.info("pagedto" + new PageDTO(cri, totalCount));
		
		model.addAttribute("order_list", order_list);
		model.addAttribute("pageMaker", new PageDTO(cri, totalCount));
	}
	
	//주문상세정보
	@GetMapping("/order_detail_info")
	public void order_detail_info(Long ord_code, Model model) throws Exception {
		
		//주문자(수령인) 정보
		OrderVO vo = adminOrderService.order_info(ord_code);
		
		//주문상품 정보
		
		//결제정보
		
	}

}
