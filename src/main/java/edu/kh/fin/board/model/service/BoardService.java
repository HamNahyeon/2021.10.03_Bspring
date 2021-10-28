package edu.kh.fin.board.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.fin.board.model.vo.Board;
import edu.kh.fin.board.model.vo.Category;
import edu.kh.fin.board.model.vo.Pagination;
import edu.kh.fin.board.model.vo.Search;

public interface BoardService {

	
	/** 전체 게시글 수 + 게시판 이름 조회 
	 * @param pg
	 * @return pagination
	 */
	Pagination getPagination(Pagination pg);
	
	/** 전체 게시글 수 + 게시판 이르 조회 (검색)
	 * @param search
	 * @param pg
	 * @return pagination
	 */
	Pagination getPagination(Search search, Pagination pg);
	/* 오버로딩 된 것들은 순서대로 나열 해 놓는 것이 일반적 */
	
	
	/** 게시글 목록조회
	 * @param pagination
	 * @return boardList
	 */
	List<Board> selectBoardList(Pagination pagination);

	/** 게시글 목록조회 (검색)
	 * @param search
	 * @param pagination
	 * @return boardList
	 */
	List<Board> selectBoardList(Search search, Pagination pagination);
	
	
	/** 게시글 상세조회
	 * @param boardNo
	 * @return board
	 */
	Board selectBoardList(int boardNo);

	/** 게시글 카테고리별 목록조회
	 * @return
	 */
	List<Category> selectCategory();

	/** 게시글 삽입
	 * @param board
	 * @param images
	 * @param webPath
	 * @param savePath
	 * @return boardNo
	 */
	int insertBoard(Board board, List<MultipartFile> images, String webPath, String savePath);
	
	/** 답글 작성
	 * @param board
	 * @param images
	 * @param webPath
	 * @param savePath
	 * @return
	 */
	int insertReply(Board board, List<MultipartFile> images, String webPath, String savePath);
	
	/** 게시글 수정을 위한 상세조회
	 * @param board
	 * @return board
	 */
	Board selectUpdateBoard(int boardNo);

	/** 게시글 수정
	 * @param board
	 * @param currentPwd 
	 * @param images
	 * @param webPath
	 * @param savePath
	 * @param deleteImages
	 * @return result
	 */
	int updateBoard(
//			String currentPass,
			Board board, List<MultipartFile> images, 
					String webPath, String savePath, String deleteImages);
	

	/** 72시간보다 더 과거에 추가된 파일명 조회
	 * @param standard
	 * @return dbList
	 */
	List<String> selectDBList(String standard);

	/** 게시글 삭제
	 * @param currentPass 
	 * @param board
	 * @return result
	 */
	int deleteBoard(Board board);

	
	/** 삭제용 비밀번호조회
	 * @param boardNo
	 * @return
	 */
	String selectPassword(int boardNo);

}
