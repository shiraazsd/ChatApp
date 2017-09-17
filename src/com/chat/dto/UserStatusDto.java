package com.chat.dto;

public class UserStatusDto {
	private String user;
	private String status;
	private int notification;

	public UserStatusDto(String user, String status) {
		this.user = user;
		this.status = status;
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public int getNotification() {
		return notification;
	}

	public void setNotification(int notification) {
		this.notification = notification;
	}	
}
