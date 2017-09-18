package com.chat.rest;

import java.util.ArrayList;
import java.util.List;

import com.chat.core.domain.GroupChat;
import com.chat.core.domain.Message;
import com.chat.dto.GroupChatDto;
import com.chat.dto.MessageDto;

public class DtoUtil {
	
	private static DtoUtil dtoUtil = null;
	
	private DtoUtil() {
				
	}
	
	public static DtoUtil getInstance() {
		if(dtoUtil == null) {
			dtoUtil = new DtoUtil();
		}
		return dtoUtil;
	}
	
	public MessageDto getTestMessageDto() {
		MessageDto dto = new MessageDto();
		dto.setContent("This is test message");
		dto.setFrom("from@gmail.com");
		dto.setTo("to@gmail.com");
		return dto;
	}
	
	public List<MessageDto> convertIntoDto(List<Message> messageList) {
		List<MessageDto> dtoList = new ArrayList<MessageDto>();
		for(Message message : messageList) {
			dtoList.add(0, new MessageDto(message.getUserFrom().getEmail(), message.getUserTo().getEmail(), message.getDescriptionMessage()));
		}
		return dtoList;
	}
	
	public GroupChatDto convertIntoDto(GroupChat groupChat) {
		GroupChatDto dto = new GroupChatDto();
		dto.setId(groupChat.getId());
		dto.setName(groupChat.getName());
		dto.setMembers(groupChat.getMembers());
		return dto;
	}
}
