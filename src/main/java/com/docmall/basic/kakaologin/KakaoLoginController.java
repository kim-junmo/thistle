package com.docmall.basic.kakaologin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.docmall.basic.user.SNSUserDTO;
import com.docmall.basic.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/oauth2")
public class KakaoLoginController {
	
	private final KakaoLoginService kakaoLoginService;
	
	private final UserService userService;
	
	//Value의 값은 application.properties에서 가지고 왔다. 
	@Value("${kakao.client.id}")
	private String clientId;
	
	@Value("${kakao.redirect.uri}")
	private String redirectUri;
	
	@Value("${kakao.client.secret}")
	private String clientSecret;

	
	//step 1. 카카오 로그인 API Server에게 인가코드를 요청하는 작업
	//헤더는 없고, 요청 파라미터가 있는 경우
	@GetMapping("/kakaologin")
	public String connect() {
		
		//StringBuffer는 String과 유사. 스트링 클래스는 문자열을 추가하면 메모리가 힙 영역에 계속 추가되지만, 
		//StringBuffer는 고정된 메모리에 저장이 된다. (단순한 문자열 추가는 버퍼가 성능이 더 좋음)
		StringBuffer url = new StringBuffer();
		url.append("https://kauth.kakao.com/oauth/authorize?");
		url.append("response_type=code");
		url.append("&client_id=" + clientId);
		url.append("&redirect_uri=" + redirectUri); //http://localhost:9090/oauth2/callback/kakao
		
		//추가옵션. 다시 사용자 로그인 인증 추가 옵션.(qr페이지 나오는 것)
		url.append("&prompt=login");

		log.info("인가코드:" + url.toString());
		
		return "redirect:" + url.toString();
	}

	
	//step2 : 카카오 로그인 API 서버에서 현재 개발 사이트의 CallBack 주소 호출 (redirect.url)
	//카카오개발자 사이트에서 애플리케이션 등록과정에서 아래주소 설정을 이미 한 상태.
	//인가 코드 받기 요청의 응답은 HTTP 302 리다이렉트되어, redirect_uri에 GET 요청으로 전달됩니다
	//String code는 카카오에서 사용하는 파라미터이다. https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-code-response-query
	@GetMapping("/callback/kakao")
	public String callback(String code, HttpSession session) {
		
		log.info("code:" + code);
		
		String accessToken = "";
		KakaoUserInfo kakaoUserInfo = null;
		
		try {
			//카카오 로그인 API서버에게 로그인 인증을 성공. 인증토큰을 이용하여 카카오 사용자에 대한 정보를 제공.
			accessToken = kakaoLoginService.getAccessToken(code); //인가코드를 통한 인증토큰을 요청, accessToken안에 인증토큰이 있음.
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		
		try {
			//카카오 로그인 API서버에서 보내온 사용자 정보
			kakaoUserInfo = kakaoLoginService.getkakaoUserInfo(accessToken);
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		if(kakaoUserInfo != null) {
			log.info("사용자정보:" + kakaoUserInfo);

			//세션작업, 인증을 세션 방식으로 처리했다.
			session.setAttribute("kakao_status", kakaoUserInfo); //인증여부에 사용됨.
			session.setAttribute("accessToken", accessToken); //카카오 로그아웃에 사용됨.
			
			String sns_email = kakaoUserInfo.getEmail();
			
			String sns_login_type = userService.existUserInfo(sns_email);
//			session.setAttribute("sns_type", sns_login_type);
			
			
			//둘다 데이터가 존재하지 않으면(회원테이블에도 존재안하고, 카카오 테이블에도 존재하지 않으면)
			//&&
			//동일한 데이터 테스트 시 첫번째 : 좌측 true / 우측: true로 전체조건이 true가 되어 데이터가 삽입되었다.
			//				    두번째 : 좌측 true / 우측: false로 전체조건이 false가 되어 데이터가 삽입되지 않았다.
			if(userService.existUserInfo(sns_email) == null && userService.sns_user_check(sns_email) == null) {
				//sns테이블에 데이터 삽입 작업
				SNSUserDTO dto = new SNSUserDTO();
				dto.setId(kakaoUserInfo.getId().toString());
				dto.setEmail(kakaoUserInfo.getEmail());
				dto.setName(kakaoUserInfo.getNickname());
				dto.setSns_type("kakao");
				
				
				userService.sns_user_insert(dto);

				
			}
		}
		
		return "redirect:/";
	}
	
	@GetMapping("/kakaologout")
	public String kakaologout(HttpSession session) {
		
		String accessToken = (String) session.getAttribute("accessToken");
		
		if(accessToken != null && !"".equals(accessToken)) {
			try {
				kakaoLoginService.kakaologout(accessToken);
			}catch (JsonProcessingException ex) {
				throw new RuntimeException(ex);
			}
			
			session.removeAttribute("kakao_status");
			session.removeAttribute("accessToken");
		}
		
		return "redirect:/";
	}
	
	
}
