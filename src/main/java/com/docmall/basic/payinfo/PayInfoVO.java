package com.docmall.basic.payinfo;

import java.sql.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class PayInfoVO {
	//p_id, ord_code, user_id, paymethod, payinfo, p_price, p_status, p_date
	private Integer p_id;
	private Long ord_code;
	private String user_id;
	private String paymethod;
	private String payinfo;
	private int p_price;
	private String p_status;
	private Date p_date;

}
