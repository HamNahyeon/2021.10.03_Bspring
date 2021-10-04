package edu.kh.fin.board.model.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.fin.board.exception.InsertAttachmentException;
import edu.kh.fin.board.exception.SaveFileException;
import edu.kh.fin.board.model.dao.BoardDAO;
import edu.kh.fin.board.model.vo.Attachment;
import edu.kh.fin.board.model.vo.Board;
import edu.kh.fin.board.model.vo.Category;
import edu.kh.fin.board.model.vo.Pagination;
import edu.kh.fin.board.model.vo.Search;

@Service
public class BoardServiceImpl implements BoardService{

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private BoardDAO dao;

	
	
	// 전체 게시글 수  + 게시판 이름 조회 
	@Override
	public Pagination getPagination(Pagination pg) {

		// 1) 전체 게시글 수 조회
		Pagination selectPg =dao.getListCount(pg.getBoardType());
		
		System.out.println(selectPg);
		
		// 2) 계산이 완료된 Pagination 객체 생성 후 반환
		return new Pagination(pg.getCurrentPage(), selectPg.getListCount(), pg.getBoardType(), selectPg.getBoardName());
	}
	
	// 전체 게시글 수 + 게시판 이름 조회 (검색)
	@Override
	public Pagination getPagination(Search search, Pagination pg) {
		// 1) 검색이 적용된 게시글 수 조회
		Pagination selectPg = dao.getSearchListCount(search);
		
		System.out.println(selectPg);
		
		// 2) 계산이 완료된 Pagination 객체 생성 후 반환
		return new Pagination(pg.getCurrentPage(), selectPg.getListCount(), pg.getBoardType(), selectPg.getBoardName());
	}

	

	// 게시글 목록 조회
	// 서비스에서 할일이 없으니 바로 DAO로 보내버림
	@Override
	public List<Board> selectBoardList(Pagination pagination) {
		return dao.selectBoardList(pagination);
	}
	
	// 게시글 목록조회 (검색)
	@Override
	public List<Board> selectBoardList(Search search, Pagination pagination) {
		return dao.selectSearchList(search, pagination);
	}
	
	

	// 게시글 상세 조회
	@Transactional(rollbackFor=Exception.class) // 모든 예외 발생 시 롤백
	@Override
	public Board selectBoardList(int boardNo) {
		
		// 1) 게시글 상세조회
		Board board = dao.selectBoard(boardNo);
		
		// 2) 게시글 상세조회 성공 시 조회 수 1 증가
		// int result = dao.increaseReadCount(boardNo);
		if(board != null) {
			dao.increaseReadCount(boardNo);
			
			// 3) 조회된 board의 readCount와 DB의 READ_COUNT동기화
			board.setReadCount(board.getReadCount() + 1);
		}
		
		
		return board;
	}

	// 게시글 카테고리 목록조회
	@Override
	public List<Category> selectCategory() {
		return dao.selectCategory();
	}

	// 게시글 삽입
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int insertBoard(Board board, List<MultipartFile> images, String webPath, String savePath) {
		
//		암호화
//		System.out.println(inputMember); // 아이디, 비밀번호
//		System.out.println("변경 전 비밀번호 : " + inputMember.getMemberPw());
//		
//		// 입력받은 비밀번호를 암호화
//		String encPw = bCryptPasswordEncoder.encode(inputMember.getMemberPw());
//		System.out.println("변경 후 비밀번호 : " + encPw);
		
		
		// 1) 크로스사이트 스크립트 방지 처리 + 개행 문자 처리
		board.setBoardTitle(replaceParameter( board.getBoardTitle() ) );
		board.setBoardContent(replaceParameter( board.getBoardContent() ) );
		board.setBoardWriter(replaceParameter(board.getBoardWriter()));
		board.setBoardPass(replaceParameter(board.getBoardPass()));
		
		board.setBoardContent(  board.getBoardContent().replaceAll("(\r\n|\r|\n|\n\r)", "<br>")  );
		
		// 2) 글 부분 삽입
		// 기존) 다음 글 번호를 조회한 후 게시글을 삽입
		// 마이바티스 사용) insert 후 특정 컬럼 값을 반환하게 만드는 useGeneratedKeys, <selectKey> 사용
		
		int boardNo = dao.insertBoard(board);
		
		System.out.println("boardNo : " + boardNo);
		if(boardNo > 0) { // 게시글 삽입 성공
			
			// 3) 파일 정보 DB삽입
			
			// images에는 파일이 업로드가 됐든, 말든
			// input type="file" 태그가 모두 담겨있다.
			// -> 구별하는 방법 : 파일이 업로드되었을 경우 파일명이 존재한다.
			
			// DB에 저장할 파일만 추가할 List
			List<Attachment> atList = new ArrayList<Attachment>();
			
			for(int i = 0; i < images.size(); i++) {
				if( !images.get(i).getOriginalFilename().equals("") ) {
					// images의 i번째 요소의 파일명이 ""이 아닐경우
					// -> 업로드된 파일이 없을 경우 파일명이 ""(빈문자열)로 존재함
					
					// 파일명 변경 작업 수행
					String fileName = rename(images.get(i).getOriginalFilename());
					
					// Attachment 객체 생성
					Attachment at = new Attachment();
					at.setFileName(fileName); // 변경한 파일명
					at.setFilePath(webPath); // 웹 접근 경로
					at.setBoardNo(boardNo); // 게시글 삽입 결과(게시글 번호)
					at.setFileLevel(i); // for문 반복자 == 파일레벨
					
					// 만들어진 객체를 atList에 추가
					atList.add(at);
				}
			}
			// 업로드된 이미지가 있을 경우에만 DAO 호출
			if(!atList.isEmpty()) { // atList가 비어있지 않을 때 == 업로드된 이미지가 있을 때
				int result = dao.insertAttachmentList(atList);
				// result == 성공한 행의 개수
				
				// 삽입이 제대로 되었는지 확인 어떻게? 
				// atList의 크기 == result
				if(atList.size() == result) { // 모두 삽입 성공한 경우
					// 4) 파일을 서버에 저장(transfer())
					for(int i = 0; i < atList.size(); i++) {
						
						// try-catch 또는 throws가 강제되는 경우
						// == Checked Exception
						/*
						String str = null;
						str.length(); // unchecked Exception 예외가 발생할 수도 있다.
						
						mf.transferTo((Path)new File()); // Checked Exception << 반드시 처리해라고 경고 뜸
						*/
						try {
							images.get( atList.get(i).getFileLevel() )
							.transferTo( new File(savePath + "/" + atList.get(i).getFileName() ) );
							// images에서 업로드된 파일이 있는 요소를 얻어와 지정된 경로에 파일 저장
							
						} catch (Exception e) {
							e.printStackTrace();
							// transferTo() 메서드는 반드시 예외처리를 해야하는
							// IOException을 발생시킬 가능성이 있는 메서드다.
							// 그런데 예외가 발생하면 rollback을 수행하는 @Transactional이 작성되어있는데
							// catch로 예외를 처리하면 이를 인지 못하는 상황이 발생함
							// 그렇다고해서 catch문을 없애면 예외처리하라고 에러가 발생함
							
							// -> 예외는 발생시키지만 코드상에서 예외처리가 강제되지 않는
							//		UnChecked Exception 구문을 만들어서 예외를 발생시킴
							
							//throw new NullPointerException(); //-> 사용은 가능하지만 적절하지 않음
							// -> 사용자 정의 예외 클래스를 생성해서 사용
							
							throw new SaveFileException();
							// 예외가 던져짐 -> @Transactional이 반응함 -> 롤백이 수행됨.
						}
					}
					
				}else { // 하나라도 삽입 실패한 경우
					// 실패시 rollback
					throw new SaveFileException();
					// ex) 4개의 파일 정보를 DB에 삽입했으나 2개만 성공한 경우
				}
			}
		}
		
		return boardNo;
	}
	
	// 게시글 수정용 상세 조회 
	@Override
	public Board selectUpdateBoard(int boardNo) {
		Board board = dao.selectBoard(boardNo);
		
		// <br> -> \r\n으로 변경
		board.setBoardContent(board.getBoardContent().replaceAll("<br>", "\r\n"));
		
		return board;
	}
	
	// 게시글 수정
	@Transactional(rollbackFor=Exception.class)
	@Override
	public int updateBoard(Board board, String currentPwd, List<MultipartFile> images, 
						   String webPath, String savePath, String deleteImages) {

		// 1) 크로스 사이트 스크립트 방지 처리 + 개행문자처리(\r\n -> <br>)
		board.setBoardTitle(replaceParameter( board.getBoardTitle() ) );
		board.setBoardContent(replaceParameter( board.getBoardContent() ) );
		
		board.setBoardContent(  board.getBoardContent().replaceAll("(\r\n|\r|\n|\n\r)", "<br>")  );
		
//		암호가 일치할 때 수정
		// DB에 저장되어 있는 현재 회원의 비밀번호 조회 
		String savePwd = dao.selectPassword( board.getBoardNo() );
		System.out.println(savePwd);
		System.out.println(board.getBoardNo());
		int result = 0;
		// 조회한 비밀번호와 입력받은 현재 비밀번호가 일치하는지 확인
		if( currentPwd.equals(savePwd)) {
//			if( bCryptPasswordEncoder.matches(currentPwd, savePwd) ) {
			
			// 2) 비밀번호 변경
			// - 새 비밀번호를 암호화
			// encPwd : 암호화된 비밀번호
			//String encPwd = bCryptPasswordEncoder.encode(newPwd);
			
			// 마이바티스 메서드는 SQL 수행 시 사용할 파라미터를 하나만 추가할 수 있다.
			// -> loginMEmber에 담아서 전달
			//loginMember.setMemberPw(encPwd);
			
			//result = dao.changePwd(loginMember);
			
			// loginMember에 저장한 encPwd를 제거(Session에 비밀번호 저장하면 안된다.)
			//loginMember.setMemberPw(null);
			
			
			// 2) 글 부분만 수정
			result = dao.updateBoard(board);
			
			// 3) 이미지 관련 코드 작성
			if(result > 0) {
				// 3-1) deleteImages와 일치하는 파일 레벨의 ATTACHMENT행 삭제 
				// deleteImages : 삭제해야할 이미지 파일 레벨 -> , 를 구분자로 만들어진 String -> 0,1,2,3
				if(!deleteImages.equals("")) { // 삭제할 파일레벨이 존재하는 경우 -> 삭제
					// DB삭제 구문에 필요한 값 : deleteImages, boardNo
					// 두데이터를 한 번에 담을 VO가 없음 -> Map사용
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("boardNo", board.getBoardNo());
					map.put("deleteImages", deleteImages);
					
					// 반환값이 아무런 의미를 갖지 못하므로 반환 받을 필요가 없다.
					dao.deleteAttachment(map);
				}
				// 3-2) 수정된 이미지 정보 update
				List<Attachment> atList = new ArrayList<Attachment>();
				
				for(int i = 0; i < images.size(); i++) {
					if( !images.get(i).getOriginalFilename().equals("") ) {
						// images의 i번째 요소의 파일명이 ""이 아닐경우
						// -> 업로드된 파일이 없을 경우 파일명이 ""(빈문자열)로 존재함
						
						// 파일명 변경 작업 수행
						String fileName = rename(images.get(i).getOriginalFilename());
						
						// Attachment 객체 생성
						Attachment at = new Attachment();
						at.setFileName(fileName); // 변경한 파일명
						at.setFilePath(webPath); // 웹 접근 경로
						at.setBoardNo(board.getBoardNo()); // 수정중인 게시글 번호
						at.setFileLevel(i); // for문 반복자 == 파일레벨
						
						// 만들어진 객체를 atList에 추가
						atList.add(at);
					}
				}
				// atList를 하나씩 반복 접근하여 한 행씩 update진행
				for(Attachment at : atList) {
					result = dao.updateAttachment(at);
					
					// update를 시도했으나 결과가 0이 나온경우
					// == 기존에 이미지가 없던 레벨에 새로운 이미지가 추가되었다.
					// -> insert진행
					
					// 3-3) 기존에 이미지가 없던 레벨을 insert
					if(result == 0) {
						result = dao.insertAttchment(at);
						
						if(result == 0) { // 삽입실패
							// 강제로 예외를 발생시켜 전체 롤백 수행
							throw new InsertAttachmentException();
						}
					}
				}

				// 4) 새로 업로드된 이미지 서버에 저장 -> 게시글 추가에서 복사해온 것
				for(int i = 0; i < atList.size(); i++) {
					try {
						images.get( atList.get(i).getFileLevel() )
						.transferTo( new File(savePath + "/" + atList.get(i).getFileName() ) );
					} catch (Exception e) {
						e.printStackTrace();
						throw new SaveFileException();
					}
				}
//			}else {
//				result = -1;
			}
		}
		
		return result;
	}
	
	
	
	// 72시간보다 더 과거에 추가된 파일명 조회 
	@Override
	public List<String> selectDBList(String standard) {
		return dao.selectDBList(standard);
	}

	
	
	// 크로스 사이트 스크립트 방지 처리 메소드
	// 외부에서 사용가능하도록 private -> public, 공동위치에 올리기위해 static?
	public static String replaceParameter(String param) {
		String result = param;
		if(param != null) {
			result = result.replaceAll("&", "&amp;");
			result = result.replaceAll("<", "&lt;");
			result = result.replaceAll(">", "&gt;");
			result = result.replaceAll("\"", "&quot;");
		}
		
		return result;
	}
	
	// 파일명 변경 메소드
	private String rename(String originFileName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = sdf.format(new java.util.Date(System.currentTimeMillis()));
		
		int ranNum = (int)(Math.random()*100000); // 5자리 랜덤 숫자 생성
		
		String str = "_" + String.format("%05d", ranNum);
		
		String ext = originFileName.substring(originFileName.lastIndexOf("."));
		
		return date + str + ext;
	}

	
	// 게시글 삭제
	@Transactional(rollbackFor=Exception.class)
	@Override
	public int deleteBoard(Board board) {
		
		int result = dao.deleteBoard(board);
		
		System.out.println("게시글 삭제 결과 ser : " + result);
		
		return result;
	}









}
