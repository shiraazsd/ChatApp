package com.chat.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.GroupChat;
import com.chat.core.repository.GroupChatRepository;
import com.chat.core.repository.MessageRepository;
import com.chat.core.repository.impl.GroupChatRepositoryImpl;
import com.chat.core.repository.impl.MessageRepositoryImpl;
import com.chat.core.util.Constants;
import com.chat.core.util.UserStatusDtoComparator;
import com.chat.dto.UserStatusDto;

@ServerEndpoint(value = "/chat/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndPoint {

	private final Logger log = Logger.getLogger(getClass().getName());

	private static MessageRepository messageRespoitory = MessageRepositoryImpl.getInstance();
	
	private GroupChatRepository groupChatRepository = GroupChatRepositoryImpl.getInstance();
	
	private Session session;
	private String username;
	private static final Set<ChatEndPoint> chatEndpoints = new CopyOnWriteArraySet<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
		log.info(session.getId() + " connected!");
		this.session = session;
		this.username = username;
		chatEndpoints.add(this);
		broadcastRefreshContact();
		System.out.println("Active endpoints count : " +  chatEndpoints.size());
		System.out.println("Active endpoints : " +  chatEndpoints);		
	}

	@OnMessage
	public void onMessage(Session session, Message message) throws IOException, EncodeException {
		log.info(message.toString());
		System.out.println("onMessage : " + message.toString());
		message.setFrom(getUser(session.getId()));
		handleMessage(message);
	}
	
	private void handleMessage(Message message) throws IOException, EncodeException {
		if(message.isGroupChat()) {
			handleGroupChatMessage(message);
		} else {
			handlePersonalMessage(message);
		}
	}
	
	private void handlePersonalMessage(Message message) throws IOException, EncodeException {
		persistMessage(message);
		sendMessageToOneUser(message);		
	}
	
	private void removeSender(List<UserStatusDto> dtoList, String sender) {
		UserStatusDto searchDto = new UserStatusDto(sender);
		int index = 0;
		for(index = 0 ; index < dtoList.size(); ++index) {
			if(dtoList.get(index).getUser().equals(sender)) {
				break;				
			}
		}
		dtoList.remove(index);
	}
	
	private void handleGroupChatMessage(Message message) throws IOException, EncodeException {
		try {
			Long groupChatId = Long.parseLong(message.getId());
			List<UserStatusDto> userList = groupChatRepository.findGroupChatById(groupChatId).getMembers();
			persistGroupChatMessage(groupChatId, message.getFrom(), message.getContent(), userList);
			System.out.println("UserStatusDtoList : " + userList);
			removeSender(userList, message.getFrom());
			for(UserStatusDto dto : userList) {
				Message m = new Message();
				m.setTo(dto.getUser());
				m.setFrom(message.getFrom());
				m.setContent(message.getContent());
				m.setId(String.valueOf(groupChatId));
				m.setGroupChat(true);
				sendMessageToOneUser(m);
			}
		} catch (SQLException e) {
			System.out.print("Unable to persist message");
			e.printStackTrace();
		}				
	}

	private static void persistGroupChatMessage(Long groupChatId, String fromUser, String content, List<UserStatusDto> userList) {
		try {
			messageRespoitory.create(fromUser, content, groupChatId);
		} catch (SQLException e) {
			System.out.print("Unable to persist group chat message");
			e.printStackTrace();
		}		
	}
	
	private static void persistMessage(Message message) {
		try {
			messageRespoitory.create(message.getFrom(), message.getTo(), message.getContent());
		} catch (SQLException e) {
			System.out.print("Unable to persist message");
			e.printStackTrace();
		}		
	}

	@OnClose
	public void onClose(Session session) throws IOException, EncodeException {
		log.info(session.getId() + " disconnected!");

		chatEndpoints.remove(this);
		broadcastRefreshContact();		
	}

	@OnError
	public void onError(Session session, Throwable throwable) throws IOException, EncodeException {
		broadcastRefreshContact();
		throwable.printStackTrace();
		log.warning(throwable.toString());
	}

	private static void broadcastRefreshContact() throws IOException, EncodeException {
		Message message = new Message();
		message.setContent(Constants.REFRESH_CONTACT);
		broadcast(message);		
	}
	
	private static void broadcast(Message message) throws IOException, EncodeException {
		for (ChatEndPoint endpoint : chatEndpoints) {
			synchronized (endpoint) {
				endpoint.session.getBasicRemote().sendObject(message);
			}
		}
	}

	private static void sendMessageToOneUser(Message message) throws IOException, EncodeException {
		System.out.println("Active Endpoint" + chatEndpoints);		
		for (ChatEndPoint endpoint : chatEndpoints) {
			synchronized (endpoint) {
				if (endpoint.session.getId().equals(getSessionId(message.getTo()))) {
					System.out.println(" Sending to : " + message.getTo() + " Content : " + message.getContent());
					endpoint.session.getBasicRemote().sendObject(message);
				}
			}
		}
	}

	private static String getSessionId(String user) {
		for(ChatEndPoint endpoint : chatEndpoints) {
			if(endpoint.username.equals(user)) {
				return endpoint.session.getId();
			}
		}
		return null;
	}

	private String getUser(String sessionId) {		
		for(ChatEndPoint endpoint : chatEndpoints) {
			if(endpoint.session.getId().equals(sessionId)) {
				return endpoint.username;
			}
		}
		return null;
	}

	public static Set<String> getOnlineUsers() {
		Set<String> users = new TreeSet<String>();
		for(ChatEndPoint enpoint : chatEndpoints) {
			users.add(enpoint.username);
		}
		return users;
	}
	
	@Override
	public String toString() {
		return "ChatEndPoint [session=" + session + ", username=" + username
				+ "]";
	}
		
}
