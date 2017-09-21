package com.chat.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class Util {
	
	public static boolean isNull(Object object) {
		if(object != null) {
			if((object instanceof String) && "".equals(object)) {
				return true;
			} else if((object instanceof Object[]) && ((Object[]) object).length <= 0 ) {
				return true;
			} else if((object instanceof Collection) && !((Collection<?>) object).isEmpty()) {
				return true;
			}
			return false;
		}
		return true;
	}
	
	public static String formatDateTime(LocalDateTime time) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");	
		return time.format(formatter);		
	}

}
