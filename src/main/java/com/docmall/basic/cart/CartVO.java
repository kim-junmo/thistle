package com.docmall.basic.cart;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartVO {
	
	//cart_tbl
	//cart_tbl, cart_code, pro_num, user_id, cart_amount, cart_date
	private long cart_code;
	private int pro_num;
	private String user_id;
	private int cart_amount;
	private Date cart_date; //carlendar, localDate, LocalTime, LocalDateTime

}
