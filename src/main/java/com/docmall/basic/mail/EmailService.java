package com.docmall.basic.mail;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.docmall.basic.commom.constants.Constants;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailService {

	// EmailConfig.java파일의 javaMailSender()메서드가 스프링시스템에서 실행되어, 리턴되는 타입의 객체. 
	// 즉 bean을 생성및등록작업을 하고, 아래 객체에 주입을 해준다.
	private final JavaMailSender mailSender;
	
	//뷰 템플릿 중 타임리프 템플릿을 메일 템플릿으로 사용하기 위해 아래 필드가 선언된다.
	private final SpringTemplateEngine templateEngine;
	

	public void sendMail(String type, EmailDTO dto, String authcode) {
		
		// mail/파일이름(templates/mail/파일명)
		type = Constants.MAILFOLDERNAME + "/" + type;
		
		//메일구성정보 담당(받는사람, 보내는 사람, 받는사람 메일주소, 본문내용)
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		
		try {
//			//받는사람의 메일주소
//			mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(dto.getReceiverMail()));
//			//보내는 사람(메일, 이름)
//			mimeMessage.addFrom(new InternetAddress[] {new InternetAddress(dto.getSenderMail(), dto.getSenderName())});
//			//제목
//			mimeMessage.setSubject(dto.getSubject(), "utf-8");
//			//본문내용
//			mimeMessage.setText(authcode, "utf-8");
//			
//			System.out.println("메일객체" + mailSender);
			
			//타임리프를 사용했을 때. 아래 코드가 진행이 된다., 메일 템플릿으로 타임리프 사용목적으로 아래 코드가 구성됨.
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(dto.getReceiverMail()); //메일 수신자
			mimeMessageHelper.setFrom(new InternetAddress(dto.getSenderMail(), dto.getSenderName()));
			mimeMessageHelper.setSubject(dto.getSubject()); //메일 제목
			mimeMessageHelper.setText(setContext(authcode, type), true); //메일 본문 내용, HTML 여부, type은 email.html을 가리킴.
			
			
			// 메일발송기능, send를 진행하기 위해 mimeMessage를 호출하였다.
			mailSender.send(mimeMessage);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//임시번호 및 임시 비밀번호 생성 메서드
	//public String createCode() {}
	
	
	//thymeleaf를 통한 html 적용
	// String authcode: 인증코드, String type: email.html
	//"authcode" : email.html 안에 들어있는 ${"authcode"}코드이다.
	public String setContext(String authcode, String type) {
		Context context = new Context();
		context.setVariable("authcode", authcode);
		
		return templateEngine.process(type, context);
	}

}
