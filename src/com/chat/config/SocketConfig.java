package com.chat.config;

import java.util.ArrayList;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Config of end point of the sockets
 * 
 * @author Mario Silva
 *
 */
@ServerEndpoint(value = "/notification")
public class SocketConfig {
	
	/**
	   * Message receiver method
	   *
	   * @param message
	   * @return
	   */
	  static ArrayList<Session> sessions = new ArrayList<>();

	  /* uncomment this section to have a thread send messages to all endpoints
	   static ThreadMsgSender sender = new ThreadMsgSender();
	   static  {
	   sender.start();
	   }
	   */
	  @OnMessage
	  public void messageReceiver(String message) {
	    System.out.println("Received message:" + message);
	  }

	  @OnOpen
	  public void onOpen(Session session) {
	    System.out.println("onOpen: " + session.getId());
	    sessions.add(session);
	    System.out.println("onOpen: Notification list size: " + sessions.size());
	  }

	  @OnClose
	  public void onClose(Session session) {
	    System.out.println("onClose: " + session.getId());
	    sessions.remove(session);
	  }

	  public static ArrayList<Session> getSessions() {
	    return sessions;
	  }

	  public static void setSessions(ArrayList<Session> sessions) {
		  SocketConfig.sessions = sessions;
	  }

}
