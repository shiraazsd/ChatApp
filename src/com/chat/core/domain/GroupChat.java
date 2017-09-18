package com.chat.core.domain;

import java.io.Serializable;
import java.util.List;

import com.chat.dto.UserStatusDto;

/**
 * Object message for the chat
 * 
 * @author Shiraaz
 *
 */
public class GroupChat extends Auditory implements Serializable {

	private static final long serialVersionUID = -2365739997644685562L;
	
	private String name;
	
	private int status;
	
	private List<UserStatusDto> members;
	
	public List<UserStatusDto> getMembers() {
		return members;
	}

	public void setMembers(List<UserStatusDto> members) {
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
