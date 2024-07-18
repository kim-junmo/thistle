package com.docmall.basic.user;

import org.apache.ibatis.annotations.Param;

public interface UserMapper {

	public void join(UserVO vo);
	
	String idCheck(String user_id);
	
	UserVO login(String user_id);
	
	//아이디 찾기 작업
	String idfind(@Param("user_name") String user_name, @Param("user_email") String user_email);
	
	//비밀번호 찾기 작업
	String pwfind(@Param("user_id") String user_id, @Param("user_name") String user_name, @Param("user_email") String user_email);
	
	//임시 비밀번호 업데이트
	void tempPwUpdate(@Param("user_id") String user_id, @Param("temp_enc_pw") String temp_enc_pw);
	
	//내 정보 변경 작업
	void modify(UserVO vo);
	
	//비밀번호 변경작업
	void changepw(@Param("user_id") String user_id, @Param("new_user_password") String new_user_password);
	
	//회원 탈퇴
	void delete(String user_id);
	
	//sns가입
	String existUserInfo(String sns_email);
	
	//sns 유저 중복체크
	String sns_user_check(String sns_email);
	
	//sns 데이터 삽입
	void sns_user_insert(SNSUserDTO dto);
	

}
