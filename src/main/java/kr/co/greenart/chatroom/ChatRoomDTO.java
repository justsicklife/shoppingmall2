package kr.co.greenart.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ChatRoomDTO {
	private int chatRoomId;
	private int memberIdx;
	private int chatRoomAlertCount;
	private int chatRoomSelected;
	private String chatRoomMessage;
	
	private String memberName;
	private String memberId;
}
