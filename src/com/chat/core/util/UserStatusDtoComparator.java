package com.chat.core.util;

import java.util.Comparator;

import com.chat.dto.UserStatusDto;

public class UserStatusDtoComparator implements Comparator<UserStatusDto> {

	@Override
	public int compare(UserStatusDto o1, UserStatusDto o2) {
		return o1.getUser().compareTo(o2.getUser());
	}
}