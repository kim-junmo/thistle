package com.docmall.basic.mail;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/email/*")
public class EmailController {
	
	private final EmailService emailService;
	
	// 스프링이 밑에작업을 자동으로 처리해준다.
	// EmailDTO dto = new EmailDTO();
	// dto.setReceiverMail("입력한 메일주소");
	@GetMapping("/authcode")
	public ResponseEntity<String> authcode(String type, EmailDTO dto, HttpSession session) {
		
		log.info("dto:" + dto); // dto.toString()호출
		
		ResponseEntity<String> entity = null;
		
		// 인증코드생성
		String authcode = "";
		for(int i=0; i<6; i++) {
			authcode += String.valueOf((int)(Math.random() * 10));
		}
		
		log.info("인증코드: " + authcode);
		
		//사용자가 자신의 메일에서 발급받은 인증코드를 읽고, 회원가입시 인증확인란에 입력을 하게되면, 서버에서 비교목적으로 세션방식으로 인증코드를 메모리에 저장해두어야 한다.
		session.setAttribute("authcode", authcode); // 톰캣서버내장  세션 30분.
		
		try {
			// 메일발송.
			
			//메일제목 작업
			if(type.equals("emailJoin")) {
				dto.setSubject("DocMall 회원가입 메일인증코드입니다.");
				dto.setMessage("메일 인증코드를 확인하시고, 회원가입시 인증코드 입력란에 입력바랍니다.");
			}else if(type.equals("emailID")) {
				dto.setSubject("DocMall 아이디 메일인증코드입니다.");
				dto.setMessage("아이디 인증코드를 확인하시고, 아이디 찾기 인증코드 입력란에 입력바랍니다.");
			}else if(type.equals("emailPw")) {
				dto.setSubject("DocMall 비밀번호 메일인증코드입니다.");
				dto.setMessage("비밀번호 인증코드를 확인하시고, 비밀번호 찾기 인증코드 입력란에 입력바랍니다.");
			}
			
			emailService.sendMail(type, dto, authcode);
			
			
			entity = new ResponseEntity<String>("success", HttpStatus.OK); // 200
//			log.info("메일발송");
		}catch(Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR); // 500
		}
		
		
		return entity;
	}
	
	//인증확인
	@GetMapping("/confirm_authcode")
	public ResponseEntity<String> confirm_authcode(String authcode, HttpSession session) {
		
		ResponseEntity<String> entity = null;
		
		
		//session.setAttribute("authcode", authcode);와 일치가 되어 있어야 한다.
		// 세션이 유지되고 있는 동안
		if(session.getAttribute("authcode") != null) {
		
			if(authcode.equals(session.getAttribute("authcode"))) {
				entity = new ResponseEntity<String>("success", HttpStatus.OK);
				session.removeAttribute("authcode");
			}else {
				entity = new ResponseEntity<String>("fail", HttpStatus.OK);
			}
		}else { // 세션이 소멸되었을 경우
			entity = new ResponseEntity<String>("request", HttpStatus.OK);
		}
		
		return entity;
		
	}

}
