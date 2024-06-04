package kr.co.greenart.chatroom;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final SqlSessionTemplate sql;
	
	private final ChatRoomRepository chatRoomDAO;
	
	public ChatRoomDTO chatRoomFindById(int id) {
		return chatRoomDAO.chatRoomFindById(sql,id);
	}
	
	public int chatRoomInsert(ChatRoomDTO chatRoomDTO) {
		return chatRoomDAO.chatRoomInsert(sql,chatRoomDTO);
	}

	public List<ChatRoomDTO> chatRoomFindAll() {
		return chatRoomDAO.chatRoomFindAll(sql);
	}

	public int chatRoomSelected(int id) {
		return chatRoomDAO.chatRoomSelected(sql,id);
	}

	public int chatRoomSelectedRemove() {
		return chatRoomDAO.chatRoomSelectedRemove(sql);
	}


	public int chatRoomAddAlert(ChatRoomDTO chatRoomDTO) {
		return chatRoomDAO.chatRoomAddAlert(sql,chatRoomDTO);
	}

	public int chatRoomRemoveAlert(ChatRoomDTO chatRoomDTO) {
		return chatRoomDAO.chatRoomRemoveAlert(sql,chatRoomDTO);
	}

	public int chatRoomFindSelected(ChatRoomDTO chatRoomDTO) {
		return chatRoomDAO.chatRoomFindSelected(sql,chatRoomDTO);
	}

	public int chatRoomSelectedReset() {
		return chatRoomDAO.chatRoomSelectedReset(sql);
	}

	public int ChatMessageGetLastMessage(ChatRoomDTO chatRoomDTO) {
		return chatRoomDAO.ChatMessageGetLastMessage(sql,chatRoomDTO);
	}
	
}
