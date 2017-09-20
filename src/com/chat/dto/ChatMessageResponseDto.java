package com.chat.dto;

import java.util.List;

public class ChatMessageResponseDto {
	private String from;
	private String to;
	private String groupChatName;
	
	private List<MessageDto> messageList;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public List<MessageDto> getMessageList() {
		return messageList;
	}
	public void setMessageList(List<MessageDto> messageList) {
		this.messageList = messageList;
	}	

	public String getGroupChatName() {
		return groupChatName;
	}
	public void setGroupChatName(String groupChatName) {
		this.groupChatName = groupChatName;
	}
	
}
