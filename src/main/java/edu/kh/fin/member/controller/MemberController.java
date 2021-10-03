package edu.kh.fin.member.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.fin.member.model.service.MemberService;
import edu.kh.fin.member.model.service.MemberServiceImpl;
import edu.kh.fin.member.model.vo.Member;

/* 해야할 일
 * 1. @Controller 컨트롤러다 라고 알려 주는 것
 * 요청을 전달받아 알맞은 서비스를 연결 해주는 역할
 * */
@Controller // 프레젠 테이션 레이어로 
				// 웹 애플리케이션에서 전달된 요청을 받고 
				// 응답화면을 제어하는 클래스 라는 것을 명시
@RequestMapping("/member/*") // 요청 주소앞부분이 member로 되어있는 요청을 모두 받아들임
@SessionAttributes({"loginMember"})
// @SessionAttributes란?
// -> Model 객체에 추가된 속성 중 Key값이 해당 어노테이션에 작성된 값과 같다면 session scope로 이동
public class MemberController {
	
	// @Autowired : component-scan을 통해 bean으로 등록된 클래스 중
	// 타입이 같거나 상속관계에 있는 객체를 자동으로 DI(의존성 주입) 해줌
	@Autowired
	private MemberService service;
	
	// ----------------------------------------------------------------------------
	
	/* @RequesetMapping 작성방법
	 * 
	 * 1. 별도의 추가 속성이 없고 전달 방식이 get/post 둘 중 아무거나 상관 없을 때
	 * 	  @RequestMapping("매핑URL")
	 * 
	 * 2. 별도 추가 속성이 있거나, 전달 방식의 구분이 필요할 때 
	 * 	  @RequestMapping(value="매핑URL", method=RequesetMethod.POST)
	 * 
	 * */
	
	// 로그인 Controller
	
	// 1) HttpServletRequest를 이용한 파라미터 전달 받기
	// 단 점 파라미터를 string으로만 꺼내올 수 있음
	
	
	// @RequestMapping("login")
	/*
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String login(HttpServletRequest request) {
		
		String memberId = request.getParameter("memberId");
		String memberPw = request.getParameter("memberPw");
		
		System.out.println("memberId : " + memberId);
		System.out.println("memberPw : " + memberPw);
		
		return null;
	}
	*/
	
	// 2) @RequestParam 어노테이션을 이용한 파라미터 전달 받기
	// - request 객체를 이용한 파라미터 전달 어노테이션
	// - 메서드 매개변수 앞에 작성하면 알맞은 파라미터가 자동으로 매개변수에 주입됨
	
	// [속성]
	// value : 전달 받은 input태그의 name속성 값 
	// required : 전달 받는 파라미터의 필수 여부(기본값 true)
	// -> required = true 인 상태에서 파라미터가 존재하지 않는다면 400Bad Request가 발생함
	// defaultValue : 전달 받은 파라미터 중 일치하는게 없을 경우에 대신 대입될 값
	
	
	// @RequestParam("memberPw") String memberPw, 값이 없으면 에러를 발생 시킴
	// defaultValue="1" String이고 자동으로 int형으로 파싱해줌
	
	/*
	@RequestMapping(value="login", method=RequestMethod.POST)
	public String login( @RequestParam("memberId") String memberId,
						 @RequestParam("memberPw") String memberPw,
						 @RequestParam(value="cp", required=false, defaultValue="1") int cp ) {
		// @RequestParam("cp") int cp ) { 
		
		// @RequestParam("memberId") String memberId
		// -> 요청에 담긴 파라미터 중 name 속성 값이 "memberId"인 파라미터를
		//	  매개변수 String memberId에 주입
		System.out.println("memberId : " + memberId);
		System.out.println("memberId : " + memberPw);
		System.out.println("cp : " + cp);
		// Resolved [org.springframework.web.bind.MissingServletRequestParameterException: 
		// Required String parameter 'cp' is not present]
		// Required : 필수 
		
		return null;
	}
	*/
	
	// 3) @RequestParam 어노테이션 생략
	// -> 생략가능 조건 : input태그의  name속성 값과 매개변수의 변수명이 일치하면 생략 가능
	// 어노테이션 코드를 생략하는 경우 의미파악, 가독성이 낮아지게 되므로 
	// 업무 환경에 따라서 생략을 못하게 규칙으로 정해진 곳도 있다.
	/*
	@RequestMapping(value="login", method=RequestMethod.POST)
	public String login( String memberId, String memberPw ) {
		System.out.println("memberId : " + memberId);
		System.out.println("memberId : " + memberPw);
		
		// 요청위임(forward) : 요청을 그대로 전달하여 대신 응답 화면을 만드는 것
		//					   -> 요청주소가 그대로 유지됨
		// DispatcherServlet -> Controller -> ViewResolver
		
		// 재요청(redirect) : 기존 요청을 폐기하고 새롭게 요청을 보내는 것
		//					   -> 새 요청이므로 요청 주소가 바뀌게 됨
		// DispatcherServlet -> Controller -> (다시요청) -> DispatcherServlet -> Controller
		
		return "redirect:/"; // 메인페이지로 리다이렉트(재요청)
	}
	*/
	
	// 4) @ModelAttribute 어노테이션을 이용한 파라미터 전달 받기
	// 요청 페이지에서 여러 파라미터가 전달될 때
	// 해당 파라미터의 key값(input태그의 name속성 값)이 
	// 특정 객체의 필드명과 같다면
	// 일치하는 객체를 자동생성하여 필드에 값을 세팅 후 반환
	
	// [사용조건]
	// 1 - VO 내부에 반드시 기본생성자가 작성되어있어야 할 것
	// 2 - setter가 반드시 있어야 함
	// 3 - 필드명과 input태그 name속성 값이 같아야 함
	
	// @ModelAttribute를 이용하여 파라미터가 자동추가된 객체를 
	// "커맨드 객체"라고 한다.
	/*
	@RequestMapping(value="login", method=RequestMethod.POST)
	public String login( @ModelAttribute Member inputMember ) {
		
		System.out.println("memberId : " + inputMember.getMemberId());
		System.out.println("memberId : " + inputMember.getMemberPw());
		
		return "redirect:/";
	}
	*/
	
	/* 아이디 비밀번호를 입력하고 제출을 했을 때 name 속성값이 전달 됨
	 * HttpServletRequest
	 * @RequestParam
	 * @ModalAttribute
	 * */
	
	// 5) @ModelAttribute 생략
	@RequestMapping(value="login", method=RequestMethod.POST)
	public String login(Member inputMember, Model model, 
								HttpServletRequest request, HttpServletResponse response,
								RedirectAttributes ra, 
								@RequestParam(value="save", required=false) String save) {
		// required=false 아이디 저장을 체크하지 않을 수 있기 때문에
		// 필 수 입력 상태로 두면 오류가 발생할 것을 예상하여 false로 둠 400에러
		// 혹시라도 null일경우를 대비해서(네모박스를 체크하지 않았을 때)
		
		System.out.println("memberId : " + inputMember.getMemberId());
		System.out.println("memberId : " + inputMember.getMemberPw());

		// 의존성 주입(DI)된 service 객체의 기능 중 
		// 로그인 서비스를 호출하여 회원 정보를 반환
		Member loginMember = service.login(inputMember);
		
		System.out.println("로그인 결과 : " + loginMember);
		System.out.println("아이디저장 체크 결과 : " + save);
		// save 체크 안했을 경우 on 체크했을 경우 null -> 조건을 on일경우가 아니라 null이 아닐경우로 함
		
		// 로그인 == 아이디, 비밀번호가 일치하는 회원 정보를 DB에서 조회해 Session에 올려두는 것
		
		// ** Model객체 (컨트롤러 메서드의 매개변수로 작성하면 Spring이 알아서 객체를 넣어줌)
		// - 데이터를 맵 형식 (K:V) 형태로 담아서 전달하는 용도의 객체
		// - Model객체의 scope는 기본적으로 requset이다.
		// (Servlet에서 requset.setAttribute("K", V)를 대신하는 객체
		
		if(loginMember != null) { // 로그인 성공 시
			
			model.addAttribute("loginMember", loginMember);
			// 1. model.addAttribute()만 작성했을 때 == request scope
			// 2. 로그인은 requset가 아닌 session에 있어야 되므로 옮겨줘야 함!!
			//    --> @SessionAttributes 어노테이션을 이용해야 한다.
			//			(Controller 선언부 위에 작성)
			
			// 쿠키생성
			Cookie cookie = new Cookie("saveId", loginMember.getMemberId());
			
			// ** 아이디 저장용 Cookie 생성하기
			if(save != null) { // 아이디 저장 체크박스가 체크 된 경우
				
				// 쿠키 유지 시간 설정
				cookie.setMaxAge(60 * 60 * 24 * 30); // 초단위
				
			}else { // 아이디 저장 체크박스가 체크되지 않은 경우
				
				// 쿠키 없애기 == 유지시간을 0초
				cookie.setMaxAge(0);
			}
			
			// 쿠키사용 유효 경로 설정
			cookie.setPath(request.getContextPath()); // 최상위 경로 (/fin) 아래 모든 경로 적용
			
			// 응답에 Cookie를 담아서 클라이언트에게 전달
			response.addCookie(cookie);
			
		}else { // 로그인 실패 시
			
			// RedirectAttributes 객체
			// - 리다이렉트 시 값을 전달하는 용도로 사용하는 객체
			// -> 기존 Session이용 방법의 단점을 보완
			// 		(Session 이용 시 지우기 전까지 유지되는 문제)
			
			// 동작 원리
			// 리다이렉트 전 : requset scope
			// 리다이렉트 중 : session scope(자동으로 이동함)
			// 리다이렉트 후 : request scope
			// addAttribute -> request scope만 쓸 때?
			// addFlashAttribute -> request scope에서 잠깐이라도 session scope로 이동할 때
			ra.addFlashAttribute("icon", "error");
			ra.addFlashAttribute("title", "로그인 실패");
			ra.addFlashAttribute("text", "아이디 또는 비밀번호가 일치하지 않습니다.");
			
		}
		return "redirect:/";
	}
	
	// /member/logout
	@RequestMapping(value="logout", method=RequestMethod.GET)
	public String logout(SessionStatus status,
								 @RequestHeader("referer") String referer) {
		
		// 로그아웃 : Session에 있는 로그인된 회원 정보를 없애는 것
		
		// @SessionAttributes를 통해 등록된 Session은
		// SessionStatus.setComplete()를 이용해야지만 없앨 수 있다.
		
		// SessionStatus 객체 : 세션의 상태를 관리할 수 있는 객체
		status.setComplete();
		
		// 로그아웃 후 돌아갈 페이지
		// 1) 메인 / 2) 로그아웃을 요청한 페이지
		// 로그아웃 후 로그아웃을 요청한 페이지로 리다이렉트
		// HttpServletRequest 객체에 담긴 header의 내용 중 "referer"을 얻어와야 됨.
		// referer
		return "redirect:" + referer;
	}
	
	// 스프링 예외처리 방법
	// 1) 메서드 별로 예외처리하는 방법 : try - catch, throws
	
	// 2) 컨트롤러 별로 예외처리하는 방법 : @ExceptionHandler
	// 		-> @Controller 또는 @RestController로 등록된 클래스 내부에서
	//			예외가 발생할 경우 처리하는 별도의 메서드
	// Controller안에서 예외가 발생하면 무조건 일로 옴 그래서 별도로 예외처리를 해줄 필요가 없음
   @ExceptionHandler(Exception.class)
   public String exceptionHendler(Exception e, Model model) {
      
      e.printStackTrace();
      model.addAttribute("errorMsg", "서비스 이용 중 문제가 발생했습니다.");
      return "common/error";
   }
	
	// 3) 전역(모든 클래스)에서 예외처리하는 방법 : @ControllerAdvice
   // 		-> 모든 예외를 모아서 처리하는 컨트롤러를 별도로 생성해서 운영
	
   
	// 회원 가입 화면 전환용 Controller
	@RequestMapping(value="signUp", method=RequestMethod.GET) // 클래스 위에 작성된 RequestMapping 값과 합쳐져 요청을 구분함
	public String signUp() { // "member/signUp"
		return "member/signUp"; // 요청 위임할 jsp의 이름(ViewResolver 부분을 제외한 나머지 경로)
		
		// /WEB-INF/views/member/signUp.jsp
	}
	
	// 오버로딩 : 하나의 매서드이름으로 다른 기능을 구현하는 것
	@RequestMapping(value="signUp", method=RequestMethod.POST)
	public String signUp( @ModelAttribute Member inputMember, RedirectAttributes ra ) {
		// RedirectAttributes ra를 매개변수로 추가해야 signUp용 응답객체가 만들어 진다.
		
		// System.out.println("signUp 메서드 실행");
		
		// inputMember에는 회원 가입 시 입력한 모든 값이 담겨져 있다.(== 커맨드 객체)
		
		// DB에 회원 정보를 삽입하는 Service호출
		int result = service.signUp(inputMember);
		
		System.out.println(inputMember);
		// 회원 가입 성공 또는 실패 경우에 따라 출력되는 메시지 제어(SweetAlert)
		if(result > 0) {
			swalSetMessage(ra, "success", "회원가입성공", inputMember.getMemberName() + "님 환영합니다.");
		}else {
			swalSetMessage(ra, "error", "회원가입실패", "고객센터로 문의해주세요.");
		}
		
		return "redirect:/"; // 메인페이지 재요청
	}
   
	// 아이디 중복검사 Controller(ajax)
	@RequestMapping(value="idDupCheck", method=RequestMethod.POST)
	@ResponseBody // 반환값(return)이 forward, redirect가 아닌 값 자체로 반환한다는 것을 명시하는 어노테이션
	public int idDupCheck(@RequestParam("id") String id) {

		// 아이디 중복검사 Service 호출
		int result = service.idDupCheck(id);
		
		// ajax : 자바스크립트를 이용한 비동기 통신
		// 비동기 통신 : 요청-응답 시 화면 전체 갱신이 아닌 일부만 갱신
		//					-> 화면이 일부만 변한다.
		
		// (문제점!!!)
		// Spring Controller에서 메서드 return은 forward, redirect만 가능하다.
		
		// 만약 return result; 할 경우
		// View Resolver로 이동하여 /WEB-INF/views/1.jsp로 요청 위임됨
		
		// 만약 return "redirect:" + result; 를 할 경우 
		// fin/member/1로 재요청을 진행함
		
		// (해결방법!)
		// 해당 메서드에 @ResponseBody 어노테이션을 작성하면 해결 가능!
		
		// @ResponseBody : 반환값(return)이 forward, redirect가 아닌 값 자체로 반환한다는 것을 명시하는 어노테이션
		
		return result;
	}
	
	// SweetAlert를 이용한 메시지 전달용 메서드
	public static void swalSetMessage(RedirectAttributes ra, String icon, String title, String text) {
		// RedirectAttibutes : 리다이렉트 시 값을 전달하는 용도의 객체 
		
		ra.addFlashAttribute("icon", icon);
		ra.addFlashAttribute("title", title);
		ra.addFlashAttribute("text", text);
	}
   
	// 정보수정
	@RequestMapping(value="myPage", method=RequestMethod.GET)
	public String mypage() {
			return "member/myPage";
	}
	
	@RequestMapping(value="update", method=RequestMethod.POST)
	public String updateMember(@ModelAttribute("loginMember") Member loginMember,
												String inputEmail, String inputPhone, String inputAddress,
												Member inputMember, // 비어있는 커맨드 객체 == new Member()
												RedirectAttributes ra) {
		// String inputEmail : @RequestParam 생략(name속성값 == 변수명)
		
		// 넘겨져올 파라미터 : inputEmail, 합쳐진 phone, 합쳐진 address
		// 추가로 필요한 값 : 회원번호(Session에 있는 loginMember에서 얻어옴)
		
		// (리마인드)로그인 -> loginMember Session에 올렸다. 어떻게?
		// -> Model에 추가하여 @SessionAttributes를 통해 올렸다.
		
		// @SessionAttributes로 Session에 올라간 내용은 
		// @ModelAttribute("key")로 받아올 수 있다.
		
		// 마이바티스에 사용할 객체는 한 개만 전달할 수 있다!!
		// -> 사용할 값이 여러 개면 하나의 객체에 담아서 전달!!
		// --> inputMember에 담아서 전달하자!!
		inputMember.setMemberNo(loginMember.getMemberNo());
		inputMember.setMemberEmail(inputEmail);
		inputMember.setMemberPhone(inputPhone);
		inputMember.setMemberAddress(inputAddress);
		
		// 회원정보수정 Service
		int result = service.updateMember(inputMember);
		
		if(result > 0) {
			swalSetMessage(ra, "success", "회원 정보 수정 성공", null);
			// null 이면 해당 부분에 대한 출력을 안함
			
			// DB와 정보 동기화
			loginMember.setMemberEmail(inputEmail);
			loginMember.setMemberPhone(inputPhone);
			loginMember.setMemberAddress(inputAddress);
		}else {
			swalSetMessage(ra, "error", "회원 정보 수정 실패", null);
		}
		return "redirect:myPage"; // 또는 redirect:/member/myPage
	}
	
	// 비밀번호변경 화면전환 컨트롤러
	// 주소매핑, 파라미터 전달, 세션에서 로그인 정보 얻어오기
	@RequestMapping(value="changePwd", method=RequestMethod.GET)
	 public String changePwd() {
		
		return "member/changePwd";
	 }
	
	// 비밀번호변경 컨트롤러
	@RequestMapping(value="changePwd", method=RequestMethod.POST)
	public String changePwd(@RequestParam("currentPwd")String currentPwd,
									     @RequestParam("newPwd")String newPwd,
									     @ModelAttribute("loginMember") Member loginMember,
									     RedirectAttributes ra) {
		int result = service.changePwd(currentPwd, newPwd, loginMember);
		
		String path = "redirect:";
		
		if(result > 0) { // 비밀번호 변경 성공
			swalSetMessage(ra, "success", "비밀번호 변경 성공", null);
			path +="myPage";
		}else { // 실패
			swalSetMessage(ra, "error", "비밀번호 변경 실패", null);
			path +="changePwd";
		}
		
		return path;
	}
	
	// 회원탈퇴 화면전환 컨트롤러
	@RequestMapping(value="secession", method=RequestMethod.GET)
	public String secession() {
		return "member/secession";
	}
	
	@RequestMapping(value="secession", method=RequestMethod.POST)
	public String secession(@RequestParam("currentPwd")String currentPwd, // 입력된 현재 비밀번호
									   @ModelAttribute("loginMember")Member loginMember, // 로그인된 현재 비밀번호
									   RedirectAttributes ra, // 메시지 전달용 객체
									   SessionStatus status) { // 세션 상태 관리 객체(로그아웃 = 세션만료 용)
		
		int result = service.secession(loginMember, currentPwd);
		
		String path = "redirect:";
		
		if(result > 0) { // 비밀번호 변경 성공
			swalSetMessage(ra, "success", "회원 탈퇴 성공", null);
			path += "/";
			status.setComplete();
		}else { // 실패
			swalSetMessage(ra, "error", "회원 탈퇴 실패", null);
			path +="secession";
		}

		return path;
	}
}


