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
import com.chat.core.domain.GroupChat;
import com.chat.core.domain.Message;
import com.chat.core.domain.User;
import com.chat.core.repository.GroupChatRepository;
import com.chat.core.repository.MessageRepository;
import com.chat.core.repository.UserRepository;
import com.chat.core.util.Constants;
import com.chat.dto.UserStatusDto;

public class MessageRepositoryImpl extends Dao implements MessageRepository {

	private static Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);

	private static MessageRepositoryImpl messageRepository;
	
	private UserRepository userRepository = UserRepositoryImpl.getInstance();

	private GroupChatRepository groupChatRepository = GroupChatRepositoryImpl.getInstance();
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
	public void create(Message domain) throws SQLException {
		try {
			sql = "insert into message(id_user, id_user_from, content, status_message) values (? , ?, ?, ?)";

			Map<Integer, Object> parameters = new HashMap<>();
//			parameters.put(":id_team", domain.getGroup().getId());
			parameters.put(1, domain.getUserTo().getId());
			parameters.put(2, domain.getUserFrom().getId());
			parameters.put(3, domain.getDescriptionMessage());
			parameters.put(4, Constants.UNREAD);
			executeUpdate(sql, parameters);
		} catch (Exception e) {
			LOGGER.error("fail findById user :", domain.getUserFrom().getId(), e);
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
			parameters.put(":id_team", domain.getGroupChat().getId());
			parameters.put(":id_user", domain.getUserTo().getId());
			parameters.put(":username", domain.getUserFrom().getUsername());
			parameters.put(":status_message", Constants.UNREAD);
			getResulsetOf(ReplaceQueryParams(sql, parameters));
		} catch (Exception e) {
			LOGGER.error("fail update user :", domain.getUserFrom().getId(), e);
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
	
	private Map<String, Integer> getCountByRS(ResultSet rs) throws SQLException {
		Map<String, Integer> userCountMap = new HashMap<String, Integer>();
		try {
			while(rs.next()) {
				userCountMap.put(rs.getString("userFrom"), rs.getInt("count"));
			}
			System.out.print("User Count Map : " + userCountMap);
			return userCountMap;
		} catch (Exception e) {
			throw new SQLException("Cannot retrieve count");
		}  finally {
			closeConections();
		}		
	}

	private Map<Long, Integer> getGroupCountByRS(ResultSet rs) throws SQLException {
		Map<Long, Integer> userCountMap = new HashMap<Long, Integer>();
		try {
			while(rs.next()) {
				userCountMap.put(rs.getLong("groupchat_id"), rs.getInt("count"));
			}
			System.out.print("User Count Map : " + userCountMap);
			return userCountMap;
		} catch (Exception e) {
			throw new SQLException("Cannot retrieve count");
		}  finally {
			closeConections();
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
			message.setUserTo(new User());
			message.getUserTo().setId(rs.getLong("to_id"));
			message.getUserTo().setEmail(rs.getString("to_email"));			
			message.setUserFrom(new User());
			message.getUserFrom().setId(rs.getLong("from_id"));
			message.getUserFrom().setEmail(rs.getString("from_email"));			
			message.setGroupChat(new GroupChat());
			message.getGroupChat().setId(rs.getLong("id_team"));
			message.setStatusMesage(Boolean.valueOf(rs.getString("status_message")));
			message.setDescriptionMessage(rs.getString("content"));
			return message;
		} catch (Exception e) {
			LOGGER.error("fail to the getFullUserByRs", e);
			throw new SQLException("fail to the getFullUserByRs", e);
		}
	}
	
	@Override
	public void create(String fromUser, String toUser, String messageText) throws SQLException {
		User from = userRepository.getUserByEmail(fromUser);		
		User to = userRepository.getUserByEmail(toUser);
		Message message = new Message();
		message.setUserFrom(from);
		message.setUserTo(to);
		message.setDescriptionMessage(messageText);
		try {
			create(message);
		} catch (SQLException e) {
			LOGGER.error("fail to the create", e);
			throw new SQLException("fail to the create", e);			
		} finally {
			closeConections();
		}
	}

	@Override
	public void create(Long fromUser, List<UserStatusDto> toUser, String messageText, Long groupChatId) throws SQLException {
		try {
			sql = "insert into message(id_user, id_user_from, content, status_message, groupchat_id) values (? , ?, ?, ?, ?)";

			List<Map<Integer, Object>> batchParamList = new ArrayList<Map<Integer,Object>>();
			for(UserStatusDto dto : toUser) {
				Map<Integer, Object> parameters = new HashMap<>();
				parameters.put(1, dto.getId());
				parameters.put(2, fromUser);
				parameters.put(3, messageText);
				parameters.put(4, Constants.UNREAD);
				parameters.put(5, groupChatId);
				batchParamList.add(parameters);
			}
			executeBatch(sql, batchParamList);
		} catch (Exception e) {
			throw new SQLException("fail findById user", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public void create(String fromUser, String messageText, Long groupChatId) throws SQLException {
		try {
			User fromId = userRepository.getUserByEmail(fromUser);					
			GroupChat groupChat = groupChatRepository.findGroupChatById(groupChatId);
			create(fromId.getId(), groupChat.getMembers(), messageText, groupChatId);
		} catch (Exception e) {
			throw new SQLException("fail findById user", e);
		} finally {
			closeConections();
		}
	}	
	
	@Override
	public List<Message> getLastFewMessages(String fromUser, String toUser, int limit) throws SQLException {		
		try {
			sql = "select id_message, id_team, fu.id_user as from_id, tu.id_user as to_id, fu.email as from_email, tu.email as to_email, content, status_message from message m " +
				  " inner join user fu on m.id_user_from = fu.id_user inner join user tu on m.id_user = tu.id_user where ((tu.email = ':user_to' and fu.email = ':user_from') or (fu.email = ':user_to' and tu.email = ':user_from')) and m.groupchat_id IS NULL order by id_message desc limit :limit";			
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":user_to", toUser);
			parameters.put(":user_from", fromUser);
			parameters.put(":limit", limit);
			List<Message> messages = getMessagesByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
			return messages;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("failed to retrieve last few messages");
		} finally {
			closeConections();
		}
	}

	@Override
	public List<Message> getLastFewMessagesForGroupChat(String user, Long groupChatId, int limit) throws SQLException {		
		try {
			sql = "select id_message, id_team, fu.id_user as from_id, tu.id_user as to_id, fu.email as from_email, tu.email as to_email, content, status_message from message m " +
				  " inner join user fu on m.id_user_from = fu.id_user inner join user tu on m.id_user = tu.id_user where ((fu.email = ':user' and tu.email = ':user') or (fu.email <> ':user' and tu.email = ':user')) and groupchat_id = :group_chat_id order by id_message desc limit :limit";			
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":user", user);
			parameters.put(":group_chat_id", groupChatId);
			parameters.put(":limit", limit);
			List<Message> messages = getMessagesByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
			return messages;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("failed to retrieve last few messages");
		} finally {
			closeConections();
		}
	}
	
	
	@Override
	public Map<String, Integer> getUnreadMessageCount(String user) throws SQLException {
		try {
			sql = "select f.email as userFrom, count(*) as count from message m inner join user t on m.id_user = t.id_user inner join user f on m.id_user_from = f.id_user where t.email = ':user' and status_message = :message_status and m.groupchat_id IS NULL group by f.email"; 						
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":user", user);
			parameters.put(":message_status", Constants.UNREAD);			
			return getCountByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("Cannot retrieve unread message count");
		}  finally {
			closeConections();
		}

	}

	@Override
	public Map<Long, Integer> getUnreadMessageCountForGroupChat(String user) throws SQLException {
		try {
			sql = "select gc.id as groupchat_id, count(*) as count from message m inner join user u on m.id_user = u.id_user inner join groupchat gc on m.groupchat_id = gc.id where u.email = ':user' and m.id_user <> m.id_user_from and status_message = :message_status group by gc.id";						
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":user", user);
			parameters.put(":message_status", Constants.UNREAD);			
			return getGroupCountByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("Cannot retrieve unread message count");
		}  finally {
			closeConections();
		}

	}
	
	@Override
	public int markAllMessagesAsRead(String userTo, String userFrom) throws SQLException {
		try {
			sql = "update message m inner join user t on m.id_user = t.id_user inner join user f on m.id_user_from = f.id_user set m.status_message = ? where m.status_message = ? and t.email = ? and f.email = ?";
			Map<Integer, Object> parameters = new HashMap<>();
			parameters.put(1, Constants.READ);
			parameters.put(2, Constants.UNREAD);		
			parameters.put(3, userTo);		
			parameters.put(4, userFrom);		
			int count = executeUpdate(sql, parameters);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("Cannot retrieve unread message count");
		}  finally {
			closeConections();
		}
	}

	@Override
	public int markAllMessagesAsReadInGroupChat(String user, Long groupChatId) throws SQLException {
		try {
			sql = "update message m inner join user t on m.id_user = t.id_user set m.status_message = ? where m.status_message = ? and t.email = ? and m.groupchat_id = ?";
			Map<Integer, Object> parameters = new HashMap<>();
			parameters.put(1, Constants.READ);
			parameters.put(2, Constants.UNREAD);		
			parameters.put(3, user);		
			parameters.put(4, groupChatId);		
			int count = executeUpdate(sql, parameters);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("Cannot retrieve unread message count");
		}  finally {
			closeConections();
		}
	}
	
	@Override
	public int updateMessageStatusToRead(String fromUser, String toUser) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void create(String fromUser, String toUser, String messageText,
			Long groupChatId) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
