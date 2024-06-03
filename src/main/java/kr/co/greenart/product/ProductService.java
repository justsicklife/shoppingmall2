package kr.co.greenart.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
			// type 순서대로 가져옴
			List<ProductDTO> productList = productRepository.productFindByType(sql, type.getLabel());
			
			// 첫번째 이미지를 대표로 가져옴
			// 1차 정규화를 했으면 좋았을 텐데
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

}
