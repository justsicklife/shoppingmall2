package kr.co.greenart.board;

import java.util.List;

import kr.co.greenart.common.PageInfo;
import kr.co.greenart.member.MemberDTO;

public interface BoardService {
	
	// 전체 게시글 수 조회
	int selectListCount(int id);
	
	// 문의 목록 불러오기
	List<BoardDTO> selectListAll(PageInfo inquiryPi, int id );
	
	// 답변 목록 불러오기
	public List<BoardDTO> selectAnswerAll(int id);
	
	// 글 작성
	public int insertInquiry(BoardDTO bo);
	
	// 문의 답변
	public int inquiryAnswer(BoardDTO bo);
	
	// 문의에 답변 했을시 체크 업데이트
	public int answerChkUpdate(BoardDTO bo);
	
	// 유저 문의하기 수정 페이지
	BoardDTO inquiryEditPage(int boardQuestionNum);
	
	// 유저 문의 업데이트
	int inquiryUpdate(BoardDTO bo);
	
	//유저 문의 삭제
	int inquiryDelete(BoardDTO bo);
	
	//관리자 답변 수정 페이지
	BoardDTO answerEditPage(int boardQuestionNum);
	
	//관리자 답변 업데이트
	int answerUpdate(BoardDTO bo);
}
