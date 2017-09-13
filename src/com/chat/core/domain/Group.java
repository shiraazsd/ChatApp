package com.chat.core.domain;

import java.io.Serializable;
import java.util.List;

public class Group extends Auditory implements Serializable{

	private static final long serialVersionUID = 3720238864022587175L;

	/**
	 * name of the group for the chat
	 */
	private String groupName;
	
	/**
	 * List of users belonging to the group
	 */
	private List<User> users;
	
	/**
	 * List of messages belonging to the group
	 */
	private List<Message> messages;
	
	
	/***
	 * inheric of attributes of auditory
	 */
	public Group() {
		super();
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

}
