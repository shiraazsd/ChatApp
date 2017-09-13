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
	private User userSendMessage;

	/**
	 * User Who receive message
	 */
	private User receiveMessage;

	/**
	 * group who receive message
	 */
	private Group group;

	public Message() {
		super();
	}

	public String getDescriptionMessage() {
		return descriptionMessage;
	}

	public void setDescriptionMessage(String descriptionMessage) {
		this.descriptionMessage = descriptionMessage;
	}

	public User getUserSendMessage() {
		return userSendMessage;
	}

	public void setUserSendMessage(User userSendMessage) {
		this.userSendMessage = userSendMessage;
	}

	public User getReceiveMessage() {
		return receiveMessage;
	}

	public void setReceiveMessage(User receiveMessage) {
		this.receiveMessage = receiveMessage;
	}

	public boolean isStatusMesage() {
		return statusMesage;
	}

	public void setStatusMesage(boolean statusMesage) {
		this.statusMesage = statusMesage;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
}
