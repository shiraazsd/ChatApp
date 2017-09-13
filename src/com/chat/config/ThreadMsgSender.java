package com.chat.config;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.Session;

public class ThreadMsgSender extends Thread{

	public ThreadMsgSender() {
	  }
	  SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy  HH:mm:ss ");

	  @Override
	  public void run() {
	    System.out.println("RUN called");
	    while (true) {
	      sendMessage(formatter.format(Calendar.getInstance().getTime()));
	      try {
	        sleep(5000);
	      } catch (InterruptedException ex) {
	        ex.printStackTrace();
	        Logger.getLogger(ThreadMsgSender.class.getName()).log(Level.SEVERE, null, ex);
	      }

	    }
	  }

	  public static void sendMessage(String message) {
	    List<Session> list = SocketConfig.getSessions();
	    System.out.println("Notification list size: " + list.size());
	    for (Session s : list) {
	      if (s.isOpen()) {
	        System.out.println("Sending Notification To: " + s.getId());
	        s.getAsyncRemote().sendText(message);
	      }
	    }
	  }
	
}
