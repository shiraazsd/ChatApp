package com.chat.core.repository;

import java.util.List;

import com.chat.core.dao.ex.SQLException;

public interface CrudRepository <T>{
	
	
	/**
	 * Method for find object for id
	 * receive long id how parameter
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public T findById(Long id) throws SQLException;
	
	/**
	 * method of create, receive some object of the domain
	 * 
	 * @param domain
	 * @return
	 * @throws SQLException
	 */
	public T create(T domain) throws SQLException;
	
	/**
	 * method for update object
	 * receive some object of the domain
	 * 
	 * @param domain
	 * @throws SQLException
	 */
	public void update(T domain) throws SQLException;
	
	/**
	 *method for delete object for domain
	 *that receive the id domain for 
	 *will do delete 
	 * 
	 * @param domain
	 * @throws SQLException
	 */
	public void delete(T domain) throws SQLException;
	
	/**
	 * search all object 
	 * return the list of objects 
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<T> findAll() throws SQLException;

}
