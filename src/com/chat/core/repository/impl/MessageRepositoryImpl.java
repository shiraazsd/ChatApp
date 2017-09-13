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
import com.chat.core.domain.Group;
import com.chat.core.domain.Message;
import com.chat.core.domain.User;
import com.chat.core.repository.MessageRepository;
import com.chat.core.util.Constants;

public class MessageRepositoryImpl extends Dao implements MessageRepository {

	private static Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);

	private static MessageRepositoryImpl messageRepository;

	private String sql;

	private MessageRepositoryImpl() {

	}

	/**
	 * pattern singleton
	 * 
	 * @return
	 */
	public static MessageRepositoryImpl getInstance() {
		if (messageRepository == null) {
			messageRepository = new MessageRepositoryImpl();
		}
		return messageRepository;
	}

	@Override
	public Message findById(Long id) throws SQLException {
		try {
			sql = "select m.id_message, m.id_team, m.id_user, u.username, m.username as admin_user, m.status_message from message m"
					+ " inner join user u on u.id_user = m.id_user  where m.id_message = :id_user";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":id_message", id);
			return getMessageByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)), Boolean.FALSE);
		} catch (Exception e) {
			LOGGER.error("fail findById user :", id, e);
			throw new SQLException("fail findById user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public Message create(Message domain) throws SQLException {
		try {
			sql = "insert message into (id_team, id_user, username, status_message) values (:id_team, :id_user, :username, :status_message)";

			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":id_team", domain.getGroup().getId());
			parameters.put(":id_user", domain.getReceiveMessage().getId());
			parameters.put(":username", domain.getUserSendMessage().getUsername());
			parameters.put(":status_message", Constants.INACTIVE);
			return getMessageByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)), Boolean.FALSE);
		} catch (Exception e) {
			LOGGER.error("fail findById user :", domain.getUserSendMessage().getId(), e);
			throw new SQLException("fail findById user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public void update(Message domain) throws SQLException {
		try {
			sql = "update message set   into (id_team, id_user, username, status_message) values (:id_team, :id_user, :username, :status_message)";

			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":id_team", domain.getGroup().getId());
			parameters.put(":id_user", domain.getReceiveMessage().getId());
			parameters.put(":username", domain.getUserSendMessage().getUsername());
			parameters.put(":status_message", Constants.INACTIVE);
			getResulsetOf(ReplaceQueryParams(sql, parameters));
		} catch (Exception e) {
			LOGGER.error("fail update user :", domain.getUserSendMessage().getId(), e);
			throw new SQLException("fail update user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public void delete(Message domain) throws SQLException {
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":id_message", domain.getId());
			sql = "delete from message where id_message = :id_message";
			getResulsetOf(ReplaceQueryParams(sql, parameters));
		} catch (Exception e) {
			LOGGER.error("fail delete user :", domain.getId(), e);
			throw new SQLException("fail delete user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public List<Message> findAll() throws SQLException {
		try {
			sql = "select m.id_message, m.id_user, u.username, m.username as admin_user, m.id_team, m.status_mesage from messaje ms "
					+ "inner join user u on u.id_user = m.id_user ";
			return getMessagesByRS(getResulsetOf(sql));
		} catch (Exception e) {
			LOGGER.error("fail findAll users", e);
			throw new SQLException("fail findAll users", e);
		} finally {
			closeConections();
		}
	}

	private List<Message> getMessagesByRS(ResultSet rs) throws SQLException {
		try {
			List<Message> messages = new ArrayList<>();
			while (rs.next()) {
				messages.add(getMessageByRS(rs, Boolean.TRUE));
			}
			return messages;
		} catch (Exception e) {
			LOGGER.error("fail to the getMessagesByRS", e);
			throw new SQLException("fail to the getMessagesByRS", e);
		}
	}

	private Message getMessageByRS(ResultSet rs, Boolean isList) throws SQLException {
		try {
			Message message = null;
			if (isList) {
				message = getFullMessageByRs(rs);
			} else {
				while (rs.next()) {
					message = getFullMessageByRs(rs);
				}
			}
			return message;
		} catch (Exception e) {
			LOGGER.error("fail to the getMessageByRS", e);
			throw new SQLException("fail to the getMessageByRS", e);
		}
	}

	/**
	 * full the object {@link Message} of the resulset 
	 * 
	 * @param rs
	 * @return the object {@link Message}
	 * @throws SQLException
	 */
	private Message getFullMessageByRs(ResultSet rs) throws SQLException {
		try {
			Message message = new Message();
			message.setId(rs.getLong("id_message"));
			message.setReceiveMessage(new User());
			message.getReceiveMessage().setId(rs.getLong("id_user"));
			message.getReceiveMessage().setUsername("username");
			message.setUserSendMessage(new User());
			message.getUserSendMessage().setUsername(rs.getString("admin_user"));
			message.setGroup(new Group());
			message.getGroup().setId(rs.getLong("id_team"));
			message.setStatusMesage(Boolean.valueOf(rs.getString("status_message")));
			return message;
		} catch (Exception e) {
			LOGGER.error("fail to the getFullUserByRs", e);
			throw new SQLException("fail to the getFullUserByRs", e);
		}
	}

}
