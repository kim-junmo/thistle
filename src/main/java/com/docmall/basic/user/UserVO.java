package com.docmall.basic.user;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//성능 관점으로 회원테이블(로그인 포함)/ 회원테이블 + 로그인 분리하여 작업하는 경우도 있다.
//user_id, user_password, user_name, user_zipcode, user_addr, user_deaddr, user_phone, user_email, user_receive, user_point,  user_lastlogin, user_datesub,user_updatedate
@Getter
@Setter
@ToString
public class UserVO {

    private String user_id;
    private String user_password;
    private String user_name;
    private String user_zipcode;
    private String user_addr;
    private String user_deaddr;
    private String user_phone;
    private String user_email;
    private String user_receive;
    private int user_point;
    private Date user_lastlogin;
    private Date user_datesub;
    private Date user_updatedate;
	
}
