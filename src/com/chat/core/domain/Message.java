package com.chat.core.domain;

import java.io.Serializable;

/**
 * Object message for the chat
 * 
 * @author Mario Silva
 *
 */
public class Message extends Auditory implements Serializable {

	private static final long serialVersionUID = -2365739997644685562L;

	/**
	 * text for the chat
	 */
	private String descriptionMessage;

	/**
	 * state message can be view, not view
	 */
	private boolean statusMesage;

	/**
	 * User who send messaje
	 */
	private User userFrom;

	/**
	 * User Who receive message
	 */
	private User UserTo;

	/**
	 * group who receive message
	 */
	private GroupChat groupChat;
	
	private String messageTime;
	
	public Message() {
		super();
	}

	public String getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}
	
	public String getDescriptionMessage() {
		return descriptionMessage;
	}

	public void setDescriptionMessage(String descriptionMessage) {
		this.descriptionMessage = descriptionMessage;
	}

	public User getUserFrom() {
		return userFrom;
	}

	public void setUserFrom(User userSendMessage) {
		this.userFrom = userSendMessage;
	}

	public User getUserTo() {
		return UserTo;
	}

	public void setUserTo(User receiveMessage) {
		this.UserTo = receiveMessage;
	}

	public boolean isStatusMesage() {
		return statusMesage;
	}

	public void setStatusMesage(boolean statusMesage) {
		this.statusMesage = statusMesage;
	}

	public GroupChat getGroupChat() {
		return groupChat;
	}

	public void setGroupChat(GroupChat groupChat) {
		this.groupChat = groupChat;
	}

	@Override
	public String toString() {
		return "Message [descriptionMessage=" + descriptionMessage
				+ ", statusMesage=" + statusMesage + ", userFrom=" + userFrom
				+ ", UserTo=" + UserTo + "]";
	}
}
