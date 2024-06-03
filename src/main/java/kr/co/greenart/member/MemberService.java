package kr.co.greenart.member;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

public interface MemberService {
	
	
	//로그인
	MemberDTO loginMember(MemberDTO m);
	
	//sns(이메일)로그인
	MemberDTO snsLoginMember(MemberDTO m);
	
	//아이디 체크
	int checkId(String id);
	
	//이메일 체크
	int checkEmail(String email);
	
	//회원 가입
	int signupMember(MemberDTO memberDto);
	
	//id찾기
	String findId(MemberDTO memberdto);
	
	//pw찾기(사용 안함)
	String findPw(MemberDTO memberdto);
	
	//pw찾기(회원번호 가져오기)
	String findIdx(MemberDTO memberdto);
	
	//pw변경
	int changePw(MemberDTO memberdto);
	
	
	//마이페이지 보기
	MemberDTO myPage(int memberIdx);
	
	//마이페이지 업데이트
	int updateMyPage(MemberDTO memberdto);
	
	
	
	//이메일 인증
	public void memberAuth(String memberEmail, String key) throws Exception;
	
	//이메일 발송
	public void sendMail(MemberDTO memberdto) throws Exception;
	
	
	
	//sns 로그인, 가입
	int snsSingup(MemberDTO memberdto);
	
	
	//카카오 로그인
	String getAccessTokenKakao(String authorize_code) throws Throwable;
	
	public HashMap<String, Object> getUserInfoKakao(String access_Token) throws Throwable;
	
	//네이버 로그인
	String getAccessTokenNaver(String authorize_code) throws Throwable;
	
	public HashMap<String, Object> getUserInfoNaver(String access_Token) throws Throwable;
	
	
	//회원 탈퇴
	int memberDelete(MemberDTO bo);
	
	//회원 탈퇴 ( 이메일 인증 삭제 )
	int memberAuthDelete(MemberDTO bo);
	
}
