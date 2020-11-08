package org.zerock.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.domain.AttachFileDTO;

import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnailator;

@Controller
@Log4j
public class UploadController {
	
	@GetMapping("/uploadForm")
	public void uploadForm() {
		log.info("upload form");
	}
	
	@PostMapping("/uploadFormAction")
	public void uploadFormPost(MultipartFile[] uploadFile, Model model) {
		
		System.out.println(uploadFile + "???????");
		
		String uploadFolder = "/Users/pyomomo/Desktop/upload";
		
	     for (MultipartFile multipartFile : uploadFile) {
			log.info("-------------------------------------");
			log.info("Upload File Name: " +multipartFile.getOriginalFilename()); 
			log.info("Upload File Size: " +multipartFile.getSize());
			
			File saveFile = new File(uploadFolder, multipartFile.getOriginalFilename());
			
			try {
				multipartFile.transferTo(saveFile);
			}catch (Exception e) {
				log.error(e.getMessage());
			}
	     }
	     
	}
	
	@GetMapping("/uploadAjax")
	public void uploadAjax() {
		
		log.info("upload ajax");
	}
	
	private String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String str = sdf.format(date);
		log.info("str : "+str);
		return str.replace("-", File.separator); 
	}
	
	private boolean checkImageType(File file) throws IOException {
		
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		
		String mimeType = mimeTypesMap.getContentType(file);
		log.info("mimeType :" + mimeType);
		
		if(mimeType.contains("image")) {
			log.info("mimeType.contains(\"image\")" + mimeType.contains("image"));
			return true;
		}else {
			return false;
		}	
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping(value = "/uploadAjaxAction", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<AttachFileDTO>> uploadAjaxPost(MultipartFile[] uploadFiles) {
		
		log.info("---update ajax post---");
		List<AttachFileDTO> list = new ArrayList<AttachFileDTO>();
		String uploadFolder = "/Users/pyomomo/Desktop/upload";
		
		String uploadFolderPath = getFolder();
		log.info("uploadFolderPath : " + uploadFolderPath);
		File uploadPath = new File(uploadFolder, uploadFolderPath);
	
		log.info("uploadPath : " + uploadPath);
		log.info("getFolder() : " + getFolder());
		
		if (uploadPath.exists() == false) {
			uploadPath.mkdirs();  //폴더 생성
		}
		
		for(MultipartFile multipartFile : uploadFiles) {
			
			AttachFileDTO attachDTO = new AttachFileDTO();
			
			log.info("-------------------------------------");
			log.info("Upload File Name: " + multipartFile.getOriginalFilename());
			log.info("Upload File Size: " + multipartFile.getSize());
			
			String uploadFileName = multipartFile.getOriginalFilename();
			
			attachDTO.setFileName(uploadFileName);
			
			UUID uuid = UUID.randomUUID();
			log.info("uuid : " + uuid);
			uploadFileName = uuid.toString() + "_" + uploadFileName;
			
			//File saveFile = new File(uploadFolder, uploadFileName);

			try {
				File saveFile = new File(uploadPath, uploadFileName);
				log.info("saveFile : "+saveFile);
				multipartFile.transferTo(saveFile);
				
				attachDTO.setUuid(uuid.toString());
				attachDTO.setUploadPath(uploadFolderPath);
				
				
				// check image type file
				if(checkImageType(saveFile)) {
					log.info("체크 타입에 들어옴");
					attachDTO.setImage(true);
					
					FileOutputStream thumbnail = 
							new FileOutputStream(new File(uploadPath, "s_"+uploadFileName));
					log.info(thumbnail);
					log.info(multipartFile.getInputStream());
					Thumbnailator.createThumbnail(multipartFile.getInputStream(), 
							thumbnail, 500, 500);
					
					thumbnail.close();
				}
				
				list.add(attachDTO);

			} catch (Exception e) {
				log.info("에러 메세지 : "+e.getMessage());

			}	
		}
		return new ResponseEntity<List<AttachFileDTO>>(list,HttpStatus.OK);	
	}
	
	@GetMapping("/display")
	@ResponseBody
	public ResponseEntity<byte[]> getFile(String fileName){
		
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		
		log.info("fileName: " + fileName);
		File file = new File("/Users/pyomomo/Desktop/upload/" + fileName);
		log.info("file: " + file);
		
		ResponseEntity<byte[]> result = null;
		
		try {
			HttpHeaders header = new HttpHeaders();
			
			header.add("content-Type", mimeTypesMap.getContentType(file));
			//header.add("content-Type", Files.probeContentType(file.toPath()));
			log.info(header);
			result = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result; 
	}
	
	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(String fileName){
		log.info("downloadFile : " + fileName);
		
		Resource resource = new FileSystemResource("/Users/pyomomo/Desktop/upload/" + fileName);
		
		log.info("resource : "+ resource);
		
		String resourceName =  resource.getFilename();
		
		log.info("resourceName : " + resourceName);
		
		// remove UUID
		String resourceOriginalName = resourceName.substring(resourceName.indexOf("_")+1);
		
		HttpHeaders headers = new HttpHeaders();
		
		try {
			String downloadName = null;
			
			downloadName = new String(resourceOriginalName.getBytes("UTF-8"), "ISO-8859-1");
			
			headers.add("Content-Disposition",	"attachment; filename=" 
		+ downloadName);
			
			log.info("downloadName: " + downloadName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}
	
	@PostMapping("/deleteFile")
	@ResponseBody
	public ResponseEntity<String> deleteFile(String fileName, String type) throws UnsupportedEncodingException{
		
		log.info("deleteFile : " + fileName);
		log.info("deleteFile type : " + type);
		
		File file;
		
		log.info("decoder : " + URLDecoder.decode(fileName));
		file = new File("/Users/pyomomo/Desktop/upload/" + URLDecoder.decode(fileName));
		// 섬네일 파일 삭제
		file.delete();
		
		if(type.equals("image")) {
			String largeFileName = file.getAbsolutePath().replace("s_", "");
			log.info("absolutePath : " + file.getAbsolutePath());
			log.info("absolutePath replace : " + file.getAbsolutePath().replace("s_", ""));
			
			file = new File(largeFileName);
			file.delete();
		}
		return new ResponseEntity<String>("deleted", HttpStatus.OK);		
	}

}
