package kr.co.greenart.chatroom;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.greenart.chatmessage.ChatMessageService;
import kr.co.greenart.product.ProductController;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
	
	private final ChatRoomService chatRoomService;
	
	private final ChatMessageService chatMessageService;
	
	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
	
	@GetMapping("/user")
	public String room(Model model,HttpSession session) {

		// memberId
		logger.info("chatController.user");
		int id =  (Integer)session.getAttribute("memberIdx");

		model.addAttribute("memberId",id);
		
		return "/chat/chat_user";
	}
	
	@GetMapping("/admin")
	public String admin(Model model,HttpSession session) {
		
		logger.info("chatController.admin");

		List<ChatRoomDTO> listChatRoomDTO = chatRoomService.chatRoomFindAll();
		
		int id =  (Integer)session.getAttribute("memberIdx");
				
		model.addAttribute("chatRoomList",listChatRoomDTO);
		
		model.addAttribute("memberId",id);
		
		return "/chat/chat_admin";
	}
	
	@PostMapping("/find")
	@ResponseBody
	public ChatRoomDTO find(ChatRoomDTO chatRoomDTO) {
		
		logger.info("chatController.find");
		ChatRoomDTO findChatRoomDTO = chatRoomService.chatRoomFindById(chatRoomDTO.getMemberIdx());
		
		if(findChatRoomDTO == null) {
			return ChatRoomDTO.builder()
					.chatRoomId(-1)
					.memberIdx(-1)
					.chatRoomAlertCount(0)
					.chatRoomSelected(0)
					.chatRoomMessage(null)
					.memberName(null)
					.memberId(null).build();
		} else {
			return findChatRoomDTO;			
		}
		
	}
	
	
	@PostMapping("/create")
	@ResponseBody
	public ChatRoomDTO create(ChatRoomDTO chatRoomDTO) {

		logger.info("chatController.create");

		int success = chatRoomService.chatRoomInsert(chatRoomDTO);
				
		ChatRoomDTO findChatRoomDTO = ChatRoomDTO.builder()
				.chatRoomId(chatRoomDTO.getChatRoomId())
				.memberIdx(chatRoomDTO.getMemberIdx())
				.chatRoomAlertCount(0)
				.chatRoomSelected(0)
				.chatRoomMessage(null)
				.memberName(null)
				.memberId(null).build();
				
		return findChatRoomDTO;
	}
	
	@PostMapping("/reset")
	@ResponseBody
	public String reset() {
		logger.info("chatController.reset");
		
		int success = chatRoomService.chatRoomSelectedReset();
		
		if(success == 0) {
			return "success";
		}else {
			return "faild";
		}
	}
}
