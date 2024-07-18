package com.docmall.basic.naverlogin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NaverLoginService {
	
	@Value("${naver.client.id}")
	private String clientId;
	
	@Value("${naver.redirect.uri}")
	private String redirectUri;
	
	@Value("${naver.client.secret}")
	private String clientSecret;
	
	/* 카카오때는 이 작업을 했음.
	https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=CLIENT_ID&state=STATE_STRING&redirect_uri=CALLBACK_URL
	StringBuffer url = new StringBuffer();
	url.append("https://kauth.kakao.com/oauth/authorize?");
	url.append("response_type=code");
	url.append("&client_id=" + clientId);
	url.append("&redirect_uri=" + redirectUri); //http://localhost:9090/oauth2/callback/kakao

	url.append("&prompt=login"); 아래방법은 ex05)Criteria에서 사용한 방법이다.
	 */
	public String getNaverAuthorizerUrl() throws UnsupportedEncodingException {
		
		UriComponents uricomponents = UriComponentsBuilder
				.fromUriString("https://nid.naver.com/oauth2.0/authorize")
				.queryParam("response_type", "code")
				.queryParam("client_id", clientId)
				.queryParam("state", URLEncoder.encode("1234", "UTF-8"))
				.queryParam("redirect_uri", URLEncoder.encode(redirectUri, "UTF-8"))
				.build();
		
		return uricomponents.toString();
	}
	
	//https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id=jyvqXeaVOVmV&client_secret=527300A0_COq1_XV33cf&code=EIc5bFrl4RibFls1&state=9kgsGTfH4j7IyAkg  
	//accessToken을 받아오는 주소
	public String getNaverTokenUrl(NaverCallback callback) {


		
		try { //네이버 서버에 들어가 작업을 하기 때문에 예외처리를 해야한다. 네트워크 오류, 서버 오류 등이 발생할 수 있기 때문
			//access token을 받기 위한 요청보내기 작업 후 결과값이 JSON으로 응답
			
			UriComponents uricomponents = UriComponentsBuilder
					.fromUriString("https://nid.naver.com/oauth2.0/token")
					.queryParam("grant_type", "authorization_code")
					.queryParam("client_id", clientId)
					.queryParam("client_secret", clientSecret)
					.queryParam("code", callback.getCode())
					.queryParam("state", URLEncoder.encode(callback.getState(), "UTF-8"))
					.build();
			
			URL url = new URL(uricomponents.toString());
			
			//https://blueyikim.tistory.com/2199 참조.
			//카카오에서는 RestTemplate restTemplate = new RestTemplate();으로 처리했음.
			//네이버에서는 HttpURLConnection conn = (HttpURLConnection) url.openConnection();을 사용.
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			
			int resposeCode = conn.getResponseCode();
			BufferedReader br;
			
			//입력스트림 작업. 
			//conn.getInputStream() : 바이트기반으로 외부 서버에서 보내오는 정보를 읽어들어오는 스트림.
			//InputStreamReader 클래스 : 바이트기반의 스트림을 문자기반스트림으로 변환하는 기능.
			//BufferedReader : 버퍼기능 제공 보조 스트림
			if(resposeCode == 200) {
					br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				}else {
					br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				}

			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			
			br.close();
			
			log.info("응답데이터 : " + response.toString());
			
			return response.toString();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
				
		return null;
		
	}
	
	/* https://openapi.naver.com/v1/nid/me
	 접근 토큰(access token)을 전달하는 헤더
	 다음과 같은 형식으로 헤더 값에 접근 토큰(access token)을 포함합니다. 토큰 타입은 "Bearer"로 값이 고정되어 있습니다.
 	 Authorization: {토큰 타입] {접근 토큰]
	 */
	public String getNaverUserByToken(NaverToken naverToken) {
		String accessToken = naverToken.getAccess_token();
		String tokenType = naverToken.getToken_type();
		
		try {
			URL url = new URL("https://openapi.naver.com/v1/nid/me");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", tokenType + " " + accessToken);
					
			int resposeCode = conn.getResponseCode();
			BufferedReader br;
			
			//입력스트림 작업. 
			//conn.getInputStream() : 바이트기반으로 외부 서버에서 보내오는 정보를 읽어들어오는 스트림.
			//InputStreamReader 클래스 : 바이트기반의 스트림을 문자기반스트림으로 변환하는 기능.
			//BufferedReader : 버퍼기능 제공 보조 스트림
			if(resposeCode == 200) {
					br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				}else {
					br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				}
	
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			
			br.close();
			
			log.info("사용자정보응답데이터 : " + response.toString());
	
			return response.toString();
			
		}catch (Exception e) {
			
			e.printStackTrace();
		} 
				
		return null;
		
	}
	
	
	public void getNaverTokenDelete(String access_token) {


		
		try { //네이버 서버에 들어가 작업을 하기 때문에 예외처리를 해야한다. 네트워크 오류, 서버 오류 등이 발생할 수 있기 때문
			//access token을 받기 위한 요청보내기 작업 후 결과값이 JSON으로 응답
			
			UriComponents uricomponents = UriComponentsBuilder
					.fromUriString("https://nid.naver.com/oauth2.0/token")
					.queryParam("grant_type", "delete")
					.queryParam("client_id", clientId)
					.queryParam("client_secret", clientSecret)
//					.queryParam("code", callback.getCode())
//					.queryParam("state", URLEncoder.encode(callback.getState(), "UTF-8"))
					.queryParam("access_token", URLEncoder.encode(access_token, "UTF-8"))
					.build();
			
			URL url = new URL(uricomponents.toString());
			
			//https://blueyikim.tistory.com/2199 참조.
			//카카오에서는 RestTemplate restTemplate = new RestTemplate();으로 처리했음.
			//네이버에서는 HttpURLConnection conn = (HttpURLConnection) url.openConnection();을 사용.
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			
			int resposeCode = conn.getResponseCode();

			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
				
	}

}
