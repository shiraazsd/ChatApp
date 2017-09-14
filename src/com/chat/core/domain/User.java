package com.chat.core.domain;

import java.io.Serializable;

import javax.faces.event.ValueChangeEvent;


/**
 * User object 
 * 
 * @author Mario Silva
 *
 */
public class User extends Auditory implements Serializable{

	private static final long serialVersionUID = -137867301132948241L;
	
	/**
	 * email this is attribute for that user can login 
	 */
	private String email;
	
	/**
	 *  username is how the another user will see you
	 *  in the platform
	 */
	private String username;
	
	/**
	 * password is attribute for login is secret for everypeople
	 */
	private String password;
	
	/**
	 * roler is the roler that have in the platform
	 */
	private Roler Roler;
	
	public User() {
		super();
	}
	
	public User(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Roler getRoler() {
		return Roler;
	}

	public void setRoler(Roler roler) {
		Roler = roler;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "User [email=" + email + ", username=" + username
				+ ", password=" + password + ", Roler=" + Roler + "]";
	}	
}
