package com.chat.core.util;

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

}
