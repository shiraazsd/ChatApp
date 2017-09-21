$(document).ready(function() {
	chatsocket.initEvent();
	chatsocket.initAction();
	initializeSessionStorage();
	createOpenChatUsersChatBox();
	loadUserContactList();	
});

var OPEN_CHAT_USERS = "openChatUsers";

var getElementIdSuffix = function(value) {
	if(typeof value == 'string') {
	  value = value.replace("@", '').replace('.', '');
	}
	return value;
};

var getChatBoxLoaderId = function(idSuffix) {
	var id = "chat_box_loader_"+idSuffix;
	return id;
}

var getMsgPanelId = function(idSuffix) {
	var id = "msg_panel_"+idSuffix;
	return id;
}

var getBtnInputId = function(idSuffix) {
	var id = "btn_input_"+idSuffix;
	return id;
}

var getChatWindowId = function(idSuffix) {
	var id = "chat_window_"+idSuffix;
	return id;
}

var getMinimId = function(idSuffix) {
	var id = "minim_"+idSuffix;
	return id;
}

var getUserListEntryId = function(idSuffix) {
	var id= "user_list_entry_"+idSuffix;
	return id;
}

var getListUserStatusId = function(idSuffix) {
	var id = "list_user_status_id_"+idSuffix;
	return id;
}

var getListUserNotificationId = function(idSuffix) {
	var id = "list_user_notification_id_"+idSuffix;
	return id;	
}

var getChatUserStatusId = function(idSuffix) {
	var id = "chat_user_status_id_"+idSuffix;
	return id;
}

var getChatUserNotificationId = function(idSuffix) {
	var id = "chat_user_notification_id_"+idSuffix;
	return id;	
}

$('#usersDataTable td').click( function() {
	$('#to').val(($( this ).text()));
	var selectedUserEmail = $( this ).text();
	createNewChatBox(selectedUserEmail);
})

var reArrangeChatBox = function() {
	var marginOffset = 320;
	$("#chatBoxContainer").children().each(function(index,value){
		var margin = marginOffset*index;
		$(value).css('margin-left', margin);
	});	
}

var closeFirstChatBox = function() {
	var firstChild = $("#chatBoxContainer").children()[0];
	$(firstChild).find('.icon_close').click();
}


var createNewChatBox = function(selectedUserEmail) {
	var template = $("#chat-window-template").html();
	var idSuffix = getElementIdSuffix(selectedUserEmail);
	var maxChat = 4;
	var chatBoxCount = $("#chatBoxContainer").children().length;
	
	if ($('#chatBoxContainer').find('#'+getChatWindowId(idSuffix)).length) {
		return false;
	}	
	if(chatBoxCount >= maxChat) {
		closeFirstChatBox();		
	}				
	
	var data = { chat_window_id : getChatWindowId(idSuffix), chat_user_email : selectedUserEmail, minim_id : getMinimId(idSuffix), msg_panel_id : getMsgPanelId(idSuffix), btn_input_id : getBtnInputId(idSuffix), loader_id : getChatBoxLoaderId(idSuffix), chat_user_status_id : getChatUserStatusId(idSuffix), chat_user_notification_id : getChatUserNotificationId(idSuffix)};	
	
	var html = Mustache.render(template, data);		
	
	$('#chatBoxContainer').append(html);
	reArrangeChatBox();
	setUserStatusIcon(getChatUserStatusId(idSuffix), getCurrentUserListStatus(selectedUserEmail));
	displayUserNotificationCount(getChatUserNotificationId(idSuffix), getCurrentUserListNotification(selectedUserEmail));	
	populateChatHistory(selectedUserEmail);
	chatsocket.initAction();
	addToOpenChatUsers({id : selectedUserEmail, type : 'user'});
	return true;
};	

var populateChatHistory = function(toUser) {
	var fromUser = getLoggedInUser();
	var chatMessageHistoryApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/chatMessages?from=" + fromUser + "&to=" + toUser; 
	$.getJSON(chatMessageHistoryApiUrl,
			   function(data) {
				 var toUser = data.to;
				 var fromUser = data.from;
				 messageList = data.messageList;
				 var idSuffix = getElementIdSuffix(toUser);
				 hideLoader(getChatBoxLoaderId(idSuffix));
				 if(messageList.length == 0)
					 return;
			   	 for(var i = 0; i < messageList.length; ++i) {
			   		 if(messageList[i].from == fromUser) {
			   			appendSendMessageToChat(idSuffix, messageList[i].content, messageList[i].messageTime);
			   		 } else{
			   			appendReceiveMessageToChat(idSuffix, messageList[i].content, messageList[i].messageTime);			   			 
			   		 }
			   	 }
			 	scrollToBottom(toUser);
		 	   });		
};


var populateGroupChatHistory = function(chatId) {
	var loggedInUser = getLoggedInUser();
	var groupChatMessageHistoryApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/groupChatMessages?user=" + loggedInUser + "&chatId=" + chatId; 
	$.getJSON(groupChatMessageHistoryApiUrl,
			   function(data) {
				 var loggedInUser = getLoggedInUser();
				 var chatId = data.from;
				 messageList = data.messageList;
				 var idSuffix = getElementIdSuffix(chatId);
				 $("#"+getChatWindowId(idSuffix)).find('.chat_box_heading_text').text(data.groupChatName);
				 hideLoader(getChatBoxLoaderId(idSuffix));
				 if(messageList.length == 0)
					 return;
			   	 for(var i = 0; i < messageList.length; ++i) {
			   		 if(messageList[i].from == loggedInUser) {
			   			appendSendMessageToChat(idSuffix, messageList[i].content, messageList[i].messageTime);
			   		 } else{
			   			appendReceiveMessageToGroupChat(idSuffix, messageList[i].content, messageList[i].from, messageList[i].messageTime);			   			 
			   		 }
			   	 }
			 	scrollToBottom(chatId);
		 	   });		
};


var getLoggedInUser = function() {
	return user = $("#loggedInUser").text();
}
var appendReceiveMessageToChat = function(idSuffix, content, time) {
	var msg_panel_id = getMsgPanelId(idSuffix);			
	$("#"+msg_panel_id).append(messageReceive(content, time));	
};

var appendReceiveMessageToGroupChat = function(idSuffix, content, from, time) {
	var msg_panel_id = getMsgPanelId(idSuffix);			
	$("#"+msg_panel_id).append(groupChatMessageReceive(content, from, time));	
};


var appendSendMessageToChat = function(idSuffix, content, time) {
	var msg_panel_id = getMsgPanelId(idSuffix);			
	$("#"+msg_panel_id).append(messageSend(content, time));	
};

var messageSend = function(message, time) {
	var buildMessage = "<div class='row msg_container base_sent'>"
			+ "<div class='col-md-10 col-xs-10'> "
			+ "<div class='messages msg_sent'>" + "<p>" + message + "</p>"
			+ "<time>" + time + "</time>"
			+ "</div>" + "</div>" + "";
	var imgMessage = "<div  class='col-md-2 col-xs-2 avatar'>"
			+ "<img src='http://www.bitrebels.com/wp-content/uploads/2011/02/Original-Facebook-Geek-Profile-Avatar-1.jpg'class='img-responsive ' />"
			+ "</div>" + "</div>";
	return buildMessage + imgMessage;
};

var messageReceive = function(message, time) {
	var imgMessage = "<div class='row msg_container base_receive'>" +
			"<div  class='col-md-2 col-xs-2 avatar'>"
			+ "<img src='http://www.bitrebels.com/wp-content/uploads/2011/02/Original-Facebook-Geek-Profile-Avatar-1.jpg'class='img-responsive ' />"
			+ "</div>";
	
	var buildMessage = "<div class='col-md-10 col-xs-10'> "
		+ "<div class='messages msg_receive'>" + "<p>" + message + "</p>"
		+ "<time>" + time + "</time>"
		+ "</div>" + "</div>" + "</div>";
	return imgMessage + buildMessage;
};

var groupChatMessageReceive = function(message, from, time) {
	var imgMessage = "<div class='row msg_container base_receive'>" +
			"<div  class='col-md-2 col-xs-2 avatar'>"
			+ "<img src='http://www.bitrebels.com/wp-content/uploads/2011/02/Original-Facebook-Geek-Profile-Avatar-1.jpg'class='img-responsive ' />"
			+ "</div>";
	
	var buildMessage = "<div class='col-md-10 col-xs-10'> "
		+ "<div class='messages msg_receive'>" + "<sup>" + from + "</sup>" + "<p>" + message + "</p>"
		+ "<time>" + time + "</time>"
		+ "</div>" + "</div>" + "</div>";
	return imgMessage + buildMessage;
};

var hideLoader = function(loaderDivId, targetDivIdList) {
	$('#'+loaderDivId).css('display', 'none');	
};

var initializeSessionStorage = function() {
	if(sessionStorage.getItem(OPEN_CHAT_USERS) == null || getLoggedInUser() == "") {
		sessionStorage.setItem(OPEN_CHAT_USERS, JSON.stringify([]));
	}
};

var addToOpenChatUsers = function(data) {
	var dataList = JSON.parse(sessionStorage.getItem(OPEN_CHAT_USERS));
	for(var i = 0; i < dataList.length; ++i) {
		if(dataList[i].id == data.id && dataList[i].type == data.type) {
			found = true;
			return;
		}
	}
	dataList.push(data);
	sessionStorage.setItem(OPEN_CHAT_USERS, JSON.stringify(dataList));
};

var removeFromOpenChatUsers = function(user) {
	var userList = JSON.parse(sessionStorage.getItem(OPEN_CHAT_USERS));
	var index = 0;
	for(index = 0; index < userList.length; ++index){
		if(userList[index].id == user.id && userList[index].type == user.type) {
			break;
		}
	}
	if (index > -1) {
	    userList.splice(index, 1);
	}
	sessionStorage.setItem(OPEN_CHAT_USERS, JSON.stringify(userList));
};

var getOpenChatUsers = function(user) {
	return JSON.parse(sessionStorage.getItem(OPEN_CHAT_USERS));	
};

var createOpenChatUsersChatBox = function() {
	var dataList = getOpenChatUsers();
	for(var i = 0; i < dataList.length; ++i) {
		if(dataList[i].type == 'user') {
			createNewChatBox(dataList[i].id);
		} else {
			restoreGroupChatBox(dataList[i].id);			
		}
	}
};


var restoreGroupChatBox = function(chatId) {
	var getGroupChatApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/getGroupChat?chatId=" + chatId; 
	$.getJSON(getGroupChatApiUrl,
			   function(data) {
				createNewGroupChatBox(data.id, data.name);					
	});		
}

var loadUserContactList = function() {
	var loggedInUser = getLoggedInUser();
	var type = $('#userListCategory').attr("data-current-selection");
	if(type == '4') {
		fetchAndPopulateGroupChatContactList();
	} else {
		fetchAndPopulateUserContactList();		
	}
};

var fetchAndPopulateUserContactList = function() {
	var loggedInUser = getLoggedInUser();
	var type = $('#userListCategory').attr("data-current-selection");
	var userListApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/getUsersList?loggedInUser=" + loggedInUser + "&type=" + type; 
	$.getJSON(userListApiUrl,
			   function(data) {
					populateUserContactList(data);
		 	   });				
};

var fetchAndPopulateGroupChatContactList = function() {
	var loggedInUser = getLoggedInUser();
	var groupChatListApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/getUserGroupChat?loggedInUser=" + loggedInUser; 
	$.getJSON(groupChatListApiUrl,
			   function(data) {
					populateGroupChatContactList(data);
		 	   });				
};

var populateUserContactList = function(data) {
	$('#contactListContainer').empty();
	var template = $("#user-contact-entry-template").html();
	var img = "http://www.bitrebels.com/wp-content/uploads/2011/02/Original-Facebook-Geek-Profile-Avatar-1.jpg";
	for(var i = 0; i < data.length; ++i) {
		var idSuffix = getElementIdSuffix(data[i].user);
		var params = { user_list_id : getUserListEntryId(idSuffix), user_list_email : data[i].user, user_pic : img, list_user_status_id : getListUserStatusId(idSuffix), list_user_notification_id : getListUserNotificationId(idSuffix)};
		var html = Mustache.render(template, params);						
		$('#contactListContainer').append(html);
		setUserStatusIcon(getListUserStatusId(idSuffix), data[i].status);		
		setUserStatusIcon(getChatUserStatusId(idSuffix), data[i].status);		
		setUserNotificationCount(data[i].user, data[i].notification);	
	}
	chatsocket.initAction();	
};

var populateGroupChatContactList = function(data) {	
	var type = $('#userListCategory').attr("data-current-selection");
	if(type == '4') {
		$('#contactListContainer').empty();
		var template = $("#group-chat-contact-entry-template").html();
		var img = "https://cdn2.iconfinder.com/data/icons/people-groups/512/Group_Woman_2-512.png";
		for(var i = 0; i < data.length; ++i) {
			var idSuffix = getElementIdSuffix(data[i].id);
			var params = { chat_list_id : getUserListEntryId(idSuffix), chat_id : data[i].id, chat_name : data[i].name, group_pic : img, list_chat_notification_id : getListUserNotificationId(idSuffix)};
			var html = Mustache.render(template, params);						
			$('#contactListContainer').append(html);
			setUserNotificationCount(data[i].id, data[i].notification);	
		}
		chatsocket.initAction();	
	} else {
		for(var i = 0; i < data.length; ++i) {
			setUserNotificationCount(data[i].id, data[i].notification);	
		}		
	}
};


var getUserAvailableStatusIcon = function(status) {
	if(status == '1')
		return 'status_online_icon';
	else 
		return 'status_offline_icon';
};

var setUserStatusIcon = function(id, status) {
	var el = $("#"+id);
	if(status == '1') {
		el.removeClass('status_offline_icon');
		el.addClass('status_online_icon')
	} else {
		el.removeClass('status_online_icon');
		el.addClass('status_offline_icon')		
	}
};

var getCurrentUserListStatus = function(user) {
	var listId = getListUserStatusId(getElementIdSuffix(user));
	if($('#'+listId).hasClass('status_online_icon') == true) {
		return '1';
	} else {
		return '0';
	}	
};

var getCurrentUserListNotification = function(user) {
	var listId = getListUserNotificationId(getElementIdSuffix(user));
	return $('#'+listId).text();
};

var setUserNotificationCount = function(user, count) {
	var idSuffix = getElementIdSuffix(user);
	idList = getListUserNotificationId(idSuffix);
	idChat = getChatUserNotificationId(idSuffix);
	displayUserNotificationCount(idList, count);
	displayUserNotificationCount(idChat, count);
}

var displayUserNotificationCount = function(id, count) {
	$('#'+id).text(count);
	if(count == 0)
		$('#'+id).css('display', 'none');
	else
		$('#'+id).css('display', '');		
};

var markMessagesAsRead = function(fromUser) {
	var toUser = getLoggedInUser();
	var markReadMessagesApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/markMessagesAsRead?to=" + toUser + "&from=" + fromUser; 
	$.getJSON(markReadMessagesApiUrl,
			   function(data) {
					fetchAndPopulateUserContactList();
		});				
};

var markGroupChatMessagesAsRead = function(chatId) {
	var user = getLoggedInUser();
	var markGroupChatReadMessagesApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/markGroupChatMessagesAsRead?user=" + user + "&chatId=" + chatId; 
	$.getJSON(markGroupChatReadMessagesApiUrl,
			   function(data) {
				fetchAndPopulateGroupChatContactList();
		});				
};

var updateNotification = function(fromUser) {
	var id = getChatBoxLoaderId(getElementIdSuffix(fromUser));
	if($('#'+id).is(':focus')) {
    	markMessagesAsRead(to);
	} else {
		loadUserContactList();
		playNotification();		
	}
};

var playNotification = function() {
	var filename = 'notification';
	$("#sound").html('<audio autoplay="autoplay"><source src="' + filename + '.mp3" type="audio/mpeg" /><source src="' + filename + '.ogg" type="audio/ogg" /><embed hidden="true" autostart="true" loop="false" src="' + filename +'.mp3" /></audio>');
};

var scrollToBottom = function(to) {
	var panel = $('#'+getMsgPanelId(getElementIdSuffix(to)));
	panel.scrollTop(panel.prop("scrollHeight"));	
};
	
var createNewGroupChat = function() {
	var loggedInUser = getLoggedInUser();
	var createNewGroupChatApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/createNewGroupChat?loggedInUser=" + loggedInUser; 
	$.getJSON(createNewGroupChatApiUrl,
			   function(data) {
					createNewGroupChatBox(data.id, data.name);	
	});	
};

var updateGroupChat = function(chatId, chatNameNew) {
	var createNewGroupChatApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/updateGroupChat?chatId=" + chatId + "&chatNameNew=" + chatNameNew; 
	$.getJSON(createNewGroupChatApiUrl,
			   function(data) {
				loadUserContactList();			
	});	
};

var clearGroupChatHistory = function(chatId) {
	var loggedInUser = getLoggedInUser();
	var clearGroupChatHistoryApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/clearGroupChatHistory?chatId=" + chatId + "&loggedInUser=" + loggedInUser; 
	$.getJSON(clearGroupChatHistoryApiUrl,
			   function(data) {
				loadUserContactList();			
	});	
};

var leaveGroupChatHistory = function(chatId) {
	var loggedInUser = getLoggedInUser();
	var leaveGroupChatApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/leaveGroupChat?chatId=" + chatId + "&loggedInUser=" + loggedInUser; 
	$.getJSON(leaveGroupChatApiUrl,
			   function(data) {
				loadUserContactList();			
	});	
};

var createNewGroupChatBox = function(chatId, chatName) {
	var template = $("#group-chat-window-template").html();
	var idSuffix = getElementIdSuffix(chatId);
	var maxChat = 4;
	var chatBoxCount = $("#chatBoxContainer").children().length;
	
	if ($('#chatBoxContainer').find('#'+getChatWindowId(idSuffix)).length) {
		return false;
	}	
	if(chatBoxCount >= maxChat) {
		closeFirstChatBox();		
	}				
	
	var data = { chat_window_id : getChatWindowId(idSuffix), name : chatName, minim_id : getMinimId(idSuffix), msg_panel_id : getMsgPanelId(idSuffix), btn_input_id : getBtnInputId(idSuffix), loader_id : getChatBoxLoaderId(idSuffix), chat_user_notification_id : getChatUserNotificationId(idSuffix), chat_id : chatId};	
	
	var html = Mustache.render(template, data);		
	
	$('#chatBoxContainer').append(html);
	reArrangeChatBox();
	displayUserNotificationCount(getChatUserNotificationId(idSuffix), getCurrentUserListNotification(chatId));	
	populateGroupChatHistory(chatId);
	chatsocket.initAction();
	addToOpenChatUsers({id : chatId, type : 'chat'});
	return true;
};	

var populateSelectionList = function(id, data, iconClass) {
	$('#'+id).empty();
	var template = $("#group-user-contact-entry-template").html();
	var loggedInUser = getLoggedInUser();
	var img = "http://www.bitrebels.com/wp-content/uploads/2011/02/Original-Facebook-Geek-Profile-Avatar-1.jpg";
	for(var i = 0; i < data.length; ++i) {
		if(data[i].user == loggedInUser) 
			continue;
		var idSuffix = getElementIdSuffix(data[i].user);
		var params = { user_list_email : data[i].user, user_pic : img, icon : iconClass};
		var html = Mustache.render(template, params);						
		$('#'+id).append(html);
	}
	chatsocket.initAction();		
};

var updateUserSelectionModal = function() {
	var chatId = getSelectedGroupChatId();
	var loggedInUser = getLoggedInUser();
	var getGroupChatApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/getGroupChat?chatId=" + chatId; 
	$.getJSON(getGroupChatApiUrl,
			   function(data) {
					populateSelectionList('modalGroupUserList', data.members, 'fa-minus');
	});		
	var getAvailableUserApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/getAvailableUsers?chatId=" + chatId; 					
	$.getJSON(getAvailableUserApiUrl,
			   function(data) {
		populateSelectionList('modalAvailableUserList', data, 'fa-plus');						
	});
};

var addMemberToGroupChat = function(chatId, user) {
	var loggedInUser = getLoggedInUser();
	var getaddMemberToGroupChatApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/addMemberToGroupChat?chatId=" + chatId +"&user=" + user;
	$.getJSON(getaddMemberToGroupChatApiUrl,
			   function(data) {
				updateUserSelectionModal();
	});
};

var removeMemberFromGroupChat = function(chatId, user) {
	var loggedInUser = getLoggedInUser();
	var getremoveMemberFromGroupChatApiUrl = "http://localhost:8090/EnterpriceChat/rest/chat/removeMemberFromGroupChat?chatId=" + chatId +"&user=" + user;
	$.getJSON(getremoveMemberFromGroupChatApiUrl,
			   function(data) {
				updateUserSelectionModal();
	});
};

var updateUserSelectionList = function(el) {
	var isAdd = el.hasClass('fa-plus');
	var user = el.closest('.header_sec').find('span').text();
	if(isAdd) {
		addMemberToGroupChat(getSelectedGroupChatId(), user);
	} else {
		removeMemberFromGroupChat(getSelectedGroupChatId(), user);
	}
};

var getSelectedGroupChatId = function() {
	return $('#groupChatUserSelection').attr('data-chat-id')	
};

var hasAttribute = function(element, attribute) {
	var attr = $(element).attr(attribute);
	if (typeof attr !== typeof undefined && attr !== false) {
		return true;
	}
	return false;
};

var closeGroupChatWindow = function(chatId) {
	var chatWindowId = getChatWindowId(chatId);
	$('#'+chatWindowId).find('.icon_close').click();
};


var getCurrentTime = function() {
	return moment().format("YYYY-MM-DD HH:mm:ss");	
}