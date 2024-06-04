package kr.co.greenart.stomp;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import kr.co.greenart.chatmessage.ChatMessageDTO;
import kr.co.greenart.chatmessage.ChatMessageService;
import kr.co.greenart.chatroom.ChatRoomDTO;
import kr.co.greenart.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StompChatController {

	private final SimpMessagingTemplate template; // 특정 Broker 로 메시지 전달
	
	private final ChatMessageService chatMessageService;
	
	private final ChatRoomService chatRoomService;

	// 방에 들어왔을때
	@MessageMapping(value="/chat/enter")
	public void enter(ChatMessageDTO message) {
		template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(),message);
	}
	
	// 메세지를 입력했을때
	@MessageMapping(value="/chat/message")
	public void message(ChatMessageDTO message) {
		System.out.println("message : " + message);
		int ok = chatMessageService.ChatMessageInsert(message);

		template.convertAndSend("/sub/chat/room/" + message.getChatRoomId() ,message);
	}

	// 메세지 내역을 가져올때
	@MessageMapping(value="/user/history")
	public void userHistory(ChatRoomDTO chatRoomDTO) {
		System.out.println("ChatRoomDTO : " + chatRoomDTO);
		
		List<ChatMessageDTO> chatMessageDTO = chatMessageService.ChatMessageFindById(chatRoomDTO.getChatRoomId());
//		System.out.println("chatMessage : " + chatMessageDTO);
		template.convertAndSend("/sub/user/history/" + chatRoomDTO.getChatRoomId() ,chatMessageDTO);
	}
	
	// 메세지 내역을 가져올때
		@MessageMapping(value="/admin/history")
		public void adminHistory(ChatRoomDTO chatRoomDTO) {
			System.out.println("ChatRoomDTO : " + chatRoomDTO);
			
			// 기존에 선택된거 삭제
			int removeSuccess = chatRoomService.chatRoomSelectedRemove();
						
			// 여기서 selected 하면 될듯
			// 여기서 업데이트 하면 됨 
			int success = chatRoomService.chatRoomSelected(chatRoomDTO.getChatRoomId());
						
			List<ChatMessageDTO> chatMessageDTO = chatMessageService.ChatMessageFindById(chatRoomDTO.getChatRoomId());
			
			// 메시지 내역 가져올때
			
			template.convertAndSend("/sub/admin/history/" + chatRoomDTO.getChatRoomId() ,chatMessageDTO);
		}
	
	// 채팅방 목록을 가져올때
	@MessageMapping(value="/chat/list")
	public void list() {
		List<ChatRoomDTO> listChatRoomDTO = chatRoomService.chatRoomFindAll();
		System.out.println("list : " + listChatRoomDTO);
		template.convertAndSend("/sub/chat/list",listChatRoomDTO);
	}
	
	@MessageMapping(value="/chat/alarm")
	public void alarm(ChatRoomDTO chatRoomDTO) {
		
		// 선택된걸 찾는다 0 이라면 현재 선택된게 아님 
		int selected = chatRoomService.chatRoomFindSelected(chatRoomDTO);
		// 마지막 메시지를 가져옴 
		int lastMessageSuccess = chatRoomService.ChatMessageGetLastMessage(chatRoomDTO);
		
		if(selected == 0) {
			int success = chatRoomService.chatRoomAddAlert(chatRoomDTO);
			template.convertAndSend("/sub/chat/alarm",chatRoomDTO);
		} else {
			template.convertAndSend("/sub/chat/alarm",ChatRoomDTO.builder()
					.chatRoomId(-1)
					.memberIdx(-1)
					.chatRoomAlertCount(0)
					.chatRoomSelected(0)
					.chatRoomMessage(null)
					.memberName(null)
					.memberId(null).build());		
		}
		
	}
	
	@MessageMapping(value="/chat/delete_alarm")
	public void deleteAlram(ChatRoomDTO chatRoomDTO) {
		int success = chatRoomService.chatRoomRemoveAlert(chatRoomDTO);
		
		template.convertAndSend("/sub/chat/delete_alarm",chatRoomDTO);
	}
}


