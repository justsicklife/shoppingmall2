package kr.co.greenart.member;

import java.io.File;
import java.lang.System.Logger;
import java.lang.annotation.Retention;
import java.sql.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.proxy.annotation.Post;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

	final private MemberServiceImpl memberService;

	final private BCryptPasswordEncoder bcryptPasswordEncoder;

	// id찾기 페이지
	// http://localhost/member/searchId
	@GetMapping("/searchId")
	public String searchId() {
		log.info("searchId");
		return "/member/search_id";
	}

	// id찾기
	@PostMapping("/findId.do")
	public String findId(@RequestParam("memberName") String memberName, @RequestParam("memberEmail") String memberEmail,
			Model model) throws Exception {

		log.info("findId");
		
		MemberDTO memberdto = new MemberDTO();
		memberdto.setMemberName(memberName);
		memberdto.setMemberEmail(memberEmail);

		model.addAttribute("memberId", memberService.findId(memberdto));
		return "/member/find_id";

	}

	// pw찾기 페이지
	// http://localhost/member/searchPw
	@GetMapping("/searchPw")
	public String searchPw() {
		return "/member/search_pw";
	}

	// pw찾기
	@PostMapping("/findPw.do")
	public String findPw(@RequestParam("memberId") String memberId, @RequestParam("memberEmail") String memberEmail,
			Model model) throws Exception {

		MemberDTO memberdto = new MemberDTO();
		memberdto.setMemberId(memberId);
		memberdto.setMemberEmail(memberEmail);

		model.addAttribute("memberIdx", memberService.findIdx(memberdto));
		log.info("memberIdx, memberSercvice.findIdx(memberdto) : " + memberService.findIdx(memberdto));

		return "/member/change_pw";
	}

	// pw변경
	@PostMapping("/changePw.do")
	public String changePw(HttpSession session, MemberDTO memberdto) {

		// 유효성 검사
		String password = memberdto.getMemberPassword();
		String passwordChk = memberdto.getMemberPasswordChk();
		String passwordRegex = "^(?=.*[a-zA-Z])(?=.*[@$!%*?&\\#])[A-Za-z\\d@$!%*?&\\#]{8,20}$";

		log.info("member :" + memberdto);
		log.info("password : " + password);

		if (password.matches(passwordRegex) && password.equals(passwordChk)) {

			log.info("중복 확인 및 유효성 검사 완료");
			log.info("password : " + password);
			log.info("savePw :" + memberdto.getMemberPassword());

			// 패스워드 암호화
			String bcryPassword = bcryptPasswordEncoder.encode(memberdto.getMemberPassword());
			memberdto.setMemberPassword(bcryPassword);
			log.info("bcryPassword : " + bcryPassword);
			log.info("member :" + memberdto);

			// update 서비스 실행
			int result = memberService.changePw(memberdto);
			log.info("result : " + result);
			log.info("update_member :" + memberdto);

			log.info("비밀번호 변경 완료");
			return "/signin/signin";
		} else {
			log.info("비밀번호 변경 실패, 유효성 검사 불일치");
			return "/shop/index"; // 임시로 메인 페이지 이동 ( error )
		}
	}

	// 메인 페이지
	// http://localhost/product/index
//	@GetMapping("/indexPage")
//	public String getIndexPage() {
//		return "/product/index";
//	}

	// 로그인 페이지 (임시)
	// http://localhost/member/loginPage
	// https://nid.naver.com/internalToken/view/tokenList/pc/ko (네이버 가입 삭제 링크)
	// https://accounts.kakao.com/login/?continue=https%3A%2F%2Faccounts.kakao.com%2Fweblogin%2Faccount%2Fpartner#login
	// (카카오 가입 삭제 링크)
	@GetMapping("loginPage")
	public String loginPage() {
		return "/signin/signin";
	}

	// 로그아웃
	@GetMapping("/logout.do")
	public String logoutMember(HttpSession session, Model model) {
		session.invalidate();
		log.info("로그아웃");
		return "redirect:/product/index";
	}

	// 로그인
	@PostMapping("/login.do")
	public String getLoginPage(MemberDTO memberDto, HttpSession session, Model model) {

		MemberDTO loginUser = memberService.loginMember(memberDto);

		// Objects.isNull(loginUser) = null : true
		// ! null : false 논리 부정 사용했으니 비어있다면 false
		//bcryptPasswordEncoder 유저가 입력한 비밀번호 , db에서 불러올 암호화된 비밀번호 순서 지켜야함
		// getMemberPassword - 사용자 입력 패스워드 / getMemberPassword - db에 자정된 패스워드
		if (!Objects.isNull(loginUser)
				&& bcryptPasswordEncoder.matches(memberDto.getMemberPassword(), loginUser.getMemberPassword())
				&& loginUser.getMemberAuth() == 1) {

			log.info("로그인 성공");
			log.info("유저가 입력한 비밀번호 : " + memberDto.getMemberPassword());

			session.setAttribute("memberIdx", loginUser.getMemberIdx());
			session.setAttribute("memberId", loginUser.getMemberId());
			session.setAttribute("memberName", loginUser.getMemberName());
			session.setAttribute("memberEmail", loginUser.getMemberEmail());
			
			log.info("id : " +loginUser.getMemberId());
			log.info("name : " + loginUser.getMemberEmail());
			
			return "redirect:/product/index";
		} else if (Objects.isNull(loginUser)
				|| !bcryptPasswordEncoder.matches(memberDto.getMemberPassword(), loginUser.getMemberPassword())) {
			log.info("로그인 실패");
			model.addAttribute("loginError", true); // 로그인 실패를 나타내는 속성 추가 (signin.jsp 스크립트 실행)
			return "/signin/signin";
		} else if (loginUser.getMemberAuth() == 0) {
			return "/member/registerReady";
		} else {
			log.info("로그인 에러");
			return "error";

		}
	}

	// 마이페이지 보기
	// http://localhost/member/editMyPage
	// http://localhost/member/editMyPage?memberIdx=(memberIdx 값)
	@GetMapping("/editMyPage")
	public String getMyPage(Model model, HttpSession session) {
		
		if (session.getAttribute("memberIdx") == null) {
			return "common/error404";
		} else {
			int idx = (int) session.getAttribute("memberIdx");
			MemberDTO result = memberService.myPage(idx);

			if (!Objects.isNull(result)) {
				// 뷰의 myPage 객체로 / <c:when test="${myPage.
				model.addAttribute("myPage", result);
				log.info("result : " + result);

				// session에서 받은 memberIdx를 뷰의 user로 초기화 / <c:when test="${myPage.memberIdx == user}">
				model.addAttribute("user", idx);
				log.info("user : " + idx);

				return "/member/mypage";
			} else {
				return "/member/mypage";
			}
		}
	}

	// 마이페이지 수정
	@PostMapping("/updateMyPage.do")
	@ResponseBody
	public String editMyPage(MemberDTO memberdto, HttpSession session) {
		
		int result = memberService.updateMyPage(memberdto);
		log.info("result : " + memberService.updateMyPage(memberdto));
		
		if (result > 0) {
			log.info("회원 정보 수정 완료");
			return "success"; // ajax로 수정 성공 메시지 리턴
		} else {
			return "fail"; // ajax로 수정 실패 메시지 리턴
		}

	}

	// 가입된 유저 체크
	// ResponseBody 쓰지 않을 경우 : /WEB-INF/views/success.jsp
	// ResponseBody 쓸 경우 : 문자열 success를 클라이언트에게 반환
	@PostMapping("checkMember.do")
	@ResponseBody // HTTP body에 return값을 응답으로 보냄
	public String checkMember(String id, String email) {

		int resultId = memberService.checkId(id);
		int resultEmail = memberService.checkEmail(email);

//		log.info("resultId : " + resultId);
//		log.info("resultEmail : " + resultEmail);

		if (resultId > 0 && resultEmail > 0) {
			return "failed";
		} else if (resultId > 0 && resultEmail <= 0) {
			log.info("아이디 중복");
			return "idFailed";
		} else if (resultId <= 0 && resultEmail > 0) {
			log.info("이메일 중복");
			return "emailFailed";
		} else {
			return "success";
		}
	}

	// http://localhost/member/signupPage.do
	// 회원가입 페이지 이동
	@GetMapping("/signupPage.do")
	public String getSingUpPage() {
		return "/signup/signup";
	}

	// 회원 가입
	@PostMapping("/signup.do")
	public String signupMember(MemberDTO memberDto, HttpSession session, RedirectAttributes rttr) throws Exception {

		// 유효성 검사
		String password = memberDto.getMemberPassword();
		String passwordChk = memberDto.getMemberPasswordChk();
		String id = memberDto.getMemberId();

		String passwordRegex = "^(?=.*[a-zA-Z])(?=.*[@$!%*?&\\#])[A-Za-z\\d@$!%*?&\\#]{8,20}$";
		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
		String idRegex = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{8,16}$";

		// 이메일 저장
		String emailPrefix = memberDto.getEmailPrefix();
		String emailSuffix = memberDto.getEmailSuffix();
		memberDto.setMemberEmail(emailPrefix + "@" + emailSuffix);
		String email = memberDto.getMemberEmail();

		// id, email 중복 확인 메서드 ( ajax로 요청하던 메서드에 email을 담고 결과값 반환 받음 , String chkMember
		// = failed , emailFailed, idFailed, success 중 하나 반환 받음)
		String chkMember = checkMember(id, email);
//		log.info("chkMember : " + chkMember);
//		
//		log.info("member :" + memberDto);
//		log.info(chkMember.equals("success"));
//		
//		log.info("password : " + password);
//		log.info("chkMember : " + chkMember);
//		log.info("email : " + email);
//		log.info("id : " + id);

		if (password.matches(passwordRegex) && password.equals(passwordChk) && chkMember.equals("success")
				&& email.matches(emailRegex) && id.matches(idRegex)) {

			log.info("중복 확인 및 유효성 검사 완료");

			// 패스워드 암호화
			String bcryPassword = bcryptPasswordEncoder.encode(memberDto.getMemberPassword());
			memberDto.setMemberPassword(bcryPassword);

			// redirect되는 곳으로 데이터 전송
//			rttr.addFlashAttribute("msg", "가입이 완료되었습니다");
			rttr.addAttribute("memberEmail", memberDto.getMemberEmail());
			rttr.addAttribute("memberId", memberDto.getMemberId());
			
			// 회원 가입 ( insert 서비스 실행(sendMail 서비스impl에 회원가입 dao 추가), 이메일 발송)
//			int result = memberService.signupMember(memberDto);
			memberService.sendMail(memberDto);
			return "redirect:/member/registerAuth";

//			if(result > 0) { //데이터가 전송 가능하면
//				log.info("가입 완료");
//				return "signin/signin";
//			} else {
//				log.info("데이터 전송 실패");
//				return "shop/index"; // 임시로 메인 페이지 이동 ( error )
//			}
		} else {
			log.info("회원가입 유효성 검사 불일치");
			return "shop/index"; // 임시로 메인 페이지 이동 ( error )
		}
	}

	// 이메일 인증
	@GetMapping("/emailConfirm")
	public String emailConfirm(String memberEmail, String key, Model model) throws Exception {
		
		memberService.memberAuth(memberEmail, key);
		model.addAttribute("memberEmail", memberEmail);
		model.addAttribute("authKey", key);

		return "/member/emailConfirm";
	}

	// 이메일 인증 요청 페이지
	@GetMapping("/registerAuth")
	public String loginView(HttpServletRequest request, Model model, @RequestParam("memberEmail") String memberEmail,
			@RequestParam("memberId") String memberId) throws Exception {

		model.addAttribute("memberEmail", memberEmail);
		model.addAttribute("memberId", memberId);

		return "/member/registerAuth";
	}

	// 네이버 로그인
	@RequestMapping(value = "/naverLogin", method = RequestMethod.GET)
	public String naverLogin(@RequestParam(value = "code", required = false) String code, HttpSession session,
			Model model) throws Throwable {

		String access_Token = memberService.getAccessTokenNaver(code);
		// 위의 access_Token 받는 걸 확인한 후에 밑에 진행
		log.info("access_Token" + access_Token);

		HashMap<String, Object> userInfo = memberService.getUserInfoNaver(access_Token);
		log.info("###id#### : " + userInfo.get("id"));
		log.info("###pw#### : " + userInfo.get("pw"));
		log.info("###email#### : " + userInfo.get("email"));
		log.info("###name#### : " + userInfo.get("name"));
		log.info("###phoneNum#### : " + userInfo.get("phoneNum"));
		log.info("###gender#### : " + userInfo.get("gender"));
		log.info("###birthday#### : " + userInfo.get("birthday"));
		String id = (String) userInfo.get("id");
		String email = (String) userInfo.get("email");

//			int checkId = memberService.checkId(id);
		int checkEmail = memberService.checkEmail(email);

		// 중복 되는 이메일로 가입된 계정이 있을 경우, 해당 이메일 계정으로 접속함
		if (checkEmail > 0) {
			MemberDTO m = new MemberDTO();
			m.setMemberEmail(email);
			MemberDTO loginUser = memberService.snsLoginMember(m);
			log.info(loginUser.toString());
			if (!Objects.isNull(loginUser)) {
				log.info("데이터(네이버 계정) 있음");
				session.setAttribute("memberIdx", loginUser.getMemberIdx());
//				String sessionMemberIdx = String.valueOf(loginUser.getMemberIdx());
//				session.setAttribute("sessionMemberIdx", sessionMemberIdx);
				session.setAttribute("memberName", loginUser.getMemberName());
				session.setAttribute("memberId", loginUser.getMemberId());
				return "redirect:/product/index";
			} else {
//					model.addAttribute("msg", "아이디 비밀번호를 확인해 주세요!");
//					model.addAttribute("status", "error");
				return "/signin/signin";
			}
		} else {
			log.info("네이버 계정 가입");
			MemberDTO md = new MemberDTO();
			md.setMemberId(id);
			md.setMemberPassword((String) userInfo.get("pw"));
			md.setMemberEmail((String) userInfo.get("email"));
			md.setMemberName((String) userInfo.get("name"));
			md.setMemberGender((String) userInfo.get("gender"));
			md.setMemberphoneNum((String) userInfo.get("phoneNum"));
			md.setMemberBirthday((String) userInfo.get("birthday"));

			int result = memberService.snsSingup(md);
			if (result > 0) {
				MemberDTO m = new MemberDTO();
				m.setMemberId(id);
				MemberDTO loginUser = memberService.loginMember(m);
				log.info(loginUser.toString());
				if (!Objects.isNull(loginUser)) {
					session.setAttribute("memberIdx", loginUser.getMemberIdx());
//					String sessionMemberIdx = String.valueOf(loginUser.getMemberIdx());
//					session.setAttribute("sessionMemberIdx", sessionMemberIdx);
					session.setAttribute("memberName", loginUser.getMemberName());
					session.setAttribute("memberId", loginUser.getMemberId());
					return "redirect:/product/index";
				} else {
//						model.addAttribute("msg", "아이디 비밀번호를 확인해 주세요!");
//						model.addAttribute("status", "error");
					return "/signin/signin";
				}
			}
			return "/signin/signin";
		}
	}

	// 카카오 로그인
	@RequestMapping(value = "/kakaoLogin", method = RequestMethod.GET)
	public String kakaoLogin(@RequestParam(value = "code", required = false) String code, HttpSession session,
			Model model) throws Throwable {

		String access_Token = memberService.getAccessTokenKakao(code);
		// 위의 access_Token 받는 걸 확인한 후에 밑에 진행

		HashMap<String, Object> userInfo = memberService.getUserInfoKakao(access_Token);
		log.info("###id#### : " + userInfo.get("id"));
		log.info("###pw#### : " + userInfo.get("pw"));
		log.info("###email#### : " + userInfo.get("email"));
//		log.info("###name#### : " + userInfo.get("name"));
//		log.info("###phoneNum#### : " + userInfo.get("phoneNum"));
//		log.info("###gender#### : " + userInfo.get("gender"));
//		log.info("###birthday#### : " + userInfo.get("birthday"));
		String id = (String) userInfo.get("id");
		String email = (String) userInfo.get("email");

//		int checkId = memberService.checkId(id);
		int checkEmail = memberService.checkEmail(email);

		// 중복 되는 이메일로 가입된 계정이 있을 경우, 해당 이메일 계정으로 접속함
		if (checkEmail > 0) {
			MemberDTO m = new MemberDTO();
			m.setMemberEmail(email);
			MemberDTO loginUser = memberService.snsLoginMember(m);
			log.info(loginUser.toString());
			if (!Objects.isNull(loginUser)) {
				log.info("데이터(카카오 계정) 있음");
				session.setAttribute("memberIdx", loginUser.getMemberIdx());
//				String sessionMemberIdx = String.valueOf(loginUser.getMemberIdx());
//				session.setAttribute("sessionMemberIdx", sessionMemberIdx);
				session.setAttribute("memberId", loginUser.getMemberId());
				log.info("memberId : " + loginUser.getMemberId());
				return "redirect:/product/index";
			} else {
//				model.addAttribute("msg", "아이디 비밀번호를 확인해 주세요!");
//				model.addAttribute("status", "error");
				return "/signin/signin";
			}
		} else {
			log.info("카카오 계정 가입");
			MemberDTO md = new MemberDTO();
			md.setMemberId(id);
			md.setMemberPassword((String) userInfo.get("pw"));
			md.setMemberEmail((String) userInfo.get("email"));
//			md.setMemberName((String) userInfo.get("name"));
//			md.setMemberGender((String) userInfo.get("gender"));
//			md.setMemberphoneNum((String) userInfo.get("phoneNum"));
//			md.setMemberBirthday((String) userInfo.get("birthday"));

			int result = memberService.snsSingup(md);
			if (result > 0) {
				MemberDTO m = new MemberDTO();
				m.setMemberId(id);
				MemberDTO loginUser = memberService.loginMember(m);
				log.info(loginUser.toString());
				if (!Objects.isNull(loginUser)) {
					session.setAttribute("memberIdx", loginUser.getMemberIdx());
//					String sessionMemberIdx = String.valueOf(loginUser.getMemberIdx());
//					session.setAttribute("sessionMemberIdx", sessionMemberIdx);
					session.setAttribute("memberId", loginUser.getMemberId());
					log.info("memberId : " + loginUser.getMemberId());
					return "redirect:/product/index";
				} else {
//					model.addAttribute("msg", "아이디 비밀번호를 확인해 주세요!");
//					model.addAttribute("status", "error");
					return "/signin/signin";
				}
			}
			return "/signin/signin";
		}

	}
	
	//회원 탈퇴 페이지
	@GetMapping("/memberDeletePage")
	public String memberDeletePage(HttpSession session, Model model) {
		
		model.addAttribute("memberId", session.getAttribute("memberId"));
		model.addAttribute("memberEmail", session.getAttribute("memberEmail"));
		
		return "/member/member_delete";
	}

	
	//회원 탈퇴
	@PostMapping("/memberDelete.do")
	@ResponseBody // dto 사용 하고 ajax에서 받아올때 dto 필드명 같아야함
	public String memberDelete(MemberDTO memberdto, HttpSession session, Model model) {		
		
		MemberDTO loginUser = memberService.loginMember(memberdto);
		log.info("loginUser : " + loginUser);
		
		log.info("memberDto : " + memberdto.getMemberPassword());
		
		//bcryptPasswordEncoder 유저가 입력한 비밀번호 , db에서 불러올 암호화된 비밀번호 순서 지켜야함
		if(bcryptPasswordEncoder.matches(memberdto.getMemberPassword(), loginUser.getMemberPassword())) {
			
			memberService.memberDelete(loginUser);
			memberService.memberAuthDelete(loginUser);
			
			session.invalidate();
			
			log.info("탈퇴 성공");
			
			return "success";
//			return "/product/index";
		} else {
			log.info("탈퇴 실패");
			
			return "failled";
//			return "redirect:/member/editMyPage" ;
		}
	}

}