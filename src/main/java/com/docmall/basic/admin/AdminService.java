package com.docmall.basic.admin;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminService {
	
	private final AdminMapper adminMapper;
	
	public AdminVO loginOK(String admin_id) {
		return adminMapper.loginOK(admin_id);
	}
}
