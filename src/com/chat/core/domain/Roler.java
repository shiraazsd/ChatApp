package com.chat.core.domain;

import java.io.Serializable;

/**
 * Roler for the user
 * 
 * @author Mario Silva
 *
 */
public class Roler implements Serializable{
	
	private static final long serialVersionUID = -5877239169484204646L;

	/**
	 * key of the roler
	 */
	private Long idRoler;
	
	/**
	 * name of the role
	 */
	private String roleName;
	
	public Roler () {
	}

	public Long getIdRoler() {
		return idRoler;
	}

	public void setIdRoler(Long idRoler) {
		this.idRoler = idRoler;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
