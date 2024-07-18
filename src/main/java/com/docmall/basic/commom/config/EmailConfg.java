package com.docmall.basic.commom.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j // 로그출력
@Configuration // bean등록작업
@PropertySource("classpath:mail/email.properties")
public class EmailConfg {

	public EmailConfg() throws Exception {
		log.info("EmailConfg.java constructor called.");
	}
	
	// email.properties파일의 설정정보를 참조
	@Value("${spring.mail.transport.protocol}")
	private String protocol;  // smtp
	
	@Value("${spring.mail.debug}")
	private boolean debug;
	
	@Value("${spring.mail.properties.mail.smtp.auth}")
	private boolean auth;
	
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private boolean starttls;
	

	
	@Value("${spring.mail.host}")
	private String host;
	
	@Value("${spring.mail.port}")
	private int port;
	
	@Value("${spring.mail.username}")
	private String username;
	
	@Value("${spring.mail.password}")
	private String password;
	
	@Value("${spring.mail.default-encoding}")
	private String encoding;
	
	@Bean // JavaMailSender : 스프링에서 메일발송하는 객체.  javaMailSender bean 생성 및 스프링컨테이너에 등록
	// bean의 목적은 의존성주입(DI)
	public JavaMailSender javaMailSender() {
		
//		log.info("host" + host);
		
		// JavaMailSenderImpl클래스가 어떤 메일서버를 이용하여 메일발송할지 서버에 대한 정보를 구성하는 작업
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		
		Properties properties = new Properties();  // https://dev-cini.tistory.com/82#google_vignette
		
		//추가
		properties.put("mail.smtp.auth", auth);
		properties.put("mail.smtp.starttls.enable", starttls);
		
//		properties.setProperty("mail.smtp.ssl.trust","smtp.mailplug.co.kr"); // ssl 인증 서버 주소
//      properties.setProperty("mail.smtp.ssl.enable","true"); // ssl 사용

		mailSender.setHost(host);
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		mailSender.setPort(port);
		mailSender.setJavaMailProperties(properties);
		mailSender.setDefaultEncoding(encoding);

		log.info("메일서버: " + host);
		return mailSender;
	}
}
