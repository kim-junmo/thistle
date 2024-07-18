package com.docmall.basic.commom.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

//@Component //스프링에서 클래스를 자동 관리해준다. 
public class FileManagerUtils {

	//날짜별로 업로드 날짜에 맞게 폴더를 생성.
	//기능: 현재 폴더를 운영체제 별 맞게 문자열로 반환하는 기능.
	public static String getDateFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //날짜포멧 패턴을 만들때 사용하는 형식
		Date date = new Date(); //오늘 날짜 정보
		
		String str = sdf.format(date); //"2024-05-16" 폴더명 문자열이 str에 저장됨.
		
		//File.separator : 이 코드를 실행하는 운영체제별로 파일의 경로 구분자를 리턴.
		/*
		 - 유닉스, 맥, 리눅스 구분자 : / 예>"2024-05-16"를 찾아서 "2024/05/16"으로 변환한다.
		 - 윈도우즈 구분자 : \(윈도우즈는 전통적으로 역슬래시를 사용한다) 예>"2024-05-16"를 찾아서 "2024\05\16"으로 변환
		 */
		
		return str.replace("-", File.separator);
	}
	
	//기능: 파일을 업로드하는 메서드
	/* 경로는 에러가 발생하여 역슬래시 2번을 사용했음. 원래 한개가 맞음
	 String uploadFolder : 업로드 폴더명 : "C:\\dev\\upload\\pds" 
	 String dateFolder : 업로드 되는 날짜 폴더명 : "2024\\05\\16"
	 MultipartFile uploadFile : 클라이언트에서 전송한 파일
	 */
	
	public static String uploadFile(String uploadFolder, String dateFolder, MultipartFile uploadFile) {
		
		String realUploadFileName = ""; //실제 업로드한 파일명.
		
		// File클래스 : JDK제공. 파일과 폴더 관련 기능을 제공
		/*
		File file = new File(파일또는 폴더 정보를 구성한다.); file.명령어(속성과 메서드)
		-파일 또는 폴더 존재여부 확인가능,
		-존재하지 않으면 파일 또는 폴더 생성,
		-존재하면 파일 또는 폴더 속성 확인가능
		*/
		
		//예> "C:/dev/upload/pds"(고정) + "2024/05/16"(동적(날짜가 변경되면 새로 폴더가 만들어진다))
		//업로드 할 폴더 File객체 
		File file = new File(uploadFolder, dateFolder);
		
		//"2024/05/16"폴더가 존재하지 않으면 폴더 생성하자.
		//새로운 날짜에 첫번째 파일 업로드가 진행이 되면 폴더가 생성되고, 
		//두번째 파일 업로드 부터는 폴더가 생성되지 않게 된다.
		if(file.exists() == false) {
			file.mkdirs(); // "C:/dev/upload/pds2024/05/16" 개발할때는 mkdir을 사용하지 않고 mkdirs를 사용함.
		}
		
		//클라이언트에서 보낸 파일명
		String ClientFileName = uploadFile.getOriginalFilename(); //abc.png
		UUID uuid = UUID.randomUUID(); // 2f48f241-9d64-4d16-bf56-70b9d4e0e79a (중복성은 있지만 희박함)
		
		//2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.png
		realUploadFileName = uuid.toString() + "_" + ClientFileName;
		
		//예외처리작업
		try {
			File saveFile = new File(file, realUploadFileName);
			uploadFile.transferTo(saveFile); //파일복사(업로드라고 한다, 원본)
			
			//Thumnail(썸네일) 작업 checkImageType: 없는 메서드인데 호출을 먼저 작성한 후 메서드를 만든다. 
			//원본파일에서 해상도 크기를 줄여 섬네일 이미지 생성하기
			if(checkImageType(saveFile)) {
				//Thumnail파일명 : s_2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.png 생성
				//thumnailFile객체 : "C:/dev/upload/pds2024/05/16" + "s_2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.png"
				File thumnailFile = new File(file, "s_" + realUploadFileName);
				
				//saveFile 객체는 이미 업로드 된 파일
				BufferedImage bo_img = ImageIO.read(saveFile);
				//이미지 파일을 줄인다.
				double ratio = 3;
				int width = (int) (bo_img.getWidth() / ratio);
				int heigth = (int) (bo_img.getHeight() / ratio);
				
				Thumbnails.of(saveFile)
						 .size(width, heigth)
						 .toFile(thumnailFile);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return realUploadFileName; // 업로드되는 실제 파일명을 리턴한다. 2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.png
	}

	//기능: 업로드 파일의 MINE타입 확인, 즉 이미지파일인지 일반파일 여부를 체크
	public static boolean checkImageType(File saveFile) {

		boolean isImageType = false;
		
		try {
			//MINE : text/html, text/plain, image/jpeg, image/png ...... 
			//클라이언트에서 전송한 파일의 MINE정보를 추출한다.
			String contentType = Files.probeContentType(saveFile.toPath());
			
			//contentType변수의 내용이 "image" 문자열 시작여부 boolean값 변환
			isImageType = contentType.startsWith("image");
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return isImageType;
	}
	
	//기능: 이미지 파일을 웹브라우저 화면에 보이는 작업. 매핑주소를 요청해서 바이트별로 가저오게 된다.
	// <img src="abc.gif"> <img src="매핑주소"> 매핑 주소를 통한 서버측에서 받아오는 바이트 배열을 이용하여 브라우저가 이미지를 표시한다.
	/*
	 String uploadPath : 서버 업로드 폴더 예)"C:/dev/upload/pds"
	 String fileName: 날짜 폴더명까지 포함된 이미지 파일명
	 */
	//파일 업로드가 되는 폴더가 프로젝트 외부에 존재하여, 보안적인 이슈가 있으므로, 업로드 파일들을 바이트배열로 읽어서 클라이언트로 보낸다.
	public static ResponseEntity<byte[]>getFile(String uploadPath, String fileName) throws Exception { //리턴타입이 바이트 배열이다.
		ResponseEntity<byte[]>entity = null;
		
		File file = new File(uploadPath, fileName);
		
		if(!file.exists()) {
			return entity;
		}
		
		HttpHeaders headers = new HttpHeaders();
		//Files.probeContentType(file.toPath())); : Mine Type정보 예:image/jpeg
		headers.add("Content-Type", Files.probeContentType(file.toPath()));
		
		entity = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
		return entity;
	}
	
	//기능: 이미지파일 삭제
	/*
	 String uploadPath : 서버 업로드 폴더 경로
	 String folderName : 날짜폴더명
	 String fileName : 파일명 (날짜 폴더명 포함)
	 */
	public static void delete(String uploadPath, String dateFolderName, String fileName, String type) {
		
		//2)원본파일 삭제 fileName.substring(2) : (문자열인 s_를 자르고 원본파일명을 만들었다.)2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.png
		File file2 = new File((uploadPath + "\\" + dateFolderName + "\\" + fileName.substring(2)).replace('\\', File.separatorChar));
		if(file2.exists()) file2.delete();
		
	
		if(type.equals("image")) {
	
		//1) thumnail 섬네일 파일 삭제 : "C:\\dev\\upload\\pds" "2024\\05\\16" s_2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.png
		File file1 = new File((uploadPath + "\\" + dateFolderName + "\\" + fileName).replace('\\', File.separatorChar));
		if(file1.exists()) file1.delete();
		
		}
		
	}


		
}
