package com.chat.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.Message;
import com.chat.core.repository.MessageRepository;
import com.chat.core.repository.impl.MessageRepositoryImpl;
import com.chat.dto.ChatMessageResponseDto;
import com.chat.dto.MessageDto;

@Path("/chat")
public class ChatRestService {
	
	private final int CHAT_MESSAGE_LIMIT = 8;
	private static MessageRepository messageRepository = MessageRepositoryImpl.getInstance();
  
	
	
	@GET
	@Path("/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageDto sayPlainTextHello() {
		 return getDtoUtil().getTestMessageDto();
	}

	@GET
	@Path("/chatMessages")
	@Produces(MediaType.APPLICATION_JSON)
	public ChatMessageResponseDto getMessageFromToUser(@QueryParam("from") final String from, @QueryParam("to") final String to) { {
		ChatMessageResponseDto response = new ChatMessageResponseDto();
		List<MessageDto> result = null;
		try {
			List<Message> messageList = messageRepository.getLastFewMessages(from, to, CHAT_MESSAGE_LIMIT);
			result = getDtoUtil().convertIntoDto(messageList);
			response.setFrom(from);
			response.setTo(to);
			response.setMessageList(result);
		} catch (SQLException e) {
			System.out.print("Unable to retrieve from:to messages");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	}

	private DtoUtil getDtoUtil() {
		return DtoUtil.getInstance();
	}
}