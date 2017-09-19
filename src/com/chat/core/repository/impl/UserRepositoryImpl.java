package com.chat.core.repository.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.core.dao.Dao;
import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.Roler;
import com.chat.core.domain.User;
import com.chat.core.repository.UserRepository;
import com.chat.core.util.Constants;

public class UserRepositoryImpl extends Dao implements UserRepository {

	private static UserRepositoryImpl userRepository;

	private static Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);

	private String sql;

	private UserRepositoryImpl() {

	}

	/**
	 * pattern singleton
	 * 
	 * @return
	 */
	public static UserRepositoryImpl getInstance() {
		if (userRepository == null) {
			userRepository = new UserRepositoryImpl();
		}
		return userRepository;
	}

	@Override
	public User getUserByEmailAndPassword(String email, String password) throws SQLException {
		try {
			sql = "select u.id_user, u.id_roler, u.email, u.password, u.status from user u where u.email = :email and u.password = :password";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":email", "'"+email+"'");
			parameters.put(":password", "'"+password+"'");
			return getUserByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
		} catch (Exception e) {
			LOGGER.error("fail getUserByEmailAndPassword user :", email, e);
			throw new SQLException("fail getUserByEmailAndPassword user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public User getUserByEmail(String email) throws SQLException {
		try {
			sql = "select u.id_user, u.id_roler, u.email, u.password, u.status from user u where u.email = :email";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":email", "'"+email+"'");
			return getUserByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
		} catch (Exception e) {
			LOGGER.error("fail getUserByEmail user :", email, e);
			throw new SQLException("fail getUserByEmail user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public List<User> getUserListByEmail(List<String> emailList) throws SQLException {
		if(emailList.size() < 1)
			return new ArrayList<User>();
		StringBuilder builder = new StringBuilder();
		try {
			sql = "select u.id_user, u.id_roler, u.email, u.password, u.status from user u where u.email in (%s)";
			
			for( int i = 0 ; i < emailList.size(); i++ ) {
			    builder.append("?,");
			}			
			String inClause = builder.deleteCharAt( builder.length() -1 ).toString();
			sql = String.format(sql, inClause);
			Map<Integer, Object> parameters = new HashMap<>();
			for(int i = 1; i <= emailList.size(); ++i) {
				parameters.put(i, emailList.get(i-1));
			}
			return getUsersByRS(getResulsetOf(sql, parameters));
		} catch (Exception e) {
			LOGGER.error("fail getUserByEmail user :", emailList, e);
			throw new SQLException("fail getUserByEmail user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public User findById(Long id) throws SQLException {
		try {
			sql = "select u.id_user, u.id_roler, u.email, u.password, u.status from user u where u.id_user = :id_user";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":id_user", id);
			return getUserByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
		} catch (Exception e) {
			LOGGER.error("fail findById user :", id, e);
			throw new SQLException("fail findById user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public void create(User domain) throws SQLException {
		try {
			sql = "insert user into (id_roler, email, password, status) values (:id_roler, :email, :password, :status)"
					+ " select u.id_user, u.id_roler, u.email, u.password, u.status from user u where u.email = :email";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":email", "'" + domain.getEmail() + "'");
			parameters.put(":password", "'" + domain.getPassword() + "'");
			parameters.put(":id_roler", domain.getRoler().getIdRoler());
			parameters.put(":status", Constants.READ);
			getUserByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
		} catch (Exception e) {
			LOGGER.error("fail create user :", domain.getEmail(), e);
			throw new SQLException("fail create user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public void update(User domain) throws SQLException {
		try {
			sql = "update user set id_roler = :id_roler, email = :email, password= :password, status = :status where user.id_user = :id_user";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":id_user", domain.getId());
			parameters.put(":email", "'" + domain.getEmail() + "'");
			parameters.put(":password", "'" + domain.getPassword() + "'");
			parameters.put(":id_roler", domain.getRoler().getIdRoler());
			parameters.put(":status", Constants.READ);
			getResulsetOf(ReplaceQueryParams(sql, parameters));
		} catch (Exception e) {
			LOGGER.error("fail update user :", domain.getEmail(), e);
			throw new SQLException("fail update user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public void delete(User domain) throws SQLException {
		try {
			sql = "delete from user u where u.id_user = :id_user";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":id_user", domain.getId());
			getResulsetOf(ReplaceQueryParams(sql, parameters));
		} catch (Exception e) {
			LOGGER.error("fail delete user :", domain.getEmail(), e);
			throw new SQLException("fail delete user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public List<User> findAll() throws SQLException {
		try {
			sql = "select * from user";
			return getUsersByRS(getResulsetOf(sql));
		} catch (Exception e) {
			LOGGER.error("fail findAll users", e);
			throw new SQLException("fail findAll users", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public List<User> getUserByStatus(String status) throws SQLException {
		try {
			sql = "select u.id_user, u.id_roler, u.email, u.password, u.status from user u where u.status = :status";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":status", status);
			return getUsersByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
		} catch (Exception e) {
			LOGGER.error("fail getUserByStatus users", e);
			throw new SQLException("fail getUserByStatus users", e);
		} finally {
			closeConections();
		}
	}

	private List<User> getUsersByRS(ResultSet rs) throws SQLException {
		try {
			List<User> users = new ArrayList<>();
			while (rs.next()) {
				users.add(getFullUserByRs(rs));
			}
			return users;
		} catch (Exception e) {
			LOGGER.error("fail to the getUsersByRS", e);
			throw new SQLException("fail to the getUsersByRS", e);
		}
	}

	private User getUserByRS(ResultSet rs) throws SQLException {
		try {
			User user = null;

			while (rs.next()) {
				user = getFullUserByRs(rs);
			}
			return user;
		} catch (Exception e) {
			LOGGER.error("fail to the getUserByRS", e);
			throw new SQLException("fail to the getUserByRS", e);
		}
	}

	private User getFullUserByRs(ResultSet rs) throws SQLException {
		try {
			User user = new User();
			user.setId(rs.getLong("id_user"));
			user.setEmail(rs.getString("email"));
			user.setPassword(rs.getString("password"));
			user.setRoler(new Roler());
			user.getRoler().setIdRoler(rs.getLong("id_roler"));
			return user;
		} catch (Exception e) {
			LOGGER.error("fail to the getFullUserByRs", e);
			throw new SQLException("fail to the getFullUserByRs", e);
		}
	}
	
	@Override
	public List<User> getRecentUserList(String user) throws SQLException {
		try {
			sql = "select f.email, f.id_user, f.password, f.id_roler from message m inner join user t on m.id_user = t.id_user inner join user f on m.id_user_from = f.id_user where t.email = ':user' group by f.email"; 						
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":user", user);
			return getUsersByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("Cannot retrieve unread message count");
		}  finally {
			closeConections();
		}

	}
	
	
}
