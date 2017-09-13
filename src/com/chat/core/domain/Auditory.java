package com.chat.core.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * this class is for do autitory for each class 
 * and for reuce attributes
 * 
 * @author Mario Silva
 *
 */
public class Auditory implements Serializable{

	private static final long serialVersionUID = 3549793542629207521L;

	/**
	 * this id be Inherited for the class
	 */
	private Long id;
	
	/**
	 * this id be Inherited for the class
	 * date register
	 */
	private Date dateCreate;
	
	/**
	 * this id be Inherited for the class
	 * last update for every object
	 */
	private Date dateUpdate;
	
	public Auditory() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate) {
		this.dateCreate = dateCreate;
	}

	public Date getDateUpdate() {
		return dateUpdate;
	}

	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = dateUpdate;
	}
}
