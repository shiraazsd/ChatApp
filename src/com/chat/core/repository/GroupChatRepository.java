package com.chat.core.repository;

import java.util.List;
import java.util.Map;

import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.GroupChat;
import com.chat.core.domain.Message;

public interface GroupChatRepository extends CrudRepository<Message>{

	String create() throws SQLException;

	int getTotalGroupChatCount() throws SQLException;

	GroupChat findGroupChatById(Long groupChatId) throws SQLException;

	void addMemberToGroupChat(Long groupChatId, String member)
			throws SQLException;

	boolean checkGroupChatHasMember(Long groupChatId, String user)
			throws SQLException;

	String update(Long groupChatId, String groupChatName) throws SQLException;

	GroupChat findGroupChatByName(String groupChatName) throws SQLException;

	void removeMemberFromGroupChat(Long groupChatId, String member)
			throws SQLException;

	List<GroupChat> getGroupChatsForUser(String user) throws SQLException;
	
}
