package com.chat.core.repository;

import java.util.List;
import java.util.Map;

import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.Message;
import com.chat.dto.UserStatusDto;

public interface MessageRepository extends CrudRepository<Message>{
	
	public void create(String fromUser, String toUser, String message) throws SQLException;	
	
	public List<Message> getLastFewMessages(String fromUser, String toUser, int limit) throws SQLException;

	public Map<String, Integer> getUnreadMessageCount(String user) throws SQLException;
	
	public int updateMessageStatusToRead(String fromUser, String toUser);

	int markAllMessagesAsRead(String userTo, String userFrom) throws SQLException;

	void create(String fromUser, String toUser, String messageText,
			Long groupChatId) throws SQLException;

	void create(Long fromUser, List<UserStatusDto> toUser, String messageText,
			Long groupChatId) throws SQLException;

	void create(String fromUser, String messageText, Long groupChatId)
			throws SQLException;

	List<Message> getLastFewMessagesForGroupChat(String user, Long groupChatId,
			int limit) throws SQLException;

	Map<Long, Integer> getUnreadMessageCountForGroupChat(String user
			) throws SQLException ;

	int markAllMessagesAsReadInGroupChat(String user, Long groupChatId)
			throws SQLException;
}
