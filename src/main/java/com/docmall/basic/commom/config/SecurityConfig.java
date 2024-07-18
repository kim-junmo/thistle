package com.docmall.basic.commom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //설정목적으로 사용하는 클래스는 @Configuration를 사용해야 한다.
//@EnableWebSecurity
public class SecurityConfig {
	
	//스프링 시큐리티 설정과 관계된 bean. v2.7과 v.3.x버전과 차이가 있음.
//	@Bean
//	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http
//						.csrf((csrf) -> csrf.disable());
//						.cors((c) -> c.disable());
//						.header((headers) -> headers.disable());
//			return http.build();
//	}
//	
	// 암호화 기능 bean을 등록(생성) passwordEncoder bean 자동생성.
	//PasswordEncoder 인터페이스 BCryptPasswordEncoder구현클래스.
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


}
