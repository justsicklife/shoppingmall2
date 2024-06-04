package kr.co.greenart.board;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.greenart.common.PageInfo;
import kr.co.greenart.common.Pagination;
import kr.co.greenart.member.MemberDTO;
import kr.co.greenart.member.MemberService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

	final private BoardServiceImpl boardservice;

	// "문의하기" 버튼을 누르면 자바스크립트 openWrite()이 받아서 inquiry_write.jsp로 포워딩
	@GetMapping("/enrollForm.do")
	public String enrollForm(HttpSession session, Model model, @RequestParam("product_id") int productId) {

		model.addAttribute("productId", productId);

		return "/board/inquiry_write";
	}

	// 유저 글 작성
	@PostMapping("insert.do")
	@ResponseBody
	public String insertInquiry(BoardDTO boardDto, HttpSession session) {

		String memberId = (String) session.getAttribute("memberId");
		int memberIdx = (Integer)session.getAttribute("memberIdx");

		boardDto.setBoardMemberId(memberId);
		boardDto.setBoardMemberIdx(memberIdx);

		int result = boardservice.insertInquiry(boardDto);

		if (result > 0) {
			return null;
		} else {
			return null;
		}
	}


	// 관리자 답변 페이지
	// "답변" 버튼을 누르면 자바스크립트 answerForm()이 받아서 admin_write.jsp로 포워딩
	@GetMapping("/answerForm.do")
	public String answerForm(BoardDTO boardDto, HttpSession session, Model model) {
		try {

			String memberId = (String) session.getAttribute("memberId");

			if (boardDto.getBoardQuestionNum() != 0) {
				// 문자열을 정수로 변환
				int parsedQuestionNum = boardDto.getBoardQuestionNum();

				// 변환된 값을 DTO에 설정
				boardDto.setBoardQuestionNum(parsedQuestionNum);
				System.out.println("boardQuestionNum: " + parsedQuestionNum);

				System.out.println("boardProductNum : " + boardDto.getBoardProductNum());

				// 임시 접속자 ( 합치고 수정할것 )
				boardDto.setBoardMemberId(memberId);
				System.out.println("boardMemberId: " + memberId);
				model.addAttribute("user", memberId);
				model.addAttribute("boardQuestionNum", boardDto.getBoardQuestionNum());
				model.addAttribute("boardProductNum", boardDto.getBoardProductNum());

				return "/board/admin_write";
			} else {
				System.out.println("boardQuestionNum 전송 되지 않음");
				// boardQuestionNum이 전송되지 않은 경우 처리
				// 원하는 방식으로 처리할 수 있도록 수정
				return "error"; // 혹은 다른 오류 처리 페이지로 리다이렉트 등
			}
		} catch (NumberFormatException e) {
			// 정수로 변환할 수 없는 경우 처리
			// 예: 클라이언트가 숫자가 아닌 값을 전송한 경우
			// 원하는 방식으로 처리할 수 있도록 수정
			return "error"; // 혹은 다른 오류 처리 페이지로 리다이렉트 등
		}
	}

	// 관리자 문의 답변 ( 테이블 1개에 기본키 2개 사용해서(질문,답변 유/무) INSERT 하면 무결성 제약 뜨는데 해결함, 코드 수정시 말
	// 할 것 )
	@PostMapping("/answer.do")
	@ResponseBody
	public String inquiryAnswer(BoardDTO boardDto, HttpSession session, Model model) {

		String memberId = (String) session.getAttribute("memberName");
		int memberIdx = (Integer) session.getAttribute("memberIdx");

		boardDto.setBoardMemberId(memberId);
		boardDto.setBoardMemberIdx(memberIdx);

		System.out.println(boardDto);

		try {
			int result = boardservice.inquiryAnswer(boardDto);

			int answerChkUpdate = boardservice.answerChkUpdate(boardDto);
			
			return "success";

		} catch (Exception e) {
			System.out.println("catch 실행");
			return "error";
		}
	}

	// 유저 문의 수정 페이지
	@GetMapping("/inquiryEdit.do")
	public String inquiryEditForm(@RequestParam(value = "boardQuestionNum") int boardQuestionNum, Model model,
			HttpSession session, BoardDTO boardDto) {

		BoardDTO result = boardservice.inquiryEditPage(boardQuestionNum);

		String memberId = (String) session.getAttribute("memberName");
		int memberIdx = (Integer) session.getAttribute("memberIdx");

		if (!Objects.isNull(result)) {
			model.addAttribute("detail", result);
			// 임시 아이디 넘버
			model.addAttribute("user", memberIdx);

			return "/board/inquiry_update";
		} else {

			return "error";
		}
	}

	// 유저 문의 업데이트
	@PostMapping("/inquiryUpdate.do")
	@ResponseBody
	public String inquiryUpdate(BoardDTO boardDto, HttpSession session) {

		int result = boardservice.inquiryUpdate(boardDto);

		if (result > 0) {
			System.out.println("문의 업데이트 성공");
			return "success";
		} else {
			System.out.println("문의 업데이트 실패");
			return "error";
		}

	}

	// 유저 문의 삭제
	// 관리자 답변이 존재하면 답변도 같이 삭제함
	@PostMapping("/inquiryDelete.do")
	@ResponseBody
	public String inquiryDelete(@RequestBody BoardDTO boardDto, HttpSession session) {

		System.out.println("문의 삭제 boardDto : " + boardDto);
		System.out.println("문의 삭제 boardrDto.getBoardQuestionNum() : " + boardDto.getBoardQuestionNum());

		int result = boardservice.inquiryDelete(boardDto);

		if (result > 0) {
			System.out.println("문의 삭제 성공");
			return "success";
		} else {
			System.out.println("문의 삭제 실패");
			return "error";
		}
	}

	// 관리자 답변 수정 페이지
	@GetMapping("/answerEditForm.do")
	public String answerEditForm(BoardDTO boardDto , Model model,
			HttpSession session) {

		BoardDTO result = boardservice.answerEditPage(boardDto.getBoardQuestionNum());

		String memberId = (String) session.getAttribute("memberName");

		if (!Objects.isNull(result)) {
			model.addAttribute("detail", result);
			// 임시 아이디
			model.addAttribute("user", memberId);
			model.addAttribute("boardQuestionNum", boardDto.getBoardQuestionNum());
			model.addAttribute("boardProductNum", boardDto.getBoardProductNum());				
			
			return "/board/admin_update";
		} else {

			return "error";
		}
	}

	// 관리자 답변 업데이트
	@PostMapping("/answerUpdate.do")
	@ResponseBody
	public String answerUpdate(BoardDTO boardDto, HttpSession session) {
			int result = boardservice.answerUpdate(boardDto);

			if (result > 0) {
				System.out.println("관리자 답변 업데이트 성공");
				return "success";
			} else {
				System.out.println("관리자 답변 업데이트 실패");
				return "error";
			}
	}
}
