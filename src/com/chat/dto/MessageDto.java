package com.chat.dto;

public class MessageDto {
	private String from;
	private String to;
	private String content;
	private String messageTime;

	public MessageDto() {		
	}
	
	public MessageDto(String from, String to, String content, String messageTime) {
		this.from = from;
		this.to = to;
		this.content = content;
		this.messageTime = messageTime;
	}

	public String getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}
	
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
