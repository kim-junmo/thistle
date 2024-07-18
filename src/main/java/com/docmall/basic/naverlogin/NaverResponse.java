package com.docmall.basic.naverlogin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NaverResponse {
	
	private String resultcode;
	private String message;
	
	Response response;

}
