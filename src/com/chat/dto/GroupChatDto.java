package com.chat.dto;

import java.util.List;

public class GroupChatDto {
	private Long id;
	private String name;
	private List<UserStatusDto> members;
	
	public List<UserStatusDto> getMembers() {
		return members;
	}
	public void setMembers(List<UserStatusDto> members) {
		this.members = members;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
