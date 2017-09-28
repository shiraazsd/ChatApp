package com.chat.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.chat.config.ChatEndPoint;
import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.GroupChat;
import com.chat.core.domain.Message;
import com.chat.core.domain.User;
import com.chat.core.repository.GroupChatRepository;
import com.chat.core.repository.MessageRepository;
import com.chat.core.repository.UserRepository;
import com.chat.core.repository.impl.GroupChatRepositoryImpl;
import com.chat.core.repository.impl.GroupRepositoryImpl;
import com.chat.core.repository.impl.MessageRepositoryImpl;
import com.chat.core.repository.impl.UserRepositoryImpl;
import com.chat.core.util.Constants;
import com.chat.core.util.GroupChatDtoComparator;
import com.chat.core.util.UserStatusDtoComparator;
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
	public ChatMessageResponseDto getMessageFromToUser(@QueryParam("from") final String from, @QueryParam("to") final String to, @DefaultValue("-1") @QueryParam("limit") final int limit) { {
		ChatMessageResponseDto response = new ChatMessageResponseDto();
		List<MessageDto> result = null;
		try {
			List<Message> messageList = messageRepository.getLastFewMessages(from, to, limit);
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
	@Path("/markGroupChatMessagesAsRead")
	@Produces(MediaType.APPLICATION_JSON)
	public int markGroupChatMessagesAsRead(@QueryParam("user") final String user, @QueryParam("chatId") final Long groupChatId) {
		try {
			int count = messageRepository.markAllMessagesAsReadInGroupChat(user, groupChatId);
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
			Long groupChatId = groupChatRepository.findGroupChatByName(chatName).getId();
			groupChatRepository.addMemberToGroupChat(groupChatId, loggedInUser);
			return getDtoUtil().convertIntoDto(groupChatRepository.findGroupChatById(groupChatId));
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/updateGroupChat")
	@Produces(MediaType.APPLICATION_JSON)
	public GroupChatDto updateGroupChat(@QueryParam("chatId") final Long groupChatId, @QueryParam("chatNameNew") final String chatNameNew, @QueryParam("loggedInUser") final String loggedInUser) {
		try {
			String chatName = groupChatRepository.update(groupChatId, chatNameNew);
			return getDtoUtil().convertIntoDto(groupChatRepository.findGroupChatById(groupChatId));
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/addMemberToGroupChat")
	@Produces(MediaType.APPLICATION_JSON)
	public GroupChatDto addMemberToGroupChat(@QueryParam("chatId") final Long groupChatId,@QueryParam("user") final String user) {
		try {
			groupChatRepository.addMemberToGroupChat(groupChatId, user);
			return getDtoUtil().convertIntoDto(groupChatRepository.findGroupChatById(groupChatId));
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/removeMemberFromGroupChat")
	@Produces(MediaType.APPLICATION_JSON)
	public GroupChatDto removeMemberFromGroupChat(@QueryParam("chatId") final Long groupChatId,@QueryParam("user") final String user) {
		try {
			groupChatRepository.removeMemberFromGroupChat(groupChatId, user);
			return getDtoUtil().convertIntoDto(groupChatRepository.findGroupChatById(groupChatId));
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/getGroupChat")
	@Produces(MediaType.APPLICATION_JSON)
	public GroupChatDto getGroupChat(@QueryParam("chatId") final Long groupChatId) {
		try {
			return getDtoUtil().convertIntoDto(groupChatRepository.findGroupChatById(groupChatId));
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/clearGroupChatHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public Long clearGroupChatHistory(@QueryParam("chatId") final Long groupChatId, @QueryParam("loggedInUser") final String loggedInUser) {
		try {
			List<Message> messageList = messageRepository.getLastFewMessagesForGroupChat(loggedInUser, groupChatId, -1);
			List<Long> idList = new ArrayList<Long>();
			for(Message message : messageList) {
				idList.add(message.getId());
			}
			messageRepository.deleteMessages(idList);
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return groupChatId;
	}

	@GET
	@Path("/clearPersonalChatHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public String clearPersonalChatHistory(@QueryParam("loggedInUser") final String loggedInUser, @QueryParam("toUser") final String toUser) {
		try {
			List<Message> messageList = messageRepository.getLastFewMessages(loggedInUser, toUser, -1);
			List<Long> idList = new ArrayList<Long>();
			for(Message message : messageList) {
				idList.add(message.getId());
			}
			messageRepository.deleteMessages(idList);
		} catch (SQLException e) {
			System.out.println("Unable to update message status to Read");
			e.printStackTrace();
		}
		return toUser;
	}
	
	@GET
	@Path("/leaveGroupChat")
	@Produces(MediaType.APPLICATION_JSON)
	public void leaveGroupChat(@QueryParam("chatId") final Long groupChatId, @QueryParam("loggedInUser") final String loggedInUser) {
		try {
			clearGroupChatHistory(groupChatId, loggedInUser);
			groupChatRepository.removeMemberFromGroupChat(groupChatId, loggedInUser);
		} catch (SQLException e) {
			System.out.println("Unable to leave group chat");
			e.printStackTrace();
		}
	}
	
	@GET
	@Path("/getAvailableUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserStatusDto> getAvailableUsers(@QueryParam("chatId") final Long groupChatId) {
		List<UserStatusDto> allUserList = new ArrayList<UserStatusDto>();
		allUserList.addAll(getAllUserList(null));
		List<UserStatusDto> chatGroupUserList = getGroupChat(groupChatId).getMembers();
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
			Set<GroupChatDto> groupChats = new TreeSet<GroupChatDto>(new GroupChatDtoComparator());

			for(GroupChat groupChat : groupChatList) {
				groupChats.add(getDtoUtil().convertIntoDto(groupChat));
			}
			populateGroupChatNotificationCount(groupChats, loggedInUser);
			return groupChats;						
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}

	@GET
	@Path("/groupChatMessages")
	@Produces(MediaType.APPLICATION_JSON)
	public ChatMessageResponseDto getMessageFromToGroupChat(@QueryParam("user") final String user, @QueryParam("chatId") final Long groupChatId, @DefaultValue("-1") @QueryParam("limit") final int limit) { 
		ChatMessageResponseDto response = new ChatMessageResponseDto();
		List<MessageDto> result = null;
		try {
			List<Message> messageList = messageRepository.getLastFewMessagesForGroupChat(user, groupChatId, limit);
			result = getDtoUtil().convertIntoDto(messageList);
			response.setFrom(String.valueOf(groupChatId));
			response.setTo(user);
			response.setMessageList(result);
			response.setGroupChatName(groupChatRepository.findGroupChatById(groupChatId).getName());
		} catch (SQLException e) {
			System.out.print("Unable to retrieve from:to messages");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	@GET
	@Path("/profilePic/{user}")
	@Produces({"image/png", "images/jpg"})	
	public Response getUserProfilePic(@PathParam("user") String user) {
		try {
			byte[] bytes = userRepository.getUserProfilePicAsByte(user);
			if(bytes == null) {
				bytes = userRepository.getDefaultUserProfilePicAsByte();
			}
			return Response.ok(new ByteArrayInputStream(bytes)).build();
		} catch (SQLException | IOException e) {
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
		Set<UserStatusDto> users = new TreeSet<UserStatusDto>(new UserStatusDtoComparator());
		Set<String> onlineUsers = ChatEndPoint.getOnlineUsers();
		for(User user : userList) {
			if(user.getEmail().equals(loggedInUser)) {
				continue;
			}
			
			if(onlineUsers.contains(user.getEmail())) {
				users.add(new UserStatusDto(user.getId(), user.getEmail(), Constants.ONLINE));
			} else {
				users.add(new UserStatusDto(user.getId(), user.getEmail(), Constants.OFFLINE));
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

	private void populateGroupChatNotificationCount(Set<GroupChatDto> groupChats, String loggedInUser) {		
		try {
			Map<Long, Integer> userCountMap = messageRepository.getUnreadMessageCountForGroupChat(loggedInUser);
			for(GroupChatDto dto : groupChats) {
				if(userCountMap.containsKey(dto.getId())) {
					dto.setNotification(userCountMap.get(dto.getId()));
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
	
	private GroupChatDto getActiveGroupChat(Long groupChatId, String loggedInUser) {
		try {
			GroupChat groupChat = groupChatRepository.findGroupChatById(groupChatId);
			GroupChatDto dto = getDtoUtil().convertIntoDto(groupChat);
			return dto;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}