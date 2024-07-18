package com.docmall.basic.admin;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/admin/*")
@RequiredArgsConstructor
@Slf4j
@Controller
public class AdminController {
	
	private final AdminService adminService;
	
	//passwordEncoder 평문(일반 텍스트)를 암호화 작업., SecurityConfig에서 주입.
	private final PasswordEncoder passwordEncoder; 
	
	@GetMapping("/")
	public String loginForm() {
		
		return "/admin/adlogin";
	}
	
	@PostMapping("/admin_ok")
	public String admin_ok(AdminVO vo, HttpSession session) throws Exception {
		
		log.info("관리자정보 : " + vo);
		
		AdminVO d_vo = adminService.loginOK(vo.getAdmin_id());
				
		String url = "";
		
				if(d_vo != null) {
					if(passwordEncoder.matches(vo.getAdmin_pw(), d_vo.getAdmin_pw())) {
						
						d_vo.setAdmin_pw("");
						session.setAttribute("admin_state", d_vo);
						url = "admin/admin_meun";
					}
				}
				
		
		return "redirect:/" + url;
	}
	
	@GetMapping("/admin_meun")
	public void admin_meun() {
		
	}
	
	@GetMapping("/admin_logout")
	public String admin_logout(HttpSession session) {
		
//		session.invalidate();
		
		session.removeAttribute("admin_state");
		
		return "redirect:/admin/";
	}

}
