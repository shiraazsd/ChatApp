package com.chat.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.User;
import com.chat.core.repository.GroupRepository;
import com.chat.core.repository.UserRepository;
import com.chat.core.repository.impl.GroupRepositoryImpl;
import com.chat.core.repository.impl.UserRepositoryImpl;

@ManagedBean
@SessionScoped
public class UserBean implements Serializable{
	
	private static final long serialVersionUID = 6490167334788739820L;
	
	private UserRepository userRespository = UserRepositoryImpl.getInstance();
	
	private GroupRepository groupRepository = GroupRepositoryImpl.getInstance();

	private List<User> users;

	@PostConstruct
	public void init() {
	    try {
			users = userRespository.findAll();
		} catch (SQLException e) {
			System.out.println("Unable to fetch user list");
			e.printStackTrace();
		}
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	   * Creates a new instance of NotificationManagedBean
	   */
	  public UserBean() {
		  System.out.print("UserBean Created !");
	  }


	public void userClicked(ValueChangeEvent e) {
		System.out.print("Changed !");			
	}		

	  public void getAllUsers() {
		  try {
			List<User> userList = userRespository.findAll();
			System.out.print(userList);
		} catch (SQLException e) {
			System.out.println("Unable to fetch user list");
			e.printStackTrace();
		}
	  }
}
