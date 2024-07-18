package com.docmall.basic.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartProductVO {

	private long cart_code;
	private int pro_num;
	private String pro_name;
	private String pro_up_folder;
	private String pro_img;
	private int pro_price;
	private int cart_amount;
}
