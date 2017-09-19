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
import com.chat.core.repository.GroupRepository;
import com.chat.core.repository.UserRepository;
import com.chat.core.util.Constants;
import com.chat.dto.UserStatusDto;

public class GroupChatRepositoryImpl extends Dao implements GroupChatRepository {

	private static Logger LOGGER = LoggerFactory.getLogger(GroupChatRepositoryImpl.class);

	private String sql;

	private static GroupChatRepositoryImpl groupChatRepositoryImpl;

	private UserRepository userRepository = UserRepositoryImpl.getInstance();
	
	private GroupChatRepositoryImpl() {

	}

	/**
	 * pattern singleton
	 * 
	 * @return
	 */
	public static GroupChatRepositoryImpl getInstance() {
		if (groupChatRepositoryImpl == null) {
			groupChatRepositoryImpl = new GroupChatRepositoryImpl();
		}
		return groupChatRepositoryImpl;
	}
	
	private GroupChat getGroupChatWithMemberByRs(ResultSet rs) throws SQLException {		
		try {
			List<UserStatusDto> members = new ArrayList<UserStatusDto>();
			GroupChat groupChat = new GroupChat();
			while(rs.next()) {
				if(groupChat.getId() == null) {
					groupChat.setId(rs.getLong("id"));
					groupChat.setName(rs.getString("chatname"));
				}
				members.add(new UserStatusDto(rs.getString("user"), Constants.OFFLINE));
			}			
			groupChat.setMembers(members);
			return groupChat;
		} catch (Exception e) {
			LOGGER.error("fail to the getFullGrouprByRs", e);
			throw new SQLException("fail to the getFullGrouprByRs", e);
		}
	}

	private List<GroupChat> getGroupChatsWithoutMemberByRs(ResultSet rs) throws SQLException {		
		try {
			List<UserStatusDto> members = new ArrayList<UserStatusDto>();
			List<GroupChat> groupChatList = new ArrayList<GroupChat>();
			while(rs.next()) {
				GroupChat groupChat = new GroupChat();
				groupChat.setId(rs.getLong("id"));
				groupChat.setName(rs.getString("chatname"));
				groupChatList.add(groupChat);
			}			
			return groupChatList;
		} catch (Exception e) {
			LOGGER.error("fail to the getFullGrouprByRs", e);
			throw new SQLException("fail to the getFullGrouprByRs", e);
		}
	}
	
	@Override
	public Message findById(Long id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String create() throws SQLException {
	try{
		String sql = "insert into groupchat(name, status) values(?, ?)";
		Map<Integer, Object> parameters = new HashMap<>();
		String chatName = String.format(Constants.GROUP_CHAT_DEFAULT_NAME, getTotalGroupChatCount());
		parameters.put(1, chatName);
		parameters.put(2, Constants.GROUP_CHAT_ACTIVE);		
		executeUpdate(sql, parameters);
		return chatName;
	} catch (Exception e) {
		e.printStackTrace();
		throw new SQLException("Failed to create new group chat");
	}  finally {
		closeConections();
	}				
	}

	@Override
	public String update(Long chatId, String chatName) throws SQLException {
	try{
		String sql = "update groupchat set name = ? where id = ?";
		Map<Integer, Object> parameters = new HashMap<>();
		parameters.put(1, chatName);
		parameters.put(2, chatId);		
		executeUpdate(sql, parameters);
		return chatName;
	} catch (Exception e) {
		e.printStackTrace();
		throw new SQLException("Failed to create new group chat");
	}  finally {
		closeConections();
	}				
	}
	
	@Override
	public void addMemberToGroupChat(Long chatId, String member) throws SQLException {
		if(checkGroupChatHasMember(chatId, member)) {
			return;
		}
		User user = userRepository.getUserByEmail(member);
	try{
		sql = "insert into groupchatuser(groupchat_id, user_id, status) values(?, ?, ?)";
		Map<Integer, Object> parameters = new HashMap<>();
		parameters.put(1, chatId);
		parameters.put(2, user.getId());		
		parameters.put(3, Constants.GROUP_CHAT_ACTIVE);		
		executeUpdate(sql, parameters);
	} catch (Exception e) {
		e.printStackTrace();
		throw new SQLException("Cannot retrieve unread message count");
	}  finally {
		closeConections();
	}				
	}

	@Override
	public void removeMemberFromGroupChat(Long chatId, String member) throws SQLException {
		if(!checkGroupChatHasMember(chatId, member)) {
			return;
		}
		User user = userRepository.getUserByEmail(member);
	try{
		sql = "delete from groupchatuser where groupchat_id = ? and user_id = ?";
		Map<Integer, Object> parameters = new HashMap<>();
		parameters.put(1, chatId);
		parameters.put(2, user.getId());		
		executeUpdate(sql, parameters);
	} catch (Exception e) {
		e.printStackTrace();
		throw new SQLException("Cannot remove user from group chat");
	}  finally {
		closeConections();
	}				
	}
	
	
	@Override
	public GroupChat findGroupChatById(Long chatId) throws SQLException {
	try{
		sql = "select g.id, g.name as chatname, u.email as user from groupchat g left outer join groupchatuser gu on g.id = gu.groupchat_id left outer join user u on gu.user_id = u.id_user where g.id = ?";
		Map<Integer, Object> parameters = new HashMap<>();
		parameters.put(1, chatId);
		return getGroupChatWithMemberByRs(getResulsetOf(sql, parameters));
	} catch (Exception e) {
		e.printStackTrace();
		throw new SQLException("Cannot retrieve unread message count");
	}  finally {
		closeConections();
	}				
	}
	
	@Override
	public List<GroupChat> getGroupChatsForUser(String user) throws SQLException {		
	try{
		sql = "	select g.id, g.name as chatname from groupchatuser gu inner join groupchat g on gu.groupchat_id = g.id inner join user u on gu.user_id = u.id_user where u.email = ?";
		Map<Integer, Object> parameters = new HashMap<>();
		parameters.put(1, user);
		return getGroupChatsWithoutMemberByRs(getResulsetOf(sql, parameters));
	} catch (Exception e) {
		e.printStackTrace();
		throw new SQLException("Cannot retrieve unread message count");
	}  finally {
		closeConections();
	}				
	}

			
	@Override
	public GroupChat findGroupChatByName(String chatName) throws SQLException {
	try{
		sql = "select g.id, g.name as chatname, u.email as user from groupchat g left outer join groupchatuser gu on g.id = gu.groupchat_id left outer join user u on gu.user_id = u.id_user where g.name = ?";
		Map<Integer, Object> parameters = new HashMap<>();
		parameters.put(1, chatName);
		return getGroupChatWithMemberByRs(getResulsetOf(sql, parameters));
	} catch (Exception e) {
		e.printStackTrace();
		throw new SQLException("Cannot retrieve unread message count");
	}  finally {
		closeConections();
	}				
	}

	@Override
	public boolean checkGroupChatHasMember(Long chatId, String user) throws SQLException {
	try{
		sql = "select count(*) as count from groupchat g left outer join groupchatuser gu on g.id = gu.groupchat_id left outer join user u on gu.user_id = u.id_user where g.id = ? and u.email = ?";
		Map<Integer, Object> parameters = new HashMap<>();
		parameters.put(1, chatId);
		parameters.put(2, user);
		return getCountByRS(getResulsetOf(sql, parameters)) >= 1;
	} catch (Exception e) {
		e.printStackTrace();
		throw new SQLException("Cannot retrieve count");
	}  finally {
		closeConections();
	}				
	}

	@Override
	public int getTotalGroupChatCount() throws SQLException {
		try {
			sql = "select count(*) as count from groupchat"; 						
			Map<String, Object> parameters = new HashMap<>();
			return getCountByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("Cannot retrieve unread message count");
		}  finally {
			closeConections();
		}		
	}
	
	private int getCountByRS(ResultSet rs) throws SQLException {
		try {
			while(rs.next()) {
				return rs.getInt("count");
			}
		} catch (Exception e) {
			throw new SQLException("Cannot retrieve count");
		}  finally {
			closeConections();
		}
		return -1;		
	}

	@Override
	public void update(Message domain) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Message domain) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Message> findAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create(Message domain) throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
