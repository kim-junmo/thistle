package com.docmall.basic.user;

import java.util.UUID;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

//구현클래스
@RequiredArgsConstructor
@Service
public class UserService{

	private final UserMapper userMapper;

	public void join(UserVO vo) {
		// TODO Auto-generated method stub
		userMapper.join(vo);
	}

	public String idCheck(String user_id) {
		// TODO Auto-generated method stub
		return userMapper.idCheck(user_id);
	}
	
	public UserVO login(String user_id) {
		return userMapper.login(user_id);
	}
	
	//아이디 찾기 작업
	public String idfind(String user_name, String user_email) {
		return userMapper.idfind(user_name, user_email);
	}
	
	//비밀번호 찾기 작업
	public String pwfind(String user_id, String user_name, String user_email) {
		return userMapper.pwfind(user_id, user_name, user_email);
	}
	
	//임시비밀번호
	public String getTempPw() {
		
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
	}
	
	//비밀번호 재발급
	public void tempPwUpdate(String user_id, String temp_enc_pw) {
		userMapper.tempPwUpdate(user_id, temp_enc_pw);
	}
	
	//내 정보 변경 작업
	public void modify(UserVO vo) {
		userMapper.modify(vo);
	}
	
	//비밀번호 변경 작업
	public void changepw(String user_id, String new_user_password) {
		userMapper.changepw(user_id, new_user_password);
	}
	
	//회원 탈퇴
	public void delete(String user_id) {
		userMapper.delete(user_id);
	}
	
	//sns
	public String existUserInfo(String sns_email) {
		return userMapper.existUserInfo(sns_email);
	}
	
	//sns 사용자 중복체크
	public String sns_user_check(String sns_email) {
		return userMapper.sns_user_check(sns_email);
	}
	
	//sns 데이터 삽입
	public void sns_user_insert(SNSUserDTO dto) {
		userMapper.sns_user_insert(dto);
	}

}
