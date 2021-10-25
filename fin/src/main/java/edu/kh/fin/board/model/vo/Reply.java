package edu.kh.fin.board.model.vo;

import java.sql.Timestamp;

public class Reply {
	private int replyNo; 		 		// 댓글 번호
	private String replyContent; 		// 댓글 내용
	private Timestamp createDate;		// 댓글 작성일
	private int boardNo;			// 댓글이 작성된 게시글 번호
//	private int memberNo;				// 댓글 작성 회원 번호
	private String replyId;			// 댓글 작성 회원 이름
	private String replyPw;			// 댓글 작성 회원 이름
	
	public Reply() {
		// TODO Auto-generated constructor stub
	}
	
	public Reply(int replyNo, String replyContent, Timestamp createDate, int boardNo, String replyId, String replyPw) {
		super();
		this.replyNo = replyNo;
		this.replyContent = replyContent;
		this.createDate = createDate;
		this.boardNo = boardNo;
		this.replyId = replyId;
		this.replyPw = replyPw;
	}
	
	public int getReplyNo() {
		return replyNo;
	}

	public void setReplyNo(int replyNo) {
		this.replyNo = replyNo;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public int getBoardNo() {
		return boardNo;
	}

	public void setBoardNo(int boardNo) {
		this.boardNo = boardNo;
	}

	public String getReplyId() {
		return replyId;
	}

	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}

	public String getReplyPw() {
		return replyPw;
	}

	public void setReplyPw(String replyPw) {
		this.replyPw = replyPw;
	}

	@Override
	public String toString() {
		return "Reply [replyNo=" + replyNo + ", replyContent=" + replyContent + ", createDate=" + createDate
				+ ", boardNo=" + boardNo + ", replyId=" + replyId + ", replyPw=" + replyPw + "]";
	}




}