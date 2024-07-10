package kr.co.greenart.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileStorageUtil {

	public static List<String> saveFiles(MultipartFile[] files,String directoryPath) throws IOException {
		
		// 파일명을 담을 배열 
		List<String> filePathList = new ArrayList<>();
		
		for(MultipartFile file :files) {
			if(!file.isEmpty()) {
 
				String fileName = generateFileName(file.getOriginalFilename());
				
				// 파일에 디렉터리 경로
				String fullPath = directoryPath + File.separator + "resources/upload/" + fileName ;
				
				// 디렉터리 경로를 저장
				File uploadDir = new File(directoryPath + File.separator + "resources/upload");
				
				
				
				// 경로가 폴더가 존재하는지 확인하는
				if(!uploadDir.exists()) {
					// 폴더를 새로생성
					uploadDir.mkdir();
				}
				
				try (InputStream is = file.getInputStream();
						OutputStream os = new FileOutputStream(fullPath)) {
					
					// 파일을 하나 만드는 코드같음
					FileCopyUtils.copy(is, os);
				
					filePathList.add("/resources/upload/" + fileName);
				} catch(IOException e) {
					throw new IOException("파일 업로드에 실패하였습니다.",e);
				}
			}
		}
		
		return filePathList;
	}

	public static String generateFileName(String originalFileName) {
		// 파일에 이름을 시간으로 바꾸고 확장자를 붙여서 반환함
		String fileType = originalFileName.substring(originalFileName.lastIndexOf("."));
		return System.currentTimeMillis() + fileType;
	}
	
}
