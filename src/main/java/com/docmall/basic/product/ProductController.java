package com.docmall.basic.product;

import java.net.URLDecoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.admin.product.ProductVO;
import com.docmall.basic.commom.dto.Criteria;
import com.docmall.basic.commom.dto.PageDTO;
import com.docmall.basic.commom.util.FileManagerUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/product/*")
public class ProductController {
	
	private final ProductService productService;
	
	//상품 이미지 경로
	@Value("${file.product.image.dir}")
	private String uploadPath;
	
	@GetMapping("/pro_list") //Criteria cri는 페이징과 검색기능을 가지고 있음., @ModelAttribute는 타임리프, jsp에서 사용하고 싶을 때 사용, 
	public void pro_listForm(@ModelAttribute("cate_code") int cate_code, @ModelAttribute("cate_name") String cate_name, Criteria cri, Model model) throws Exception {
		
		cri.setAmount(9); //페이지 당 상품 9개씩 보여줌.
		
		log.info("2차 카테고리코드 : " + cate_code);
		log.info("2차 카테고리이름 : " + cate_name);
		
		List<ProductVO> pro_list = productService.pro_list(cate_code, cri);
		
		//클라이언트에 \를 /로 변환하여 model 작업 전 처리함. 2024\07\01 -> 2024/07/01
		pro_list.forEach(vo -> {
			vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/"));
		});
		
		
		int totalCount = productService.getCountProductByCategory(cate_code);
		
		model.addAttribute("pro_list", pro_list);
		model.addAttribute("pageMaker", new PageDTO(cri, totalCount));
	}
	
	@GetMapping("/image_display")
	public ResponseEntity<byte[]> image_display(String dateFolderName, String fileName) throws Exception {
		
		return FileManagerUtils.getFile(uploadPath + dateFolderName, fileName);
	}
	
	//상품정보 1
	@GetMapping("/pro_info")
	public ResponseEntity<ProductVO> pro_info(int pro_num) throws Exception {
		
		log.info("상품코드:" + pro_num);
		
		ResponseEntity<ProductVO> entity = null;
		
		//db연동 타임리프, jsp에서 정보를 보여줄 때는 모델작업을 하지만 ajax 작업에는 사용안함.
		ProductVO vo = productService.pro_info(pro_num);
		vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/"));
		
		entity = new ResponseEntity<ProductVO>(vo, HttpStatus.OK);
		
		return entity;
	}
	
	//상품정보 2
	@GetMapping("/pro_info_2")
	public void pro_info_2Form(int pro_num, Model model) throws Exception {
		
		log.info("상품코드 : " + pro_num);
		
		//db연동
		ProductVO vo = productService.pro_info(pro_num);
		vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/"));
		
		model.addAttribute("product", vo);
		
	}
	
	// 상품 상세설명
	@GetMapping("/pro_detail")
	public void pro_detail(int pro_num, Model model) throws Exception {
		ProductVO vo = productService.pro_info(pro_num);
		vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/"));
		
		model.addAttribute("product", vo);
		
		// 상품리뷰 model
		
		// Q&A model
	}

}
