package kr.co.greenart.review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.greenart.common.PageInfo;

@Service
public class ReviewService {
	
	@Autowired
	SqlSessionTemplate sql;
	
	@Autowired
	ReviewRepository reviewRepository;
	
	public int insertReview(ReviewDTO reviewDTO) {
		return reviewRepository.insertReview(sql,reviewDTO);
	}

	public List<ReviewDTO> reviewFindByProductId(PageInfo pi, int id) {
		return reviewRepository.reviewFindByProductId(sql,pi,id);
	}

	public ReviewDTO findReviewByMemberAndProduct(Map<String, Integer> map) {
		return reviewRepository.findReviewByMemberAndProduct(sql,map);
	}

	public ReviewDTO reviewFindByReviewId(int review_id) {
		return reviewRepository.reviewFindByReviewId(sql,review_id);
	}

	public int reviewUpdpate(ReviewDTO reviewDTO) {
		return reviewRepository.reviewUpdpate(sql,reviewDTO);
	}

	public int reviewDelete(int review_id) {
		return reviewRepository.reviewDelete(sql,review_id);
	}

	public int selectListCount(int id) {
		return reviewRepository.selectListCount(sql,id);
	}
	
	public int getStarCountById(Map<String, Integer> map) {
		return reviewRepository.getStarCountById(sql,map);
	}
	
	public List<Integer> getScoreNums(int id) {
		return IntStream.rangeClosed(1,5).mapToObj(x -> {
			Map<String,Integer> map = new HashMap<>();
			map.put("score", x);
			map.put("id",id);
			
			return reviewRepository.getStarCountById(sql, map);
			
		}).collect(Collectors.toList());
	}
	
	public double calculateAverageScore(List<Integer> scoreNums) {
		int countScore = 0;
		// 별점 갯수
		for(int i = 0 ; i < scoreNums.size(); i++) {
			countScore += scoreNums.get(i);
		}
		
		double sumScore = 0;

		// 별점 점수마다 곱한거
		for(int i = 1 ; i<=  5 ; i++) {
			sumScore += scoreNums.get(i-1) * i;
		}
		
		double averageScore = 0;
		
		// 평균 = 별점 점수 합계 / 별점 갯수
		if(countScore != 0) {
			averageScore=sumScore / countScore;
		}
		
		return averageScore;
		
	}
	
}
