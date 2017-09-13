package com.chat.bean;

import java.io.Serializable;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.chat.config.SocketConfig;
import com.chat.config.ThreadMsgSender;
import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.Group;
import com.chat.core.domain.User;
import com.chat.core.repository.GroupRepository;
import com.chat.core.repository.UserRepository;
import com.chat.core.repository.impl.GroupRepositoryImpl;
import com.chat.core.repository.impl.UserRepositoryImpl;
import com.chat.core.util.Constants;

@ManagedBean
@SessionScoped
public class NotificationManagedBean implements Serializable{
	
	private static final long serialVersionUID = 6490167334788739820L;
	
	private UserRepository userRespository = UserRepositoryImpl.getInstance();
	
	private GroupRepository groupRepository = GroupRepositoryImpl.getInstance();

	/**
	   * Creates a new instance of NotificationManagedBean
	   */
	  public NotificationManagedBean() {
		  System.out.print("Created !");
	  }
	  
	  String message;
	  
	  public void sendNotification() throws SQLException  {
		 List<User> users = userRespository.getUserByStatus(Constants.ACTIVE);
		 
		 User human = userRespository.findById(1l);
		 
		 System.out.println("human->"+human.getEmail());
		 
		 Group groupHuman = groupRepository.findById(1l);
		 
		 System.out.println(groupHuman.getGroupName());
		 
		 List<Group> groups = groupRepository.findAll();
		 groups.forEach(group -> {
			 System.out.println(group.getGroupName());
		 });
		 
		 users.forEach(user -> {
			 System.out.println("user ->" +user.getEmail());
		 });
		 
	    System.out.println("Sending Message: " + message);
	    ThreadMsgSender.sendMessage(message);
	  }
	  
	  public List getNotificationReceiverList() {
	    return SocketConfig.getSessions();
	  }

	  public String getMessage() {
		  System.out.print("This is called!");
	    return message;
	  }

	  public void setMessage(String message) {
	    this.message = message;
	  }
	  

}
