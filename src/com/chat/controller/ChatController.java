package com.chat.controller;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

public class ChatController {
	
	@OnMessage
	  public void messageReceiver(String message) {
	    System.out.println("Received message:" + message);
	  }

	  @OnOpen
	  public void onOpen(Session session) {
	    System.out.println("onOpen: " + session.getId());
	    //sessions.add(session);
	    System.out.println("onOpen: Notification list size: ");
	  }

}
