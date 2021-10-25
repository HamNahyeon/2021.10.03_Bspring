package edu.kh.fin.board.model.service;

import java.util.List;

import edu.kh.fin.board.model.vo.Reply;

public interface ReplyService {

	/** 댓글 목록조회
	 * @param boardNo
	 * @return rList
	 */
	List<Reply> selectList(int boardNo);

	/** 댓글 삽입
	 * @param reply
	 * @return result
	 */
	int insertReply(Reply reply);

	/** 댓글수정
	 * @param reply
	 * @return result
	 */
	int updateReply(Reply reply);

	/** 댓글 삭제
	 * @param reply
	 * @return result
	 */
	int deleteReply(Reply reply);

}
