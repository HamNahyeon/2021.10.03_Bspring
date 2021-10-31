package edu.kh.fin.board.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.fin.board.model.service.BoardService;
import edu.kh.fin.board.model.service.ReplyService;
import edu.kh.fin.board.model.vo.Board;
import edu.kh.fin.board.model.vo.Category;
import edu.kh.fin.board.model.vo.Pagination;
import edu.kh.fin.board.model.vo.Reply;
import edu.kh.fin.board.model.vo.Search;
import edu.kh.fin.member.controller.MemberController;
import edu.kh.fin.member.model.vo.Member;

@Controller
@RequestMapping("/board/*")
@SessionAttributes({"loginMember"})
public class BoardController {

	// @SessionAttributes
	// Session 올리기 : Model.addAttribute("key", value)에서의 "key" 값을
	//						  @SessionAttributes에 작성
	// Session에서 얻어오기 : @ModelAttribute("세션에 올라간 key")
	
	@Autowired
	private BoardService service;
	
	@Autowired
	private ReplyService replyservice;
	
	// 게시글 목록 조회
	// 목록조회는 GET POST 지정 하지 않아도 되기 때문에 value, method 없이 그냥 value만 작성
	@RequestMapping("{boardType}/list")
	public String boardList(@PathVariable("boardType")int boardType,
									  @RequestParam(value="cp", required=false, defaultValue="1")int cp,
									  Model model, Pagination pg/*페이징 처리에 사용할 비어있는 객체*/,
									  Search search /*검색용 커맨드 객체*/) {
		// @PathVariable 언제사용? -> 특정 자원(게시판, 상세조회) 구분할 때 사용
		// PathVariable 에 따라 조회되는 내용이 다를 때
		
		// 쿼리스트링은 언제사용? -> 정렬, 필터링(검색) 할 때 사용
		
		
		// 1) pg에 boardType, cp를 세팅
		pg.setBoardType(boardType);
		pg.setCurrentPage(cp);
		
		System.out.println(search);
		// 게시글 목록조회 : Search [sk=null, sv=null, boardType=1, ct=[1, 3]]
		// 카테고리X 검색O : Search [sk=titcont, sv=옴뇸뇸, boardType=1, ct=null]
		// 카테고리 + 타입 + 검색어 Search [sk=titcont, sv=옴뇸뇸, boardType=1, ct=[1, 3]]
		// 검색 X -> sk == null
		// 검색 O -> sk != null
		
		Pagination pagination = null;
		List<Board> boardList = null;
		
		if(search.getSk() == null) { // 검색 X -> 전체 목록 조회 
			// 2) 전체 게시글 수를 조회하여 Pagination 관련 내용을 계산하고 값을 저장한 객체 반환받기
			pagination = service.getPagination(pg);
			
			// 3) 생성된 pagination을 이용하여 현재 목록 페이지에 보여질 게시글 목록 조회
			boardList = service.selectBoardList(pagination);
		}else { // 검색 -> 검색 목록조회
			// 검색이 적용됨 pagination 객체 생성 
			pagination = service.getPagination(search, pg); // 오버로딩
			
			// 검색이 적용된 pagination을 이용하여 게시글 목록조회
			boardList = service.selectBoardList(search, pagination);
			
		}
		

		
		// 조회결과 임시확인
		/*
		 * for(Board b : boardList) { System.out.println(b); }
		 */
		
		// model : 데이터 전달용 객체
		model.addAttribute("boardList", boardList);
		model.addAttribute("pagination", pagination);
		
		return "board/boardList";
	}
	
	// 게시글 상세조회 
	@RequestMapping("{boardType}/{boardNo}")
	public String boardView(@PathVariable("boardType")int boardType,
										@PathVariable("boardNo")int boardNo,
										@RequestParam(value="cp", required=false, defaultValue="1")int cp,
										Model model, RedirectAttributes ra) {
		
		// 게시글 상세조회 Service 호출
		Board board = service.selectBoardList(boardNo);
		if(board != null) { // 상세 조회 성공 시
			
			// +++++++++++++++++++++댓글 조회 추가
			List<Reply> rList = replyservice.selectList(boardNo);
			model.addAttribute("rList", rList);
			// +++++++++++++++++++++++++++++
			
			model.addAttribute("board", board);
			return "board/boardView";
		}else { // 상세 조회 실패 시 (해당 게시글 번호의 글이 없는 경우)
			
			MemberController.swalSetMessage(ra, "error", "게시글 상세 조회 실패", "삭제된 게시글 입니다.");
			return "redirect:list"; // 게시글 목록 조회로 리다이렉트
		}

	}
	
	// 게시글 삽입 화면 전환
	@RequestMapping(value="{boardType}/insert", method=RequestMethod.GET)
	public String insertForm(Model model) {
		
		// DB에서 CATEGORY테이블 내용을 모두 조회 해오기
		List<Category> category = service.selectCategory();
		
		model.addAttribute("category", category); 
		// 요청 위임 페이지에서 사용할 수 있도록 데이터 전달
		
		return "board/boardInsert";
	}
	
	// 게시글 삽입
	@RequestMapping(value="{boardType}/insert", method=RequestMethod.POST)
	public String insertBoard(@PathVariable("boardType")int boardType,
										 @ModelAttribute Board board /* 커맨드객체 : 보드객체 필드값이랑 같으면 자동으로 대입 */,
										 @RequestParam(value="cp", required=false, defaultValue="1")int cp,
//										 @ModelAttribute("loginMember")Member loginMember /* 세션 로그인 정보 */,
										 @RequestParam("images") List<MultipartFile> images /* 업로드된 이미지 파일 */,
										 HttpServletRequest request, RedirectAttributes ra
										 ) {
		/* 스프링에는 업로드된 파일을 위한 MultipartResolver라는 인터페이스가 존재한다.
		 * 
		 * 이 인터페이스를 구현하는 방법은 2가지가 있음
		 * 1. Servlet 3.0 이상 버전에서 제공하는 StandardServletMultipartResolver
		 * 2. Apache Commons FileUpload에서 제공하는 CommonsMultipartResolver
		 * -> 2를 사용 할 건데 bean으로 등록해서 Spring에서 알아서 처리해주는 게 많기 때문
		 * -> 준비 1) commons-fileupload 라이브러리 추가
		 * -> 준비 2) root-context.xml 파일에 bean등록
		 * 
		 * input type="file" 형태의 파라미터를 MultipartFile 객체로 반환해줌!
		 * 
		 * MultipartFile에서 제공하는 메서드
		 * - getSize() : 업로드된 파일의 크기(용량)반환
		 * - getOriginalFileName() : 업로드된 파일 원본 명
		 * - transferTo() : 메모리 -> 파일로 변환되어 저장
		 * 
		 * */
		
		// System.out.println("디버그모드 테스트");

		// 1) 로그인 된 회원정보에서 회원 번호를 얻어와 board 커맨드 객체에 세팅
//		board.setMemberNo( loginMember.getMemberNo() );
		
		// 2) @PathVariabel boardType을 board 커맨드 객체에 세팅
		board.setBoardType(boardType);
		
		// 3) 웹상 접근 경로, 실제 파일 저장 경로 지정
		String webPath = "resources/images/";
		
		// 게시판 타입에 따라 업로드되는 파일의 경로를 지정
		switch(boardType) {
			case 1 : webPath += "freeboard/"; break;
			case 2 : webPath += "informationboard/"; break;
			case 3 : webPath += "qnaboard/"; break;
		}
		
		// 실제로 파일이 저장되는 경로 얻어오기
		String savePath = request.getSession().getServletContext().getRealPath(webPath);
		
		// 4) 게시글 삽입 Service호출
		int boardNo = service.insertBoard(board, images, webPath, savePath);
		// 변수명이 boardNo인 이유?
		// -> 삽입 성공 시 해당 번호 글로 상세조회 하기 위해
		
		String path = null;
		if(boardNo > 0) { // 삽입 성공
			// 상세 조회 페이지로 리다이렉트 -> /fin/board/1/600
			// 현재 페이지						   -> /fin/board/1/insert
			path ="redirect:/board/" + boardType + "/list?type=cp=" + cp;
			// path ="redirect:";
			MemberController.swalSetMessage(ra, "success", "게시글 삽입 성공", null);
			
		}else { // 삽입 실패
			// 이전 게시글 작성화면으로 리다이렉트
			path= "redirect:" + request.getHeader("referer"); // 요청 이전 주소
			MemberController.swalSetMessage(ra, "error", "게시글 삽입 실패", null);
		}
		
		return path;
	}
	
	
	
	// 답글 삽입 화면 전환
	@RequestMapping(value="{boardType}/insert/{boardGroup}/reply", method=RequestMethod.GET)
	public String insertReply(Model model) {
		
		// DB에서 CATEGORY테이블 내용을 모두 조회 해오기
		List<Category> category = service.selectCategory();
		
		model.addAttribute("category", category); 
		// 요청 위임 페이지에서 사용할 수 있도록 데이터 전달
		
		return "board/boardReplyInsert";
	}
	
	// 답글 삽입
	@RequestMapping(value="{boardType}/insert/{boardNo}/reply", method=RequestMethod.POST)
	public String insertReply(@PathVariable("boardType")int boardType,
							  @PathVariable("boardNo")int boardNo,
							  @RequestParam(value="cp", required=false, defaultValue="1")int cp,
							  @ModelAttribute Board board /* 커맨드객체 : 보드객체 필드값이랑 같으면 자동으로 대입 */,
							  @ModelAttribute Board boardGroup /* 커맨드객체 : 보드객체 필드값이랑 같으면 자동으로 대입 */,
							  @RequestParam("images") List<MultipartFile> images /* 업로드된 이미지 파일 */,
							  HttpServletRequest request, RedirectAttributes ra
							  ) {
		
		// 2) @PathVariabel boardType을 board 커맨드 객체에 세팅
		board.setBoardType(boardType);
		board.setBoardNo(boardNo);
		
		// 3) 웹상 접근 경로, 실제 파일 저장 경로 지정
		String webPath = "resources/images/";
		
		// 게시판 타입에 따라 업로드되는 파일의 경로를 지정
		switch(boardType) {
			case 1 : webPath += "freeboard/"; break;
			case 2 : webPath += "informationboard/"; break;
			case 3 : webPath += "qnaboard/"; break;
		}
		
		// 실제로 파일이 저장되는 경로 얻어오기
		String savePath = request.getSession().getServletContext().getRealPath(webPath);
		
		// 4) 게시글 삽입 Service호출
		int boardNo2 = service.insertReply(board, images, webPath, savePath);
		// 변수명이 boardNo인 이유?
		// -> 삽입 성공 시 해당 번호 글로 상세조회 하기 위해
		
		String path = null;
		if(boardNo2 > 0) { // 삽입 성공
			// 상세 조회 페이지로 리다이렉트 -> /fin/board/1/600
			// 현재 페이지						   -> /fin/board/1/insert
			// http://localhost:8081/fin/board/1/303?cp=1
			// http://localhost:8081/fin/board/1/305
			// http://localhost:8081/fin/board/1/insert/305/reply
			// http://localhost:8081/fin/board/1/insert
			// path = "redirect:/board/" + boardType + "/" + boardNo2;
			path ="redirect:/board/" + boardType + "/list?type=cp=" + cp;
			MemberController.swalSetMessage(ra, "success", "게시글 삽입 성공", null);
		}else { // 삽입 실패
			// 이전 게시글 작성화면으로 리다이렉트
			path= "redirect:" + request.getHeader("referer"); // 요청 이전 주소
			MemberController.swalSetMessage(ra, "error", "게시글 삽입 실패", null);
		}
		
		return path;
	}
	
	
	
	
	// 게시글 수정화면전환
	@RequestMapping(value="{boardType}/updateForm", method=RequestMethod.POST)
	public String updateForm(int boardNo, Model model) {
		// DB에서 조회해야만 하는 것들 : 카테고리 목록, 해당 번호 게시글(개행문자 처리)
		
		// 1) 카테고리 목록 조회
		List<Category> category = service.selectCategory();
		
		// 2) 게시글 상세조회(개행문자 <br> -> \r\n으로 변경한 것
		// textarea에서는 <br>을 쓰면 그대로 <br>로 표기 때문에 필요 
		Board board = service.selectUpdateBoard(boardNo);
		
		// 3) 조회된 데이터를 전달하기 위해 model에 추가
		model.addAttribute("category", category);
		model.addAttribute("board", board);
		
		return "board/boardUpdate";
	}
	
	// 게시글 수정
	@RequestMapping(value="{boardType}/update", method=RequestMethod.POST)
	public String updateBoard(@PathVariable("boardType") int boardType,
//							  @RequestParam("currentPass") String currentPass,
											Board board, /* 커맨드객체 */
											@RequestParam("images") List<MultipartFile> images, /* 수정 또는 추가된 파일 */
											@RequestParam("deleteImages") String deleteImages, /* 삭제파일레벨 */
											HttpServletRequest request, Model model, RedirectAttributes ra) {
		
		// 1) 파일이 저장될 실제 서버 경로, DB에 저장될 웹 접근 경로 구하기
		String webPath = "resources/images/";
		switch(boardType) { // 게시판 타입이 여러 개 있을 경우
		case 1 : webPath += "freeboard/"; break;
		case 2 : webPath += "informationBoard/"; break;
		case 3 : webPath += "QnABoard/"; break;
		}
		
		// 실제 저장용 서버 경로
		String savePath = request.getSession().getServletContext().getRealPath(webPath);

		// 2) 게시글 수정 Service 호출
		int result = service.updateBoard(
//				currentPass, 
				board, images, webPath, savePath, deleteImages);
		
//		System.out.println("currentPass : " + currentPass);
		
		String path = null;
		if(result > 0) { // 수정 성공
			path ="redirect:" + board.getBoardNo();
			MemberController.swalSetMessage(ra, "success", "게시글 수정 성공", null);
		}else { // 수정 실패
			path= "redirect:" + request.getHeader("referer"); // 요청 이전 주소
			MemberController.swalSetMessage(ra, "error", "게시글 수정 실패", null);
		}
		
		return path;
	}
	
	
//	@RequestMapping(value="{boardType}/delete/{boardNo}", method=RequestMethod.GET)
//	public String deleteBoard(@PathVariable("boardNo") int boardNo,
//							  @PathVariable("boardType") int boardType,
//							  @RequestParam(value="currentPass") String currentPass,
//						   	  Board board) {
//		
//		return "board/boardView";
////		return "board/boardList";
//	}
	
	// 게시글 삭제
	@RequestMapping(value="{boardType}/delete/{boardNo}", method=RequestMethod.GET)
	public String deleteBoard(@PathVariable("boardNo") int boardNo,
							  @PathVariable("boardType") int boardType,
//							  @RequestParam(value="cp", required=false, defaultValue="cp")int cp,
//							  @RequestParam(value="currentPass") String currentPass,
						   	  Board board, 
//						   	  Pagination pg,
						   	  HttpServletRequest request, Model model, RedirectAttributes ra) {
		
		System.out.println(board);
		model.addAttribute("board", board);
		//pg.setCurrentPage(cp);
//		String savePwd = service.selectPassword( board.getBoardNo() );
		
		int result = 0;
		String path = null;
		
//		if(savePwd.equals(currentPass)) {
			result = service.deleteBoard( board);
			
			//System.out.println("currentPass : " + currentPass);
			System.out.println("BOARD : " + board);
			System.out.println("BOARDTYPE : " + boardType);
			System.out.println("게시글 삭제 결과 cont : " + result);
			
			if(result > 0) { 
				//http://localhost:8081/fin/board/1/8?cp=1
//				path ="board/boardList";
				path ="redirect:/board/" + boardType + "/list";
//				path ="redirect:/";
				MemberController.swalSetMessage(ra, "success", "게시글 삭제 완료", null);
				System.out.println(board);
				System.out.println(boardType);
			}else { 
				path= "redirect:" + request.getHeader("referer");
				MemberController.swalSetMessage(ra, "error", "게시글 삭제 실패", null);
			}
//		}
		
		return path;
	}
	
}


