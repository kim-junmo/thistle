package com.docmall.basic.user;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docmall.basic.kakaologin.KakaoUserInfo;
import com.docmall.basic.mail.EmailDTO;
import com.docmall.basic.mail.EmailService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user/*")
@Controller
public class UserController {

	private final UserService userService;
	
	//passwordEncoder 평문(일반 텍스트)를 암호화 작업., SecurityConfig에서 주입.
	private final PasswordEncoder passwordEncoder; 
	
	//아이디 찾기에 관련하여 이메일 서비스 의존성 주입이 필요하여 작성함.
	private final EmailService emailService;
	
	//회원가입 폼
	@GetMapping("join")
	public void joinForm() {
		log.info("join");
		
	}
	
	//회원가입 정보 저장.
	@PostMapping("/join")
	public String join(UserVO vo) throws Exception {
		
//		log.info("비밀번호: " + passwordEncoder.encode(vo.getU_pwd()));
		
		//비밀번호 암호화로 변경.
		vo.setUser_password(passwordEncoder.encode(vo.getUser_password()));
		
		log.info("회원정보: " + vo);
		
		userService.join(vo);
		
		return "redirect:/user/login";
	}

	
	// 아이디중복체크, Ajax요청 작업은 리턴타입이 ResponseEntity를 사용해야 한다.
	// ResponseEntity를 사용시 @RespinseBody를 사용할 필요가 없다.
	// 아이디 길이는 유동적으로 변하니 ResponseEntity를 사용함.
	@GetMapping("/idCheck")
	public ResponseEntity<String> idCheck(String user_id) throws Exception {
		
		log.info("아이디: " + user_id);
		
		ResponseEntity<String> entity = null;
		
		String idUse = "";
		if(userService.idCheck(user_id) != null) {
			idUse = "no"; // 사용불가능
		}else {
			idUse = "yes"; // 사용가능
		}
		
		entity = new ResponseEntity<String>(idUse, HttpStatus.OK);
		
		return entity;
		
	}
	
	//로그인폼
	@GetMapping("/login")
	public void loginForm() {
		
	}
	
	//로그인
	@PostMapping("login") //1)LoginDTO dto 2)String user_id, String user_password
	public String loginOK(LoginDTO dto, HttpSession session, RedirectAttributes rttr) throws Exception {
		
		UserVO vo = userService.login(dto.getUser_id());
		
		String msg = ""; 
		String url = "/"; //"/" : 메인주소
		
		if(vo != null) { //null이 아니면(id가 존재하는 경우)
			//비밀번호 비교, u_pwd: 사용자가 작성한 pwd, vo.getU_pwd: db에서 가지고온 암호화된 pwd
			//user_password: 사용자가 입력한 평문텍스트 비밀번호
			//vo.getUser_password(): 디비에 저장된 암호화된 비밀번호
			if(passwordEncoder.matches(dto.getUser_password(), vo.getUser_password())) { // 사용자가 입력한 비밀번호가 암호화된 형태와 일치하는지 확인
				vo.setUser_password(""); //암호화된 비밀번호
				session.setAttribute("login_status", vo);
			}else { // 사용자가 입력한 비밀번호가 암호화된 형태에 해당하지 않는것이라면
				msg = "failPW"; //failPW는 login.html 자바스트 파일에서 사용함.
				url = "/user/login"; //로그인폼 주소
			}
		}else { //가져온 유저 정보가 null인 경우 (아이디가 존재하지 않는 경우)
			msg = "failID";
			url = "/user/login"; //로그인폼 주소
		}
		
		rttr.addFlashAttribute("msg", msg); //thymeleaf에서 msg변수를 사용목적
		
		return "redirect:" + url; //메인으로 이동.
	
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		
		session.invalidate(); // invalidate: 세션형태로 관리되는 모든 메모리 소멸.
		
		return "redirect:/";
	}
	
	//로그인 페이지 아이디 찾기
		@GetMapping("/idfind")
		public void idfindForm() {
			
		}
		
		@PostMapping("/idfind")
		public String idfindOK(String user_name, String user_email, String authcode, HttpSession session, RedirectAttributes rttr) throws Exception {
			
			String url = "";
			String msg = "";
			
			//인증코드 확인 작업
			if(authcode.equals(session.getAttribute("authcode"))) {
				
				//이름과 메일주소를 확인하는 작업, 아이디를 찾아 메일발송.
				String user_id = userService.idfind(user_name, user_email);
				if(user_id != null) {

					
					//아이디를 내용으로 한 메일 발송 작업, 아이디와 이메일이 동일하다면 메일 발송작업을 진행한다.
					String subject = "DocMall 아이디를 보냅니다";
					String message = "아이디를 확인하시고, 로그인 해주세요.";
					
					//emailService.sendMail("타임리프 파일명", null, u_id);
					EmailDTO dto = new EmailDTO("DocMall", "DocMall", user_email, subject, message);
					
					
					emailService.sendMail("emailIDResult", dto, user_id);
					
				session.removeAttribute("authcode");
					
					msg = "success";
					url = "/user/login";
					rttr.addFlashAttribute("msg", msg);
					
				}else {
					msg = "failID";
					url = "/user/idfind";
				}
				
			}else { //인증번호가 틀렸다면.
				msg = "failAuthCode";
				url = "/user/idfind";
			}
			rttr.addFlashAttribute("msg", msg);
			
			return "redirect:" + url;
		}


		//비밀번호 찾기 폼
		@GetMapping("/pwfind")
		public void pwfindForm() {
			
		}
		
		//비밀번호
		@PostMapping("/pwfind")
		public String pwfindOK(String user_id, String user_name, String user_email, String authcode, HttpSession session, RedirectAttributes rttr) throws Exception {
			
			String url = "";
			String msg = "";
			
			//인증코드 확인 및 비교작업을 진행.
			if(authcode.equals(session.getAttribute("authcode"))) {
				
				//사용자가 입력한 3개의 정보(아이디, 이름, 이메일)를 조건으로 사용하여, 이메일을 db에서 가져온다.
				String d_email = userService.pwfind(user_id, user_name, user_email);
				if(d_email != null) {
					
					//임시 비밀번호 생성 (uuid 이용)
					String tempPw = userService.getTempPw();
					
					//10자리 임시 비밀번호를 가지고 온 후 업데이트 작업을 진행해야 됨.(db에 넣어야 함.)
					//암호화된 비밀번호
					String temp_enc_pw = passwordEncoder.encode(tempPw);
					
					//임시 비밀번호를 암호화하여 암호화된 임시비번을 해당 아이디에 업데이트 한다.
					userService.tempPwUpdate(user_id, temp_enc_pw);
					
					//메일 발송
					//비밀번호 임시코드를 생성하여, 암호화 한 후 사용자 아이디에 수정한다.
					
					
					//그리고 임시비밀번호를 메일로 발급한다.
					
					EmailDTO dto = new EmailDTO("DocMall", "DocMall", d_email, "DocMall에서 임시 비밀번호를 보냈습니다.", tempPw);
					//emailService.sendMail("타임리프 파일명", dto, tempPw);
					emailService.sendMail("emailPwResult", dto, tempPw);
					
					session.removeAttribute("authcode");
					msg = "success";
					url = "/user/pwfind";
				}else {
					url = "/user/pwfind";
					msg = "failInput";
				}

			}else {
				url = "/user/pwfind";
				msg = "failAuth";
			}
			
			rttr.addFlashAttribute("msg", msg);
			
			return "redirect:" + url;
			

		}
		
		//인증된 페이지에서 Session에 저장된 값을 꺼내야 되기 때문에 Session을 사용해야 한다.
		//사용자가 일반 로그인 또는 카카오 로그인 인지 체크하는 과정이 필요.
		@GetMapping("/mypage")
		public void mypageForm(HttpSession session, Model model) throws Exception {
			
			
			if(session.getAttribute("login_status") != null) {
			String user_id = ((UserVO) session.getAttribute("login_status")).getUser_id();
			
			UserVO vo = userService.login(user_id);
			
			model.addAttribute("user", vo);
			
			}else if(session.getAttribute("kakao_status") != null) {
				
				KakaoUserInfo kakaoUserInfo = (KakaoUserInfo) session.getAttribute("kakao_status");
				
				//Mypage에서 보여줄 정보를 선택적으로 작업.
				UserVO vo = new UserVO();
				vo.setUser_name(kakaoUserInfo.getNickname());
				vo.setUser_email(kakaoUserInfo.getEmail());
				
				model.addAttribute("user", vo);
				model.addAttribute("msg", "kakao_login");
			}
		}
		
		//내정보 수정하기 throws Exception: db작업을 하면 예외처리를 해야한다.
		//섹션방식으로 인증 가입된 정보를 가지고 와야되기 때문에 HttpSession session이 필요하다. 
		@PostMapping("/modify")
		public String modifyOK(UserVO vo, HttpSession session, RedirectAttributes rttr) throws Exception {
			
			//세션이 죽으면 페이지에 오류가 발생하기 때문에 로그인 페이지로 돌아갈 수 있게 만들어야 함.
			//이는 작업 막판에 진행이 되며 스프링 인터셉트라고 한다.
			if(session.getAttribute("login_status") == null) return "redirect:/user/login";
			
			log.info("회원수정: " + vo);
			
			// 세션에서 로그인한 사용자의 ID를 가져옵니다.
			String user_id = ((UserVO) session.getAttribute("login_status")).getUser_id();
			vo.setUser_id(user_id);
			
			
			 // 사용자 정보를 수정합니다.
			userService.modify(vo);
			
			// 타임리프에서 사용하기 위한 목적으로 "success"라는 플래시 속성을 추가합니다.
			rttr.addFlashAttribute("msg", "success"); //jsp에서 사용하기 위한 목적으로 만들었음.
			
			// 사용자를 마이페이지로 리다이렉트하여 수정된 정보가 적용되어 화면에 표시됩니다.
			return "redirect:/user/mypage";
		}
		
		@GetMapping("/changepw")
		public void changepwForm() {
			
		}
		
		@PostMapping("/changepw")
		public String changepwOK(String cur_user_password, String new_user_password, HttpSession session, RedirectAttributes rttr) throws Exception {
			
			//세션을 통해 아이디를 참조
			String user_id = ((UserVO) session.getAttribute("login_status")).getUser_id();
			
			UserVO vo = userService.login(user_id);
			
			String msg = ""; 
			String url = "/"; // "/" : 메인주소
			
			if(vo != null) { //null이 아니면(id가 존재하는 경우)
				//비밀번호 비교, user_pwd: 사용자가 작성한 pwd, vo.getUser_pwd: db에서 가지고온 암호화된 pwd
				if(passwordEncoder.matches(cur_user_password, vo.getUser_password())) { // 사용자가 입력한 비밀번호가 암호화된 형태에 해당하는 것이라면

					//신규 비밀번호 변경작업. 암호화 작업도 함께.
					String enc_new_user_password = passwordEncoder.encode(new_user_password);
					//String user_password = passwordEncoder.encode(new_user_password);이 작업으로 인해 u_pwd는 암호화된 패스워드이다.
					 userService.changepw(user_id, enc_new_user_password);
					 msg = "success";
					
				}else { // 사용자가 입력한 비밀번호가 암호화된 형태에 해당하지 않는것이라면
					msg = "failPW";
				}
			}
			
			rttr.addFlashAttribute("msg", msg); //jsp에서 msg 변수 사용목적
			
			return "redirect:/user/changepw"; //메인으로 이동.
					
		}
		
		//회원탈퇴 폼
		@GetMapping("/delete") 
		public void deleteForm() {
		}
		
		//회원탈퇴 기능
		//RedirectAttributes rttr 메세지 작업 시 필요함.
		@PostMapping("/delete")
		public String deleteOK(String user_password, HttpSession session, RedirectAttributes rttr) throws Exception {
			
			String user_id = ((UserVO) session.getAttribute("login_status")).getUser_id();
			
			UserVO vo = userService.login(user_id);
			
			String msg = ""; 
			String url = "/"; //"/" : 메인주소
			
			if(vo != null) { //null이 아니면(id가 존재하는 경우)
				//비밀번호 비교, u_pwd: 사용자가 작성한 pwd, vo.getU_pwd: db에서 가지고온 암호화된 pwd
				//user_password: 사용자가 입력한 평문텍스트 비밀번호
				//vo.getUser_password(): 디비에 저장된 암호화된 비밀번호
				if(passwordEncoder.matches(user_password, vo.getUser_password())) { // 사용자가 입력한 비밀번호가 암호화된 형태와 일치하는지 확인

					//회원삭제(탈퇴)작업
					userService.delete(user_id);
					session.invalidate(); //회원탈퇴 시 세션 삭제 작업.
					
				}else { // 사용자가 입력한 비밀번호가 암호화된 형태에 해당하지 않는것이라면
					msg = "failPW"; //failPW는 login.html 자바스트 파일에서 사용함.
					url = "/user/delete"; //회원 탈퇴 폼 주소
					
					rttr.addAttribute("msg", msg);
				}
			}
			
			
			return "redirect:" + url;
		}
		
}
