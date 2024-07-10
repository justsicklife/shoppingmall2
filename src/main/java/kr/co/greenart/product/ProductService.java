package kr.co.greenart.product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import kr.co.greenart.common.FileStorageUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	
	final private SqlSessionTemplate sql;
	
	final private ProductRepository productRepository;
		
	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	
	public Map<String,List<ProductDTO>> getProductByTypes(List<ProductType> types) {
		Map<String,List<ProductDTO>> productsByType = new HashMap<>();
		
		types.forEach(type -> {
			List<ProductDTO> productList = productRepository.productFindByType(sql, type.getLabel());
			
			productList.forEach(product -> {
				product.setProduct_image(product.getProduct_image_group().split(",")[0]);
			});
			
			productsByType.put(type.getLabel(), productList);
			
		});

		logger.info("getProductByTypes");
		
		return productsByType;
	}
	
	public int insertProduct(ProductDTO productDTO) {
		return productRepository.insertProduct(sql,productDTO);
	}
	
	public ProductDTO productFindById(int id) {
		return productRepository.productFindById(sql,id);
	}
	
	public List<String> uploadFile(MultipartFile[] upload,String filePath) throws IOException {
		List<String> filePathList = FileStorageUtil.saveFiles(upload, filePath);
		return filePathList;
	}

}
