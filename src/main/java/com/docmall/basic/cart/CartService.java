package com.docmall.basic.cart;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class CartService {

	private final CartMapper cartMapper;
	
	public void cart_add(CartVO vo) {
		cartMapper.cart_add(vo);
	}
	
	public List<CartProductVO> cart_list(String user_id) {
		return cartMapper.cart_list(user_id);
	}
	
	public void cart_del(Long cart_code) {
		cartMapper.cart_del(cart_code);
	}
	
	public void cart_change(Long cart_code, int cart_amount) {
		cartMapper.cart_change(cart_code, cart_amount);
	}
	
	public void cart_empty(String user_id) {
		cartMapper.cart_empty(user_id);
	}
}
