package com.docmall.basic.naverlogin;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.user.SNSUserDTO;
import com.docmall.basic.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RequestMapping("/oauth2")
@Slf4j
@Controller
public class NaverLoginController {
	
	private final NaverLoginService naverLoginService;
	
	private final UserService userService;

	//step1 네이버 로그인 연동 URL 생성
	@GetMapping("/naverlogin")
	public String connect() throws UnsupportedEncodingException {
		
		String url = naverLoginService.getNaverAuthorizerUrl();
		
		return "redirect:" + url.toString();
	}
	
	//step2
	//callback주소 생성 작업. //http://localhost:9090/oauth2/callback/naver
	//API 요청 성공시 : http://콜백URL/redirect?code={code값}&state={state값}
	//API 요청 실패시 : http://콜백URL/redirect?state={state값}&error={에러코드값}&error_description={에러메시지}
	@GetMapping("/callback/naver")
	public String callback(NaverCallback callback, HttpSession session) throws UnsupportedEncodingException, Exception {
		
		if(callback.getError() != null) {
			log.info(callback.getError_description());
		}
		
		//JSON포멧의 응답데이터
		String responseToken = naverLoginService.getNaverTokenUrl(callback);
		
		ObjectMapper objectMapper = new ObjectMapper();
		NaverToken naverToken = objectMapper.readValue(responseToken, NaverToken.class);
		
		log.info("토큰정보 : " + naverToken.toString());
		
		//엑세스토큰을 이용한 사용자정보 받아오기.
		String responseUser = naverLoginService.getNaverUserByToken(naverToken);
		NaverResponse naverResponse = objectMapper.readValue(responseUser, NaverResponse.class);
		
		log.info("사용자 정보 : " + naverResponse.toString());
		
		String sns_email = naverResponse.getResponse().getEmail();
		
		if(naverResponse != null) {
			session.setAttribute("naver_status", naverResponse);
			session.setAttribute("accessToken", naverToken.getAccess_token());
			
			if(userService.existUserInfo(sns_email) == null && userService.sns_user_check(sns_email) == null) {
				
				SNSUserDTO dto = new SNSUserDTO();
				dto.setId(naverResponse.getResponse().getId());
				dto.setEmail(naverResponse.getResponse().getEmail());
				dto.setName(naverResponse.getResponse().getName());
				dto.setSns_type("naver");
				
				userService.sns_user_insert(dto);
			}
		}
		
		return "redirect:/";
	}
	
	@GetMapping("/naverlogout")
	public String naverlogout(HttpSession session) {
		
		String accessToken = (String) session.getAttribute("accessToken");
		
		naverLoginService.getNaverTokenDelete(accessToken);
		
		if(accessToken != null && !"".equals(accessToken)) {
//			try {
//				naverLoginService.naverlogout(accessToken);
//			}catch (JsonProcessingException ex) {
//				throw new RuntimeException(ex);
//			}
			
			
			
			session.removeAttribute("naver_status");
			session.removeAttribute("accessToken");
		}
		
		return "redirect:/";
	}
}
