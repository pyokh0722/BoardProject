package org.zerock.task;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zerock.domain.BoardAttachVO;
import org.zerock.mapper.BoardAttachMapper;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j

public class FileCheckTask {
	
	@Setter( onMethod_ = @Autowired )
	private BoardAttachMapper attachMapper;
	
	private String getFolderYesterday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		System.out.println("cal : "+ cal);
		String str = sdf.format(cal.getTime());
		System.out.println("cal.getTime : "+ cal.getTime());
		System.out.println("str : " + str);
		System.out.println(str.replace("-", File.separator));
		return str.replace("-", File.separator);
	}
	
	@Scheduled(cron="0 0/1 * * * *")
	public void checkFiles() throws Exception{
		log.warn("File Check Task run.................");
		log.warn(new Date());
		
		List<BoardAttachVO> fileList = attachMapper.getOldFiles();
		
		
		List<Path> fileListPaths = fileList.stream().map(vo -> Paths.get("/Users/pyomomo/Desktop/upload/",
				vo.getUploadPath(), vo.getUuid() + "_" + vo.getFileName())).collect(Collectors.toList());
		
		System.out.println("fileListPaths : " + fileListPaths);
		
		
		// 기존 패스 리스트에 썸네일 패스로 추가 시킨다.
		fileList.stream().filter(vo -> vo.isFileType() == true)
		.map(vo -> Paths.get("/Users/pyomomo/Desktop/upload/", vo.getUploadPath(), "s_" + vo.getUuid() + "_" + vo.getFileName()))
		.forEach(p -> fileListPaths.add(p)); 

		log.warn("===========================================");
		fileListPaths.forEach(p -> log.warn(p));
		 // files in yesterday directory
		File targetDir = Paths.get("/Users/pyomomo/Desktop/upload/", getFolderYesterday()).toFile();
		
		System.out.println("targee" + targetDir);
		

		
		File[] removeFiles = targetDir.listFiles(file -> fileListPaths.contains(file.toPath()) == false);
		System.out.println("listfiles : " + targetDir.listFiles().length);
		//File[] list = targetDir.listFiles(file -> fileListPaths.contains(file.toPath()) == true);

		for(int i = 0; i < removeFiles.length; i++) {
			System.out.println("리스트 : " + removeFiles[i]);
		}

//		for(int i = 0; i < removeFiles.length; i++) {
//			System.out.println("리무브 : " + removeFiles[i]);
//		}
//		
//		
//		log.warn("-----------------------------------------");
//		
		for (File file : removeFiles) {
			log.warn(file.getAbsolutePath());
			file.delete();
		}
	}
}
