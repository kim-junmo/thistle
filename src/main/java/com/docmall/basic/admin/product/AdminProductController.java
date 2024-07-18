package com.docmall.basic.admin.product;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docmall.basic.admin.category.AdminCategoryService;
import com.docmall.basic.admin.category.CategoryVO;
import com.docmall.basic.commom.dto.Criteria;
import com.docmall.basic.commom.dto.PageDTO;
import com.docmall.basic.commom.util.FileManagerUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RequestMapping("/admin/product/*")
@Slf4j
@Controller
public class AdminProductController {
	
	private final AdminProductService adminProductService;
	
	private final AdminCategoryService adminCategoryService;
	
	//상품 이미지 경로
	@Value("${file.product.image.dir}")
	private String uploadPath;
	
	//ckeditor 파일 업로드 경로
	@Value("${file.ckdir}")
	private String uploadCKPath;
	
	//상품등록폼
	@GetMapping("/pro_insert")
	public void pro_insertForm(Model model) {
		
		List<CategoryVO> cate_list = adminCategoryService.getFirstCategoryList();
		model.addAttribute("cate_list", cate_list);
	}
	
	//업로드 작업
	//MultipartFile uploadFile : <input type="file" class="form-control" name="uploadFile" id="uploadFile">
	@PostMapping("pro_insert")
	public String pro_insertOK(ProductVO vo, MultipartFile uploadFile) throws Exception {
		
		//1)상품이미지업로드
		String dateFolder = FileManagerUtils.getDateFolder();
		String saveFileName = FileManagerUtils.uploadFile(uploadPath, dateFolder, uploadFile);
		
		vo.setPro_img(saveFileName);
		vo.setPro_up_folder(dateFolder);
		
		log.info("상품정보: " + vo);
		
		
		//2)상품정보 db저장
		adminProductService.pro_insert(vo);
		
		
		
		return "redirect:/admin/product/pro_list";
	}
	
	//ckeditor 상품 설명 이미지 업로드
	//MultipartFile upload : ckeditor 업로드 탭에서 나온 파일 첨부 태그 파라미터(<input type="file" name="upload">)를 참조함.
	//MultipartFile upload : 첨부된 파일정보를 가지고 있음.
	//HttpServletRequest request : 클라이언트의 요청정보를 가지고 있는 객체.
	//HttpServletResponse response : 서버에서 클라이언트에게 보낸 정보를 응답하는 객체.
	@PostMapping("/imageupload")
	public void imageupload(HttpServletRequest request, HttpServletResponse response, MultipartFile upload) {
		
		//입출력 스트림으로 OutputStream을 사용함. 자바의 입출력 스트림 공부해서 정리할 필요가 있음.
		OutputStream out = null;
		//PrintWriter: 서버에서 클라이언트에게 응답정보를 보낼때 사용.(업로드한 이미지 정보를 클라이언트(브라우저)에게 보내는 작업용도)
		PrintWriter printWriter = null;
		
		try {
			//1)ckeditor를 통한 파일 업로드 처리 기능.
			String fileName = upload.getOriginalFilename(); //업로드 할 클라이언트 파일 이름.
			byte[] bytes = upload.getBytes(); //업로드 할 파일의 바이트 배열
			
			//uploadCKPath + fileName : "C:\\dev\\upload\\ckeditor\\" + "fileName(ex. abc.gif)
			//그렇기 때문에 경로에 \\가 들어가 있어야 함. 없으면 C:\\dev\\upload\\ckeditorabc.gif 이렇게 됨.
			String ckUploadPath = uploadCKPath + fileName;
			
			//C:\\dev\\upload\\ckeditor\\abc.gif 이 한줄에 의해 파일이 생성이 된다.
			//이때 파일은 0byte로 생성이 된다. 
			out = new FileOutputStream(ckUploadPath); 
			
			out.write(bytes); //빨때(스트림)의 공간에 업로드할 파일의 바이트 배열을 채운 상태.
			out.flush(); //0byte의 파일의 크기가 채워진 정상적인 파일로 인식이 된다.
			
			//2)업로드한 이미지 파일에 대한 정보를 클라이언트에게 보내는 작업.
			
			printWriter = response.getWriter();

			String fileUrl = "/admin/product/display/" + fileName; //매핑주소/이미지파일명
			//String fileUrl = fileName;
			
			//ckeditor 4.1.2에서는 파일정보를 다음과 같이 구성하여 보내야 한다.
			//{"fileName" : "abc.gif", "uploaded":1, "url":"/ckupload/abc.gif"} : 자바스트의 제이슨 문법 스타일.
			//{"fileName" : "변수", "uploaded":1, "url":"변수"} 
			//즉 변수 값을 포함하는 JSON 문자열을 생성하고 출력하는 예제이다. 스트림에 내용을 채움.
			printWriter.println("{\"fileName\" :\"" + fileName + "\", \"uploaded\":1,\"url\":\"" + fileUrl + "\"}"); //스프림에 채움.
			printWriter.flush(); //println의 정보가 브라우저로 보내진다.
			
		}catch (Exception ex) {
			ex.printStackTrace();
		}finally {
			if(out != null) {
				try {
					out.close();
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			if(printWriter != null) printWriter.close();
		}
	}
	
	//<img src="매핑주소">를 통해 이미지를 제공하는 핸들러
		@GetMapping("/display/{fileName}")
		public ResponseEntity<byte[]> getFile(@PathVariable("fileName") String fileName) {
			
			log.info("파일이미지: " + fileName); //파일 이름을 로그에 기록
			
			ResponseEntity<byte[]> entity = null;  //파일 데이터를 담을 ResponseEntity 객체
						
			try {
				//파일을 읽어 ResponseEntity 객체에 담음
				entity = FileManagerUtils.getFile(uploadCKPath, fileName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return entity; //클라이언트에게 파일 데이터를 반환
		}
		
		//상품리스트
		@GetMapping("/pro_list")
		public void pro_list(Criteria cri, Model model) throws Exception {
			
//			cri.setAmount(2);
			
			List<ProductVO> pro_list = adminProductService.pro_list(cri);
			
			//클라이언트에 \를 /로 변환하여 model 작업 전 처리함. 2024\07\01 -> 2024/07/01
			pro_list.forEach(vo -> {
				vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/"));
			});
			
			
			int totalCount = adminProductService.getTotalCount(cri);
			
			model.addAttribute("pro_list", pro_list);
			model.addAttribute("pageMaker", new PageDTO(cri, totalCount));
			
			
		}
		
		// 상품리스트에서 사용할 이미지보여주기. 1)<img src="매핑주소"> 2) <img src="test.gif">
		@GetMapping("/image_display")
		public ResponseEntity<byte[]> image_display(String dateFolderName, String fileName) throws Exception {
			
			return FileManagerUtils.getFile(uploadPath + dateFolderName, fileName);
		}
		
		//상품 수정 폼
		@GetMapping("/pro_edit")
		public void pro_editForm(@ModelAttribute("cri") Criteria cri, Integer pro_num, Model model) throws Exception {
			
			
			
			//1차 카테고리 목록
			List<CategoryVO> cate_list = adminCategoryService.getFirstCategoryList();
			model.addAttribute("cate_list", cate_list);
			
			//상품정보(2차카테고리)
			//model이름 : productVO
			ProductVO vo = adminProductService.pro_edit(pro_num);
			
			//클라이언트에 \를 /로 변환하여 model 작업 전 처리함. 2024\07\01 -> 2024/07/01
			vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/"));
			model.addAttribute("productVO", vo);
			
			//1차 카테고리
			int cate_code = vo.getCate_code(); //상품 테이블에 존재하는 2차 카테고리 코드
			int cate_prtcode = adminCategoryService.getFirstCategoryBySecondCategory(cate_code).getCate_prtcode();
			model.addAttribute("cate_prtcode", cate_prtcode);
			
			//2차 카테고리 목록
			model.addAttribute("sub_cate_list", adminCategoryService.getsecondCategoryList(cate_prtcode));
			
			
		}
		
		//상품 수정하기, Criteria사용하는 이유는 원래 출발했던 원래상태로 돌아가기 위해서이다. 
		@PostMapping("/pro_edit")
		public String pro_editOK(ProductVO vo, MultipartFile uploadFile, Criteria cri, RedirectAttributes rttr) throws Exception {
			
			//상품 이미지 변경(업로드) 유무
			if(!uploadFile.isEmpty()) {
				
				//기존 상품이미지 삭제, 날짜 폴더명, 파일명이 필요함.
				FileManagerUtils.delete(uploadPath, vo.getPro_up_folder(), vo.getPro_img(), "image");
				//변경 이미지 업로드
				String dateFolder = FileManagerUtils.getDateFolder();
				String saveFileName = FileManagerUtils.uploadFile(uploadPath, dateFolder, uploadFile);
				
				// 새로운 파일명, 날짜 폴더명
				vo.setPro_img(saveFileName);
				vo.setPro_up_folder(dateFolder);
				
			}
			
			log.info("상품 수정 정보: " + vo);
			
			//db저장(update) 공통으로 작업해야함.
			adminProductService.pro_edit_ok(vo);
			
			return "redirect:/admin/product/pro_list" + cri.getListLink();
		}
		
		//상품 삭제하기
		@PostMapping("/pro_delete")
		public String pro_deleteOK(Criteria cri, Integer pro_num) throws Exception {
			
			adminProductService.pro_delete(pro_num);
			
			return "redirect:/admin/product/pro_list" + cri.getListLink();
		}
		
		//체크상품 수정작업1
		@PostMapping("/pro_checked_modify1")
		public ResponseEntity<String> pro_checked_modify1(
				@RequestParam("pro_num_arr") List<Integer> pro_num_arr,
				@RequestParam("pro_price_arr") List<Integer> pro_price_arr,
				@RequestParam("pro_buy_arr") List<String> pro_buy_arr) throws Exception {
			
			log.info("상품코드 : " + pro_num_arr);
			log.info("상품가격 : " + pro_price_arr);
			log.info("상품진열 : " + pro_buy_arr);
			
			adminProductService.pro_checked_modify1(pro_num_arr, pro_price_arr, pro_buy_arr);
			
			ResponseEntity<String> entity = null;
			entity = new ResponseEntity<>("success", HttpStatus.OK);
			
			return entity;
		}
		
		//체크상품 수정작업2
		@PostMapping("/pro_checked_modify2")
		public ResponseEntity<String> pro_checked_modify2(
				@RequestParam("pro_num_arr") List<Integer> pro_num_arr,
				@RequestParam("pro_price_arr") List<Integer> pro_price_arr,
				@RequestParam("pro_buy_arr") List<String> pro_buy_arr) throws Exception {
			
			log.info("상품코드 : " + pro_num_arr);
			log.info("상품가격 : " + pro_price_arr);
			log.info("상품진열 : " + pro_buy_arr);
			
			adminProductService.pro_checked_modify2(pro_num_arr, pro_price_arr, pro_buy_arr);
			
			ResponseEntity<String> entity = null;
			entity = new ResponseEntity<>("success", HttpStatus.OK);
			
			return entity;
		}
		
		
	}
