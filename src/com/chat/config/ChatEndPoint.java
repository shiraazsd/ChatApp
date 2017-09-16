package com.chat.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.chat.core.dao.ex.SQLException;
import com.chat.core.repository.MessageRepository;
import com.chat.core.repository.impl.MessageRepositoryImpl;

@ServerEndpoint(value = "/chat/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndPoint {

	private final Logger log = Logger.getLogger(getClass().getName());

	private static MessageRepository messageRespoitory = MessageRepositoryImpl.getInstance();
	
	private Session session;
	private String username;
	private static final Set<ChatEndPoint> chatEndpoints = new CopyOnWriteArraySet<>();
	private static HashMap<String, String> users = new HashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
		log.info(session.getId() + " connected!");
		this.session = session;
		this.username = username;
		chatEndpoints.add(this);
		users.put(session.getId(), username);

		Message message = new Message();
		message.setFrom(username);
		message.setContent("connected!");
		broadcast(message);

		System.out.println("Active endpoints count : " +  chatEndpoints.size());
		System.out.println("Active endpoints : " +  chatEndpoints);		
	}

	@OnMessage
	public void onMessage(Session session, Message message) throws IOException, EncodeException {
		log.info(message.toString());
		System.out.println("onMessage : " + message.toString());
		message.setFrom(users.get(session.getId()));
		sendMessageToOneUser(message);
		persistMessage(message);
	}
	
	private static void persistMessage(Message message) {
		try {
			messageRespoitory.create(message.getFrom(), message.getTo(), message.getContent());
		} catch (SQLException e) {
			System.out.print("Unable to retrieve message");
			e.printStackTrace();
		}		
	}

	@OnClose
	public void onClose(Session session) throws IOException, EncodeException {
		log.info(session.getId() + " disconnected!");

		chatEndpoints.remove(this);
		Message message = new Message();
		message.setFrom(users.get(session.getId()));
		message.setContent("disconnected!");
		broadcast(message);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		log.warning(throwable.toString());
	}

	private static void broadcast(Message message) throws IOException, EncodeException {
//		for (ChatEndPoint endpoint : chatEndpoints) {
//			synchronized (endpoint) {
//				endpoint.session.getBasicRemote().sendObject(message);
//			}
//		}
	}

	private static void sendMessageToOneUser(Message message) throws IOException, EncodeException {
		for (ChatEndPoint endpoint : chatEndpoints) {
			synchronized (endpoint) {
				if (endpoint.session.getId().equals(getSessionId(message.getTo()))) {
					endpoint.session.getBasicRemote().sendObject(message);
				}
			}
		}
	}

	private static String getSessionId(String to) {
		if (users.containsValue(to)) {
			for (String sessionId : users.keySet()) {
				if (users.get(sessionId).equals(to)) {
					return sessionId;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "ChatEndPoint [session=" + session + ", username=" + username
				+ "]";
	}
	
}
