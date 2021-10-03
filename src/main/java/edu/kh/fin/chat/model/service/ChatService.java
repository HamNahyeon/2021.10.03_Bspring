package edu.kh.fin.chat.model.service;

import java.util.List;

import edu.kh.fin.chat.model.vo.ChatMessage;
import edu.kh.fin.chat.model.vo.ChatRoom;
import edu.kh.fin.chat.model.vo.ChatRoomJoin;

public interface ChatService {

	// public abstract 
	// 모든 interface 는 public abstract 이기 때문에 기본적으로 생략되어있다.
	// 안써도 써도 됨
	// public abstract List<ChatRoom> selectRoomList();
	
	/** 채팅 목록조회 Service
	 * @return
	 */
	List<ChatRoom> selectRoomList();

	
	/** 채팅방만들기 Service
	 * @param room
	 * @return chatRoom
	 */
	int openChatRoom(ChatRoom room);

	/** 채팅방 입장 + 내용얻어오기
	 * @param join
	 * @return list
	 */
	List<ChatMessage> joinChatRoom(ChatRoomJoin join);


	/** 채팅내용 삽입
	 * @param cm
	 * @return result
	 */
	int insertMessage(ChatMessage cm);

}
