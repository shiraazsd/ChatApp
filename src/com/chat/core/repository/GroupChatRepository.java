package com.chat.core.repository;

import java.util.List;
import java.util.Map;

import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.GroupChat;
import com.chat.core.domain.Message;

public interface GroupChatRepository extends CrudRepository<Message>{

	String create() throws SQLException;

	int getTotalGroupChatCount() throws SQLException;

	GroupChat findGroupChatById(Long chatId) throws SQLException;

	void addMemberToGroupChat(Long chatId, String member)
			throws SQLException;

	boolean checkGroupChatHasMember(Long chatId, String user)
			throws SQLException;

	String update(Long chatId, String chatName) throws SQLException;

	GroupChat findGroupChatByName(String chatName) throws SQLException;

	void removeMemberFromGroupChat(Long chatId, String member)
			throws SQLException;
	
}
