package com.docmall.basic.review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docmall.basic.commom.dto.Criteria;
import com.docmall.basic.commom.dto.PageDTO;
import com.docmall.basic.user.UserVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// pro_detail.html 에서 상품후기 처리.

@RequiredArgsConstructor
@RequestMapping("/review/*")
@Slf4j
@RestController
public class ReviewController {

	private final ReviewService reviewService;
	
	//리뷰 목록 리뷰목록과 페이징 작업을 진행할 예정, ajax로 할 예정(ajax를 사용하기 때문에 model작업은 하지 않음)
	// rest api 개발방법론 : /revlist/상품코드/페이지번호, revlist/10/1 매핑주소의 파트부분을 매개변수로 사용하고자 하는 경우 
	// 전통적인 주소(쿼리스트링 url) : revlist?pro_num=10&page=1
	// ResponseEntity<T>에서 <T>는 제너릭에서 타입파라미터라고 함. 여기에는 참조타입만 사용할 수 있음.
	// Object안에 pageDTO, reviewVO가 들어감
	@GetMapping("/revlist/{pro_num}/{page}") //목록과 페이징 작업을 할 경우 크리테리아 파라미터를 지금까지는 사용했음.
	public ResponseEntity<Map<String, Object>> revlist(@PathVariable("pro_num") int pro_num, @PathVariable("page") int page) throws Exception {
		ResponseEntity<Map<String, Object>> entity = null;
		Map<String, Object> map = new HashMap<>();
		
		//1) 후기목록 작업 
		Criteria cri = new Criteria();
		cri.setAmount(2);
		cri.setPageNum(page);
		
		List<ReviewVO> revlist = reviewService.rev_list(pro_num, cri);
		
		//2) 페이징 정보
		int revcount = reviewService.getCountReviewByPro_num(pro_num);
		PageDTO pageMaker = new PageDTO(cri, revcount);
		
		//map은 add를 사용하지 않고, put을 사용하여 추가함.
//		map.put("revlist", "후기목록데이타");
//		map.put("pageMaker", "페이징정보");
		map.put("revlist", revlist);
		map.put("pageMaker", pageMaker);
		
		entity = new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
		
		return entity;
	}
	
	//jacksonDatabind 라이브러리.
	//상품후기 저장 작업 consumes : 클라이언트에서 넘어오는 값의 포멧(Mime), produces: 클라이언트쪽에 이 값만 보내겠다고 선언.
	//@RequestBody ReviewVO vo : json으로 넘어온 데이터가 ReviewVO에 저장
	@PostMapping(value = "/review_save", consumes = {"application/json"}, produces = {MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<String> review_saveOK(@RequestBody ReviewVO vo, HttpSession session) throws Exception {
		
		//로그인 했을 때 아이디를 참조
		String user_id = ((UserVO) session.getAttribute("login_status")).getUser_id();
		vo.setUser_id(user_id);
		
		log.info("상품후기데이터: " + vo);
		
		reviewService.review_save(vo);
		
		ResponseEntity<String> entity = null;
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
	
	//상품후기 수정폼
	@GetMapping("/review_modify/{re_code}")
	public ResponseEntity<ReviewVO> review_modifyForm(@PathVariable("re_code") Long re_code) throws Exception {
		
		ResponseEntity<ReviewVO> entity = null;
		
		entity = new ResponseEntity<ReviewVO>(reviewService.review_modify(re_code), HttpStatus.OK);
		
		return entity;
		
	}
	
	//상품후기 수정
	@PutMapping("/review_modify")
	public ResponseEntity<String> review_modifyOK(@RequestBody ReviewVO vo) throws Exception {
		
		ResponseEntity<String> entity = null;
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		
		return entity;
		
	}
	
	
	//상품 후기 삭제
	@DeleteMapping("/review_delete/{re_code}")
	public ResponseEntity<String> review_deleteOK(@PathVariable("re_code") Long re_code) throws Exception {
		
		log.info("장바구니코드 : " + re_code);
		ResponseEntity<String> entity = null;
		reviewService.review_delete(re_code);
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
}
