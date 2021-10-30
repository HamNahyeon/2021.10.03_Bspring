package edu.kh.fin.board.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.fin.board.model.dao.ReplyDAO;
import edu.kh.fin.board.model.vo.Reply;

@Service
public class ReplyServiceImpl implements ReplyService{
	
	@Autowired
	private ReplyDAO dao;

	// 댓글 목록 조회
	@Override
	public List<Reply> selectList(int boardNo) {
		return dao.selectList(boardNo);
	}

	// 댓글 삽입
	@Transactional(rollbackFor=Exception.class) // 예외가 발생하면 롤백을 시키겠다 DML구문은 트랜잭션 必
	@Override
	public int insertReply(Reply reply) {
		
		// 크로스사이트 스크립트 방지처리
		reply.setReplyContent( BoardServiceImpl.replaceParameter( reply.getReplyContent() ) );
		reply.setReplyContent( reply.getReplyContent().replaceAll("(\r\n|\r|\n|\n\r)", "<br>") );
		reply.setReplyContent(  reply.getReplyContent().replaceAll(" ", "&nbsp")  );
		reply.setReplyId( BoardServiceImpl.replaceParameter( reply.getReplyId() ) );
		reply.setReplyId( reply.getReplyId().replaceAll("(\r\n|\r|\n|\n\r)", "<br>") );
		reply.setReplyId(  reply.getReplyId().replaceAll(" ", "&nbsp")  );
		reply.setReplyPw( BoardServiceImpl.replaceParameter( reply.getReplyPw() ) );
		reply.setReplyPw( reply.getReplyPw().replaceAll("(\r\n|\r|\n|\n\r)", "<br>") );
		reply.setReplyPw(  reply.getReplyPw().replaceAll(" ", "&nbsp")  );
		
		return dao.insertReply(reply);
	}

	// 댓글 수정
	@Transactional(rollbackFor=Exception.class)
	@Override
	public int updateReply(Reply reply) {
		
		// 크로스사이트 스크립트 방지처리
		reply.setReplyContent( BoardServiceImpl.replaceParameter( reply.getReplyContent() ) );
		reply.setReplyContent( reply.getReplyContent().replaceAll("(\r\n|\r|\n|\n\r)", "<br>") );
		
		return dao.updateReply(reply);
	}
	
//	// 댓글 수정 비밀번호 반영
//	@Transactional(rollbackFor=Exception.class)
//	@Override
//	public int updateReply(Reply reply, String rPass) {
//		
//		// 댓글 수정 용 비밀번호 조회
//		String savePw = dao.selectPassword( reply.getReplyNo() );
//		
//		int result = 0;
//		
//		if(savePw.equals(rPass)) {
//			// 크로스사이트 스크립트 방지처리
//			reply.setReplyContent( BoardServiceImpl.replaceParameter( reply.getReplyContent() ) );
//			reply.setReplyContent( reply.getReplyContent().replaceAll("(\r\n|\r|\n|\n\r)", "<br>") );
//			
//			result = dao.updateReply(reply);
//		}else {
//			result = -1;
//		}
//		
//		return result;
//	}
	
	// 댓글 삭제
	@Transactional(rollbackFor=Exception.class)
	@Override
	public int deleteReply(Reply reply) {
		return dao.deleteReply(reply);
	}

	
}
