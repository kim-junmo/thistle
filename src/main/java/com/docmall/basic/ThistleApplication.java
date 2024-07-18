package com.docmall.basic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//com.docmall.basic 이 안에 들어가 있는 모든 인터페이스를 mapper로 참고하기 때문에 
//service를 지우고 serviceImpl이름을 service으로 수정함.                                                                                                                                                                           
@MapperScan(basePackages = "com.docmall.basic.**")
//exclude = SecurityAutoConfiguration.class 스프링 시큐리트 적용을 하지 못하게 만듬
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ThistleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThistleApplication.class, args);
	}

}
