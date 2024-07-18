package com.docmall.basic.kakaologin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class KakaoLoginService {
	
	private final KakaoMapper kakaoMapper;
	
	@Value("${kakao.client.id}")
	private String clientId;
	
	@Value("${kakao.redirect.uri}")
	private String redirectUri;
	
	@Value("${kakao.client.secret}")
	private String clientSecret;
	
	@Value("${kakao.oauth.tokenuri}")
	private String tokenUri;
	
	@Value("${kakao.oauth.userinfouri}")
	private String userinfoUri;
	
	@Value("${kakao.user.logout}")
	private String kakaologout;

	/*Step3 엑세스 토큰을 받기 위한 정보
	  주소: https://kauth.kakao.com/oauth/token 주소 호출하는 작업.
	  요청방식(Post)
	  헤더(Header) : Content-type: application/x-www-form-urlencoded;charset=utf-8
	  본문(Body) :
	  grant_type : authorization_code
	  client_id : 앱 REST API 키
	  redirect_uri : 인가 코드가 리다이렉트된 URI
	  code : 인가 코드 받기 요청으로 얻은 인가 코드(실시간으로 받기 때문에 달라질 수 있음.
	  client_secret : 토큰 발급 시, 보안을 강화하기 위해 추가 확인하는 코드
    */
	public String getAccessToken(String code) throws JsonProcessingException {
		
		//1. http Header를 사용하기 위해 HttpHeader 클래스가 있다. org.springframework.http
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		//2. Http Body 생성
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		
		body.add("grant_type", "authorization_code");
		body.add("client_id", clientId);
		body.add("redirect_uri", redirectUri);
		body.add("code", code);
		body.add("client_secret", clientSecret);
		
		//3. Header와 Body를 한번에 관리하는 클래스
		//즉 Header + Body 정보를 Entity로 구성.
		HttpEntity<MultiValueMap<String, String>> tokenKakaoRequest = new HttpEntity<>(body, headers);
		
		//4. http요청보내기. (API Server와 통신을 담당하는 기능을 제공하는 클래스). resttemplate  https://adjh54.tistory.com/234 
		/*
		 API작업을 할때 사용하는 클래스. RESTful API 웹 서비스와의 상호작용할때 외부 도메인에서 데이터를 전송 및 가져올때 사용.
		 원격 서버와 ‘동기식 방식’으로 JSON, XML 등의 다양한 데이터 형식으로 통신
		 */
		RestTemplate restTemplate = new RestTemplate();
		
		//restTemplate.exchange(token, HttpMethod.POST, tokenKakaoRequest, String.class);
		//위가 실행이 되어 responseEntity가 결과를 받는다.
		ResponseEntity<String> responseEntity = restTemplate.exchange(tokenUri, HttpMethod.POST, tokenKakaoRequest, String.class);
		
		//Http 응답(JSON) -> Access Token 추출(파싱 Parsing)작업 (JSON안에 토큰이 들어가 있음)
		String responseBody = responseEntity.getBody();
		
		log.info("응답데이터:" + responseBody);
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseBody);
		
		
		return jsonNode.get("access_token").asText();
	}
	
	//엑세스 토큰을 이용한 사용자 정보 받아오기
		public KakaoUserInfo getkakaoUserInfo(String accessToken) throws JsonProcessingException {
			
			//1) 헤더 생성 작업
			HttpHeaders headers = new HttpHeaders();
			//Bearer : 뒤에 공간이 있는 이유는 카카오측에서 그렇게 설계를 했기 때문.
			headers.add("Authorization", "Bearer " + accessToken); 
			headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			
			//2) body 생성안함, 카카오 디벨로퍼 API 메뉴얼에서 필수가 아님.
			
			//3) Header와 Body를 한번에 관리하는 클래스
			HttpEntity<MultiValueMap<String, String>> userinfokakaoRequest = new HttpEntity<>(headers);
			
			//4) Http 요청
			RestTemplate restTemplate = new RestTemplate();
			
			//5) Http 응답
			ResponseEntity<String> responseEntity = restTemplate.exchange(userinfoUri, HttpMethod.POST, userinfokakaoRequest, String.class);
			
			String responseBody = responseEntity.getBody();
			
			log.info("응답사용자정보:" + responseBody);
			
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(responseBody);
			
			//인증토큰을 이용한 카카오 사용자에 대한 정보를 받아옴.
			Long id = jsonNode.get("id").asLong();
			String email = jsonNode.get("kakao_account").get("email").asText();
			String nickname = jsonNode.get("properties").get("nickname").asText();
			
			return new KakaoUserInfo(id, nickname, email);
		}
		
		//카카오 로그아웃. https://kapi.kakao.com/v1/user/logout 헤더는 있고, 요청 파라미터가 없는 경우
		//헤더 : Authorization: Bearer ${ACCESS_TOKEN}
		public void kakaologout(String accessToken) throws JsonProcessingException {
			
			//http header 생성
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + accessToken);
			headers.add("Content-type", "application/x-www-form-urlencoded");
			
			//http 요청 작업
			HttpEntity<MultiValueMap<String, String>> kakaoLogoutRequest = new HttpEntity<>(headers);
			
			//http 요청하기
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.exchange(kakaologout, HttpMethod.POST, kakaoLogoutRequest, String.class);
			
			//리턴된 정보가 JSON의 포멧 문자열로 넘어온다.(responseBody)
			String responseBody = response.getBody();
			log.info("responseBody" + responseBody);
			
			//JSON문자열을 java 객체로 역직열화 하거나 자바 객체를 JSON으로 직렬화 할 때 사용하는 Jackson라이브러리의 클래스이다.
			//ObjectMapper 생성 비용이 비싸기 때문에 bean 혹은 static으로 처리하는 것이 성능에 좋다.
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(responseBody);
			
			Long id = jsonNode.get("id").asLong();
			
			log.info("id" + id);
		}
		
}
