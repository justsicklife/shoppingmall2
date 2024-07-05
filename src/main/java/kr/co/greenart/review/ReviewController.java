package kr.co.greenart.review;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
	
	final private ReviewService reviewService;
	
	@GetMapping("/create")
	public String getReviewCreatePage(
			@RequestParam("member_id") int member_id,
			@RequestParam("product_id") int product_id,
			Model model,
			HttpSession session
			) {
		
		log.info("getReviewCreatePage");
			
		Object obj =  session.getAttribute("memberId");
		
		String memberId= null;
		
		if(obj != null && obj instanceof String) {
			memberId = (String) obj;
		}
		
		
		model.addAttribute("product_id",product_id);
		model.addAttribute("member_idx",member_id);
		model.addAttribute("member_id",memberId);
		
		return "/review/create";
	}
	
	
	@PostMapping("/create")
	public String postReviewCreatePage(ReviewDTO reviewDTO) {
		
		log.info("review Create");
		
		int succecss = reviewService.insertReview(reviewDTO);

		return "redirect:/product/detail?product_id=" + reviewDTO.getProduct_id();
	}
	
	@GetMapping("/update")
	public String getUpdatePage(
			Model model,
			@RequestParam("review_id") int review_id) {
		
		log.info("UpdatePage");
		
		ReviewDTO review = reviewService.reviewFindByReviewId(review_id);
		
		model.addAttribute("review",review);
		
		return "/review/update";
	}
	
	@PostMapping("/update")
	public String postUpdate( ReviewDTO reviewDTO) {
		
		log.info("review Update");
		
		int success = reviewService.reviewUpdpate(reviewDTO);
		
		return "redirect:/product/detail?product_id=" + reviewDTO.getProduct_id();	
	}
	
	@PostMapping("/delete")
	public String postDelete(ReviewDTO reviewDTO) {
	
		log.info("delete review");
		
		int success = reviewService.reviewDelete(reviewDTO.getReview_id());
			
		return "redirect:/product/detail?product_id=" + reviewDTO.getProduct_id();
	}
}