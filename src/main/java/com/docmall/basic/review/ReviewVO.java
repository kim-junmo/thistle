package com.docmall.basic.review;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewVO {

	// re_code, user_id, pro_num, re_title, re_content, re_rate, re_date
	private Long re_code;
	private String user_id;
	private int pro_num;
	private String re_title;
	private String re_content;
	private int re_rate;
	private Date re_date;
}
