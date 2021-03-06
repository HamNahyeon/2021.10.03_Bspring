package edu.kh.fin.board.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.fin.board.model.vo.Reply;

@Repository
public class ReplyDAO {
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	/** 댓글 목록 조회
	 * @param boardNo
	 * @return rList
	 */
	public List<Reply> selectList(int boardNo) {
		return sqlSession.selectList("replyMapper.selectList", boardNo);
	}

	/** 댓글삽입
	 * @param reply
	 * @return result
	 */
	public int insertReply(Reply reply) {
		return sqlSession.insert("replyMapper.insertReply", reply);
	}

	/** 댓글 수정 삭제용 비밀번호 확인
	 * @param replyPass
	 * @return result
	 */
	public String selectPassword(int replyNo) {
		return sqlSession.selectOne("replyMapper.selectPassword", replyNo);
	}
	
	/** 댓글 수정
	 * @param reply
	 * @return result
	 */
	public int updateReply(Reply reply) {
		return sqlSession.update("replyMapper.updateReply", reply);
	}
	
	/** 댓글 삭제
	 * @param reply
	 * @return result
	 */
	public int deleteReply(Reply reply) {
		return sqlSession.update("replyMapper.deleteReply", reply);
	}

}
