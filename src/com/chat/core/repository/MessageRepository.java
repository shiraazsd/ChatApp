package com.chat.core.repository;

import java.util.List;

import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.Message;

public interface MessageRepository extends CrudRepository<Message>{
	
	public void create(String fromUser, String toUser, String message) throws SQLException;	
	
	public List<Message> getLastFewMessages(String fromUser, String toUser, int limit) throws SQLException;
}
