package com.chat.core.dao.ex;

public class SQLException extends java.sql.SQLException{


	private static final long serialVersionUID = 5440876681098239706L;
	
	public SQLException(String error) {
		super(error);
	}

	public SQLException(String error, Throwable causa) {
		super(error, causa);
	}

}
