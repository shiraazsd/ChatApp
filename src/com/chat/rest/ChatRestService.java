package com.chat.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.chat.config.ChatEndPoint;
import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.GroupChat;
import com.chat.core.domain.Message;
import com.chat.core.domain.User;
import com.chat.core.repository.GroupChatRepository;
import com.chat.core.repository.GroupRepository;
import com.chat.core.repository.MessageRepository;
import com.chat.core.repository.UserRepository;
import com.chat.core.repository.impl.GroupChatRepositoryImpl;
import com.chat.core.repository.impl.GroupRepositoryImpl;
import com.chat.core.repository.impl.MessageRepositoryImpl;
import com.chat.core.repository.impl.UserRepositoryImpl;
import com.chat.core.util.Constants;
import com.chat.dto.ChatMessageResponseDto;
import com.chat.dto.GroupChatDto;
import com.chat.dto.MessageDto;
import com.chat.dto.UserStatusDto;

@Path("/chat")
public class ChatRestService {
		
	private static MessageRepository messageRepository = MessageRepositoryImpl.getInstance();
  
	private static UserRepository userRepository = UserRepositoryImpl.getInstance();

	private static GroupChatRepository groupChatRepository = GroupChatRepositoryImpl.getInstance();
	
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
	@Path("/getUsersList")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<UserStatusDto> getOnlineUsers(@QueryParam("loggedInUser") final String loggedInUser, @QueryParam("type") final String type) {
		Set<UserStatusDto> result = null;
		if(Constants.ALL_USERS.equals(type)) {
			result = getAllUserList(loggedInUser);
		} else if(Constants.ONLINE_USERS.equals(type)) {
			result = getOnlineUserList(loggedInUser);
		} else {
			result = getRecentUserList(loggedInUser);
		}
		populateNotificationCount(result, loggedInUser);
		return result;
	}
	
	@GET
	@Path("/markMessagesAsRead")
	@Produces(MediaType.APPLICATION_JSON)
	public int markMessagesAsRead(@QueryParam("from") final String from, @QueryParam("to") final String to) {
		try {
			int count = messageRepository.markAllMessagesAsRead(to, from);
			System.out.print("Marked messages as read : " + count);
			return count;
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return -1;
	}
	
	@GET
	@Path("/createNewGroupChat")
	@Produces(MediaType.APPLICATION_JSON)
	public GroupChatDto createNewGroupChat(@QueryParam("loggedInUser") final String loggedInUser) {
		try {
			String chatName = groupChatRepository.create();
			Long chatId = groupChatRepository.findGroupChatByName(chatName).getId();
			groupChatRepository.addMemberToGroupChat(chatId, loggedInUser);
			return getDtoUtil().convertIntoDto(groupChatRepository.findGroupChatById(chatId));
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/updateGroupChat")
	@Produces(MediaType.APPLICATION_JSON)
	public GroupChatDto updateGroupChat(@QueryParam("chatId") final Long chatId, @QueryParam("chatNameNew") final String chatNameNew, @QueryParam("loggedInUser") final String loggedInUser) {
		try {
			String chatName = groupChatRepository.update(chatId, chatNameNew);
			return getDtoUtil().convertIntoDto(groupChatRepository.findGroupChatById(chatId));
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/addMemberToGroupChat")
	@Produces(MediaType.APPLICATION_JSON)
	public GroupChatDto addMemberToGroupChat(@QueryParam("chatId") final Long chatId,@QueryParam("user") final String user) {
		try {
			groupChatRepository.addMemberToGroupChat(chatId, user);
			return getDtoUtil().convertIntoDto(groupChatRepository.findGroupChatById(chatId));
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/removeMemberFromGroupChat")
	@Produces(MediaType.APPLICATION_JSON)
	public GroupChatDto removeMemberFromGroupChat(@QueryParam("chatId") final Long chatId,@QueryParam("user") final String user) {
		try {
			groupChatRepository.removeMemberFromGroupChat(chatId, user);
			return getDtoUtil().convertIntoDto(groupChatRepository.findGroupChatById(chatId));
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/getGroupChat")
	@Produces(MediaType.APPLICATION_JSON)
	public GroupChatDto getGroupChat(@QueryParam("chatId") final Long chatId) {
		try {
			return getDtoUtil().convertIntoDto(groupChatRepository.findGroupChatById(chatId));
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/getAvailableUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserStatusDto> getAvailableUsers(@QueryParam("chatId") final Long chatId) {
		List<UserStatusDto> allUserList = new ArrayList<UserStatusDto>();
		allUserList.addAll(getAllUserList(null));
		List<UserStatusDto> chatGroupUserList = getGroupChat(chatId).getMembers();
		List<UserStatusDto> availableUserList = new ArrayList<UserStatusDto>();
		boolean flag = false;
		for(UserStatusDto dto1 : allUserList) {
			for(UserStatusDto dto2 : chatGroupUserList) {
				if(dto1.getUser().equals(dto2.getUser())) {
					flag = true;
					break;
				}					
			}
			if(!flag) {
				availableUserList.add(dto1);
			}
			flag = false;
		}						
		return availableUserList;		
	}

	@GET
	@Path("/getUserGroupChat")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<GroupChatDto> getUserGroupChat(@QueryParam("loggedInUser") final String loggedInUser) {
		try {
			List<GroupChat> groupChatList = groupChatRepository.getGroupChatsForUser(loggedInUser);
			Set<GroupChatDto> groupChats = new TreeSet<GroupChatDto>(new Comparator<GroupChatDto>() {

				@Override
				public int compare(GroupChatDto o1, GroupChatDto o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

			for(GroupChat groupChat : groupChatList) {
				groupChats.add(getDtoUtil().convertIntoDto(groupChat));
			}
			return groupChats;						
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}
		
	private Set<UserStatusDto> getAllUserList(String loggedInUser) {
		List<User> userList = null;
		try {
			userList = userRepository.findAll();
			return getUserStatusDtoList(userList, loggedInUser);
		} catch (SQLException e) {
			System.out.println("Unable to fetch all users");
			e.printStackTrace();
		}
		return null;
	}
	
	private Set<UserStatusDto> getOnlineUserList(String loggedInUser) {
		List<User> userList = null;
		try {
			List<String> onlineUserList = new ArrayList<String>();
			onlineUserList.addAll(ChatEndPoint.getOnlineUsers());
			userList = userRepository.getUserListByEmail(onlineUserList);
			return getUserStatusDtoList(userList, loggedInUser);
		} catch (SQLException e) {
			System.out.println("Unable to fetch all users");
			e.printStackTrace();
		}
		return null;
	}

	private Set<UserStatusDto> getRecentUserList(String loggedInUser) {
		List<User> userList = null;
		try {
			userList = userRepository.getRecentUserList(loggedInUser);
			return getUserStatusDtoList(userList, loggedInUser);
		} catch (SQLException e) {
			System.out.println("Unable to fetch all users");
			e.printStackTrace();
		}
		return null;
	}	
	
	private Set<UserStatusDto> getUserStatusDtoList(List<User> userList, String loggedInUser) {
		System.out.print("UserList : " + userList);
		Set<UserStatusDto> users = new TreeSet<UserStatusDto>(new Comparator<UserStatusDto>() {

			@Override
			public int compare(UserStatusDto o1, UserStatusDto o2) {
				return o1.getUser().compareTo(o2.getUser());
			}
		});
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
	

	private void populateNotificationCount(Set<UserStatusDto> users, String loggedInUser) {		
		try {
			Map<String, Integer> userCountMap = messageRepository.getUnreadMessageCount(loggedInUser);
			for(UserStatusDto dto : users) {
				if(userCountMap.containsKey(dto.getUser())) {
					dto.setNotification(userCountMap.get(dto.getUser()));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private DtoUtil getDtoUtil() {
		return DtoUtil.getInstance();
	}
	
	private GroupChatDto getActiveGroupChat(Long chatId, String loggedInUser) {
		try {
			GroupChat groupChat = groupChatRepository.findGroupChatById(chatId);
			GroupChatDto dto = getDtoUtil().convertIntoDto(groupChat);
			return dto;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}