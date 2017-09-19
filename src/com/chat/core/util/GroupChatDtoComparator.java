package com.chat.core.util;

import java.util.Comparator;

import com.chat.dto.GroupChatDto;

public class GroupChatDtoComparator implements Comparator<GroupChatDto> {

	@Override
	public int compare(GroupChatDto o1, GroupChatDto o2) {
		return o1.getName().compareTo(o2.getName());
	}
}