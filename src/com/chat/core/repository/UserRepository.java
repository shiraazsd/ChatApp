package com.chat.core.repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.List;

import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.User;

public interface UserRepository extends CrudRepository<User>{
	
	/**
	 * this method if for get all the user with status active 
	 * receive how parameter the status, ever status is active
	 * 
	 * @param status
	 * @return return one list of {@link User}
	 * @throws SQLException
	 */
	public List<User> getUserByStatus(String status) throws SQLException;
	
	public User getUserByEmailAndPassword(String email, String password) throws SQLException;
	
	public User getUserByEmail(String email) throws SQLException;

	public List<User> getRecentUserList(String user) throws SQLException;

	public List<User> getUserListByEmail(List<String> emailList) throws SQLException;

	byte[] getUserProfilePicAsByte(String user) throws SQLException;

	byte[] getImageBlobAsBytes(ResultSet rs) throws SQLException;

	byte[] getDefaultUserProfilePicAsByte() throws IOException;;

}
