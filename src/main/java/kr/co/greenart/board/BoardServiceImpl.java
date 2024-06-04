package kr.co.greenart.board;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.greenart.common.PageInfo;
import kr.co.greenart.member.MemberDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

	
	final private SqlSessionTemplate sqlsession; //mybatis에서 사용되는 내장 템플릿
	
	final private BoardRepository boardDao;
	
	
	// 전체 게시글 수 조회
	@Override
	public int selectListCount(int id) {
		return boardDao.selectListCount(sqlsession, id);
	}

	// 문의 목록 불러오기
	@Override
	public List<BoardDTO> selectListAll(PageInfo inquiryPi, int id ) {
		return boardDao.selectListAll(sqlsession, inquiryPi, id);
	}

	// 답변 목록 불러오기
	@Override
	public List<BoardDTO> selectAnswerAll(int id) {
		return boardDao.selectAnswerAll(sqlsession, id);
	}
	
	// 글 작성
	@Override
	public int insertInquiry(BoardDTO bo) {
		return boardDao.insertInquiry(sqlsession, bo);
	}
	
	// 문의 답변
	@Override
	public int inquiryAnswer(BoardDTO bo) {
		return boardDao.inquiryAnswer(sqlsession, bo);
	}

	// 문의에 답변 했을시 체크 업데이트
	@Override
	public int answerChkUpdate(BoardDTO bo) {
		return boardDao.answerChkUpdate(sqlsession, bo);
	}

	// 유저 문의하기 수정 페이지
	@Override
	public BoardDTO inquiryEditPage(int boardQuestionNum) {
		return boardDao.inquiryEditPage(sqlsession, boardQuestionNum);
	}

	// 유저 문의 업데이트
	@Override
	public int inquiryUpdate(BoardDTO bo) {
		return boardDao.inquiryUpdate(sqlsession, bo);
	}
	
	//유저 문의 삭제
	@Override
	public int inquiryDelete(BoardDTO bo) {
		return boardDao.inquiryDelete(sqlsession, bo);
	}

	//관리자 답변 수정 페이지
	@Override
	public BoardDTO answerEditPage(int boardQuestionNum) {
		return boardDao.answerEditPage(sqlsession, boardQuestionNum);
	}

	//관리자 답변 업데이트
	@Override
	public int answerUpdate(BoardDTO bo) {
		return boardDao.answerUpdate(sqlsession, bo);
	}

}
