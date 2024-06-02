package kr.co.greenart.product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import kr.co.greenart.board.model.dto.BoardDto;
import kr.co.greenart.board.model.service.BoardServiceImpl;
import kr.co.greenart.common.model.dto.PageInfo;
import kr.co.greenart.common.template.Pagination;
import kr.co.greenart.review.ReviewDTO;
import kr.co.greenart.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

	final private ProductService productService;

	final private ReviewService reviewService;

	final private BoardServiceImpl boardservice;
	
	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
	
	@GetMapping("/index")
	public String getIndexPage(HttpSession session, Model model) {
		
		logger.debug("hi");
		
		Map<String,List<ProductDTO>> mapProductDTO= productService.getProductByTypes(Arrays.asList(ProductType.values()));
		
		mapProductDTO.forEach(model::addAttribute);
		
		return "/product/index";
	}

	@GetMapping("/create")
	public String getCreatePage(HttpSession session, Model model) {

		String msg = (String) session.getAttribute("msg");

		session.removeAttribute("msg");

		model.addAttribute("msg", msg);

		return "/product/create";
	}

	@GetMapping("/detail")
	public String getDetailePage(HttpServletResponse response,
			@RequestParam(value = "product_id", required = true) int id, Model model,
			@RequestParam(value = "rpage", defaultValue = "1") int reviewCurrentPage,
			// q&a 페이지
			@RequestParam(value = "ipage", defaultValue = "1") int inquryCurrentPage, HttpSession session)
			throws IOException {

		// 상품을 가져온다
		ProductDTO productDTO = productService.productFindById(id);

		// 상품이 없다면 404 페이지 보여준다
		if (productDTO == null) {
			response.sendError(404, "404에러가 발생했습니다");
		}

		// 각 별점 마다 갯수 구하는 메서드
		List<Integer> scoreNums = reviewService.getScoreNums(id);
		// 구한 별점을 다 더한 값
		
		double averageScore = reviewService.calculateAverageScore(scoreNums);
		
//		model.addAttribute("sumScore", sumScore);
		model.addAttribute("averageScore", averageScore);


		model.addAttribute("scores", scoreNums);

		// 임시 유저 아이디
		Object idObject = session.getAttribute("memberIdx");
		int memberId = -1;

		if (session.getAttribute("memberIdx") != null && idObject instanceof Integer) {
			memberId = (Integer) idObject;
		}

		// qna 코드

		// 전체 게시글 구하기
		int listCount = boardservice.selectListCount(id);
		// 보여질 페이지 수
		int pageLimit = 5;
		// 한 페이지에 들어갈 게시글 수
		int boardLimit = 4;
		// 글의 번호를 뒤에서부터 보여주기 = 전체 게시글 수 - (현재페이지 -1 ) * 한 페이지에 보여줄 게시글 수
		int row = listCount - (inquryCurrentPage - 1) * boardLimit;

		PageInfo inquiryPi = Pagination.getPageInfo(listCount, inquryCurrentPage, pageLimit, boardLimit);

		List<BoardDto> list = boardservice.selectListAll(inquiryPi, id);

		System.out.println(list);
		
		List<BoardDto> answerList = boardservice.selectAnswerAll(id);
	
		System.out.println("answerList : "  + answerList);
		
		Object memberNameObj = session.getAttribute("memberName");
		String memberName = null;
	
		if (memberNameObj != null && memberNameObj instanceof String) {
			memberName = (String)memberNameObj;
		}
				
		model.addAttribute("answerList", answerList);
		model.addAttribute("boardList", list);
		model.addAttribute("boardRow",row);
		model.addAttribute("inquiryPi", inquiryPi);
		
		model.addAttribute("memberName",memberName);
		
		// qna

		int reviewListCount = reviewService.selectListCount(id);

		// 페이지 제한 수
		int reviewPageLimit = 5;
		// 리뷰 제한수
		int reviewLimit = 5;

		PageInfo pi = Pagination.getPageInfo(reviewListCount, reviewCurrentPage, reviewPageLimit, reviewLimit);

		// 상품아이디 와 같은 리뷰를 가져올때
		List<ReviewDTO> reviewList = reviewService.reviewFindByProductId(pi, id);

		Map<String, Integer> map = new HashMap<>();
		map.put("member_id", memberId);
		map.put("product_id", id);

		// 리뷰를 가져오는데 자기가 작성한 리뷰 를 가져온다.
		ReviewDTO curUserReviewDTO = reviewService.findReviewByMemberAndProduct(map);

		model.addAttribute("reviewPi", pi);

		model.addAttribute("reviewListCount", reviewListCount);

		model.addAttribute("reviewCurUser", curUserReviewDTO);

		model.addAttribute("reviewList", reviewList);

		model.addAttribute("product", productDTO);

		model.addAttribute("member_id", memberId);
		
		model.addAttribute("product_id",id);
		
		return "/product/detail";
	}

	@PostMapping("/create")
	public String postCreatePage(ProductDTO product,
			@RequestParam(name = "images", defaultValue = "", required = false) MultipartFile[] upload,
			HttpServletRequest request, Model model, HttpSession session) throws IOException {

		List<String> filePathList = reviewService.uploadFile(upload, request.getSession().getServletContext().getRealPath("/"));

		if (filePathList.size() != 0) {
			String filePathStr = String.join(",", filePathList);
			product.setProduct_image_group(filePathStr);
		}

		if (product.getProduct_image_group() == null) {
			session.setAttribute("msg", "이미지가 없습니다.");
			return "redirect:/product/create";
		}

		productService.insertProduct(product);
		int product_id = product.getProduct_id();

		return "redirect:/product/detail?product_id=" + product_id;
	}
}
