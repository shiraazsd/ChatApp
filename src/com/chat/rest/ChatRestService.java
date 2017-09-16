package com.chat.rest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.chat.config.ChatEndPoint;
import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.Message;
import com.chat.core.domain.User;
import com.chat.core.repository.MessageRepository;
import com.chat.core.repository.UserRepository;
import com.chat.core.repository.impl.MessageRepositoryImpl;
import com.chat.core.repository.impl.UserRepositoryImpl;
import com.chat.core.util.Constants;
import com.chat.dto.ChatMessageResponseDto;
import com.chat.dto.MessageDto;
import com.chat.dto.UserStatusDto;

@Path("/chat")
public class ChatRestService {
		
	private static MessageRepository messageRepository = MessageRepositoryImpl.getInstance();
  
	private static UserRepository userRepository = UserRepositoryImpl.getInstance();
	
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
			List<Message> messageList = messageRepository.getLastFewMessages(from, to, Constants.CHAT_MESSAGE_LIMIT);
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

	@GET
	@Path("/getOnlineUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<UserStatusDto> getOnlineUsers(@QueryParam("loggedInUser") final String loggedInUser, @QueryParam("type") final String type) {
		if(Constants.ALL_USERS.equals(type)) {
			return getAllUserList(loggedInUser);
		} else if(Constants.ONLINE_USERS.equals(type)) {
			return getOnlineUserList(loggedInUser);
		} else {
			return getRecentUserList(loggedInUser);
		}
	}
	
	private Set<UserStatusDto> getAllUserList(String loggedInUser) {
		List<User> userList = null;
		try {
			userList = userRepository.findAll();
		} catch (SQLException e) {
			System.out.println("Unable to fetch all users");
			e.printStackTrace();
		}
		Set<UserStatusDto> users = new HashSet<UserStatusDto>();
		Set<String> onlineUsers = ChatEndPoint.getOnlineUsers();
		for(User user : userList) {
			if(user.getEmail().equals(loggedInUser)) {
				continue;
			}
			
			if(onlineUsers.contains(user.getEmail())) {
				users.add(new UserStatusDto(user.getEmail(), Constants.ONLINE));
			} else {
				users.add(new UserStatusDto(user.getEmail(), Constants.OFFLINE));
			}
		}
		return users;
	}
	
	private Set<UserStatusDto> getOnlineUserList(String loggedInUser) {
		Set<UserStatusDto> users = new HashSet<UserStatusDto>();
		Set<String> onlineUsers = ChatEndPoint.getOnlineUsers();
		for(String user : onlineUsers) {
			if(!user.equals(loggedInUser))
			users.add(new UserStatusDto(user, Constants.ONLINE));
		}
		return users;
	}

	private Set<UserStatusDto> getRecentUserList(String loggedInUser) {
		//TODO
		return null;
	}	
	
	private DtoUtil getDtoUtil() {
		return DtoUtil.getInstance();
	}
}