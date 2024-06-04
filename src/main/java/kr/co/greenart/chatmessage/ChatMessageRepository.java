package kr.co.greenart.chatmessage;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatMessageRepository {

	public static List<ChatMessageDTO> ChatMessageFindById(SqlSessionTemplate sql, int chatRoomId) {
		return sql.selectList("s_chat_message.ChatMessageFindById",chatRoomId);
	}

	public static int ChatMessageInsert(SqlSessionTemplate sql, ChatMessageDTO chatMessageDTO) {
		return sql.insert("s_chat_message.ChatMessageInsert",chatMessageDTO);
	}

}
