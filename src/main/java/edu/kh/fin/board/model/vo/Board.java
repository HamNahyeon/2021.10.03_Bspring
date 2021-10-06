package edu.kh.fin.board.model.vo;

import java.sql.Timestamp;
import java.util.List;

public class Board {

	private int boardNo;
	private String boardTitle;
	private String memberName;
	private String categoryName;
	private int readCount;	
	private Timestamp createDate;
	
	private String boardWriter;
	private String boardPass;
	
	private String boardContent;	// 글 내용
	private int memberNo;			// 작성 회원 번호
	private Timestamp modifyDate;	// 마지막 수정일
	private List<Attachment> atList;// 게시글에 첨부된 파일(이미지) 목록
	
	// 삽입 시 필요한 필드 추가
	int boardType;
	private int categoryCode;
	
	
	public Board() {}

	// 게시글 작성용 매개변수 생성자
	public Board(int boardNo, String boardTitle, String boardContent, int categoryCode, int boardType,
			String boardWriter, String boardPass) {
		super();
		this.boardNo = boardNo;
		this.boardTitle = boardTitle;
		this.boardContent = boardContent;
		this.categoryCode = categoryCode;
		this.boardType = boardType;
		this.boardWriter = boardWriter;
		this.boardPass = boardPass;
	}
	
	// 게시글 조회용 
	public Board(int boardNo, String categoryName, String boardTitle, String boardContent, String boardWriter, 
				int readCount, Timestamp createDate, Timestamp modifyDate, int boardType, String boardPass) {
		super();
		this.boardNo = boardNo;
		this.categoryName = categoryName;
		this.boardTitle = boardTitle;
		this.boardContent = boardContent;
		this.boardWriter = boardWriter;
		this.readCount = readCount;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
		this.boardType = boardType;
		this.boardPass = boardPass;
	}

	public int getBoardNo() {
		return boardNo;
	}

	
	public void setBoardNo(int boardNo) {
		this.boardNo = boardNo;
	}


	public String getBoardTitle() {
		return boardTitle;
	}


	public void setBoardTitle(String boardTitle) {
		this.boardTitle = boardTitle;
	}


	public String getMemberName() {
		return memberName;
	}


	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}


	public String getCategoryName() {
		return categoryName;
	}


	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}


	public int getReadCount() {
		return readCount;
	}


	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}


	public Timestamp getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}



	public String getBoardContent() {
		return boardContent;
	}


	public void setBoardContent(String boardContent) {
		this.boardContent = boardContent;
	}


	public int getMemberNo() {
		return memberNo;
	}


	public void setMemberNo(int memberNo) {
		this.memberNo = memberNo;
	}


	public Timestamp getModifyDate() {
		return modifyDate;
	}


	public void setModifyDate(Timestamp modifyDate) {
		this.modifyDate = modifyDate;
	}


	public List<Attachment> getAtList() {
		return atList;
	}


	public void setAtList(List<Attachment> atList) {
		this.atList = atList;
	}


	public int getCategoryCode() {
		return categoryCode;
	}


	public void setCategoryCode(int categoryCode) {
		this.categoryCode = categoryCode;
	}


	public int getBoardType() {
		return boardType;
	}


	public void setBoardType(int boardType) {
		this.boardType = boardType;
	}


	public String getBoardWriter() {
		return boardWriter;
	}


	public void setBoardWriter(String boardWriter) {
		this.boardWriter = boardWriter;
	}


	public String getBoardPass() {
		return boardPass;
	}


	public void setBoardPass(String boardPass) {
		this.boardPass = boardPass;
	}


	@Override
	public String toString() {
		return "Board [boardNo=" + boardNo + ", boardTitle=" + boardTitle + ", memberName=" + memberName
				+ ", categoryName=" + categoryName + ", readCount=" + readCount + ", createDate=" + createDate
				+ ", boardWriter=" + boardWriter + ", boardPass=" + boardPass + ", boardContent=" + boardContent
				+ ", memberNo=" + memberNo + ", modifyDate=" + modifyDate + ", atList=" + atList + ", boardType="
				+ boardType + ", categoryCode=" + categoryCode + "]";
	}




	
	

}
