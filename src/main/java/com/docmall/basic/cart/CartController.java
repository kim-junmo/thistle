package com.docmall.basic.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.commom.util.FileManagerUtils;
import com.docmall.basic.user.UserVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RequestMapping("/cart/*")
@Controller
@Slf4j
public class CartController {
	
	private final CartService cartService;
	
	//상품 이미지 경로
	@Value("${file.product.image.dir}")
	private String uploadPath;
	
	//장바구니 추가
	@GetMapping("/cart_add")
	public ResponseEntity<String> cart_add(CartVO vo, HttpSession session) throws Exception {
		
		log.info("장바구니 : " + vo);
		
		//아이디 확보하는 코드
		String user_id = ((UserVO) session.getAttribute("login_status")).getUser_id();
		vo.setUser_id(user_id);
		
		ResponseEntity<String> entity = null;
		
		//db연동 작업
		cartService.cart_add(vo);
		
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
	
	//장바구니 목록
	@GetMapping("/cart_list")
	public void cart_listForm(HttpSession session, Model model) throws Exception {
		
		//아이디 확보하는 코드
		String user_id = ((UserVO) session.getAttribute("login_status")).getUser_id();
		
		List<CartProductVO> cart_list = cartService.cart_list(user_id);
		cart_list.forEach(vo -> vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/")));
		
		model.addAttribute("cart_list", cart_list);
	}
	
	//이미지 추가
	@GetMapping("/image_display")
	public ResponseEntity<byte[]> image_display(String dateFolderName, String fileName) throws Exception {
		
		return FileManagerUtils.getFile(uploadPath + dateFolderName, fileName);
	}
	
	//장바구니 삭제
	@GetMapping("/cart_del")
	public String cart_del(Long cart_code) throws Exception {
		
		cartService.cart_del(cart_code);
		
		return "redirect:/cart/cart_list";
	}
	
	//수량변경
	@GetMapping("/cart_change")
	public String cart_change(Long cart_code, int cart_amount) throws Exception {
		
		cartService.cart_change(cart_code, cart_amount);
		
		return "redirect:/cart/cart_list";
	}
	
	// 장바구니 비우기
	@GetMapping("/cart_empty")
	public String cart_empty(HttpSession session) throws Exception {
		
		String user_id = ((UserVO) session.getAttribute("login_status")).getUser_id();
		
		cartService.cart_empty(user_id);
		
		return "redirect:/cart/cart_list";
	}

}
