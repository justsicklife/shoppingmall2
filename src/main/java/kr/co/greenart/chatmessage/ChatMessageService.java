package kr.co.greenart.chatmessage;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
	
	private final SqlSessionTemplate sql;
		
	private final ChatMessageRepository chatMessageDAO;
		
	public List<ChatMessageDTO> ChatMessageFindById(int chatRoomId) {
		return ChatMessageRepository.ChatMessageFindById(sql,chatRoomId);
	}
	
	public int ChatMessageInsert(ChatMessageDTO chatMessageDTO) {
		return ChatMessageRepository.ChatMessageInsert(sql,chatMessageDTO);
	}

}
