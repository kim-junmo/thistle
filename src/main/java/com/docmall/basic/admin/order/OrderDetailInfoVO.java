package com.docmall.basic.admin.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class OrderDetailInfoVO {
	//ot.ord_code, ot.pro_num, ot.dt_price, ot.dt_amount, p.pro_name, p.pro_up_folder, p.pro_img
	
	private Long ord_code;
	private Integer pro_num;
	private int dt_amount;
	private int dt_price;
	private String pro_name;
	private String pro_up_folder;
	private String pro_img;
	

}
