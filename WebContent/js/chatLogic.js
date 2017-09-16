$(document).ready(function() {
	chatsocket.initEvent();
	initializeSessionStorage();
	createOpenChatUsersChatBox();	
});

var OPEN_CHAT_USERS = "openChatUsers";

var getElementIdSuffix = function(email) {
	var suffix = email.replace("@", '').replace('.', '');
	return suffix;
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
	
	var data = { chat_window_id : getChatWindowId(idSuffix), chat_user_email : selectedUserEmail, minim_id : getMinimId(idSuffix), msg_panel_id : getMsgPanelId(idSuffix), btn_input_id : getBtnInputId(idSuffix), loader_id : getChatBoxLoaderId(idSuffix)};	
	
	var html = Mustache.render(template, data);		
	
	$('#chatBoxContainer').append(html);
	reArrangeChatBox();
	populateChatHistory(selectedUserEmail);
	chatsocket.initAction();
	addToOpenChatUsers(selectedUserEmail);	
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
			   			appendSendMessageToChat(idSuffix, messageList[i].content);
			   		 } else{
			   			appendReceiveMessageToChat(idSuffix, messageList[i].content);			   			 
			   		 }
			   	 }
		 	   });		
};

var getLoggedInUser = function() {
	return user = $("#loggedInUser").text();
}
var appendReceiveMessageToChat = function(idSuffix, content) {
	var msg_panel_id = getMsgPanelId(idSuffix);			
	$("#"+msg_panel_id).append(messageReceive(content));	
};

var appendSendMessageToChat = function(idSuffix, content) {
	var msg_panel_id = getMsgPanelId(idSuffix);			
	$("#"+msg_panel_id).append(messageSend(content));	
};

var messageSend = function(message) {
	var buildMessage = "<div class='row msg_container base_sent'>"
			+ "<div class='col-md-10 col-xs-10'> "
			+ "<div class='messages msg_sent'>" + "<p>" + message + "</p>"
			+ "<time datetime='2009-11-13T20:00'>Timothy • 51 min</time>"
			+ "</div>" + "</div>" + "";
	var imgMessage = "<div  class='col-md-2 col-xs-2 avatar'>"
			+ "<img src='http://www.bitrebels.com/wp-content/uploads/2011/02/Original-Facebook-Geek-Profile-Avatar-1.jpg'class='img-responsive ' />"
			+ "</div>" + "</div>";
	return buildMessage + imgMessage;
};

var messageReceive = function(message) {
	var imgMessage = "<div class='row msg_container base_receive'>" +
			"<div  class='col-md-2 col-xs-2 avatar'>"
			+ "<img src='http://www.bitrebels.com/wp-content/uploads/2011/02/Original-Facebook-Geek-Profile-Avatar-1.jpg'class='img-responsive ' />"
			+ "</div>";
	
	var buildMessage = "<div class='col-md-10 col-xs-10'> "
		+ "<div class='messages msg_receive'>" + "<p>" + message + "</p>"
		+ "<time datetime='2009-11-13T20:00'>Timothy • 51 min</time>"
		+ "</div>" + "</div>" + "</div>";
	return imgMessage + buildMessage;
};

var hideLoader = function(loaderDivId, targetDivIdList) {
	$('#'+loaderDivId).css('display', 'none');	
};

var initializeSessionStorage = function() {
	if(sessionStorage.getItem(OPEN_CHAT_USERS) == null) {
		sessionStorage.setItem(OPEN_CHAT_USERS, JSON.stringify([]));
	}
};

var addToOpenChatUsers = function(user) {
	var userList = JSON.parse(sessionStorage.getItem(OPEN_CHAT_USERS));
	if(userList.indexOf(user) == -1) {
		userList.push(user);
		sessionStorage.setItem(OPEN_CHAT_USERS, JSON.stringify(userList));
	}
};

var removeFromOpenChatUsers = function(user) {
	var userList = JSON.parse(sessionStorage.getItem(OPEN_CHAT_USERS));
	var index = userList.indexOf(user);
	if (index > -1) {
	    userList.splice(index, 1);
	}
	sessionStorage.setItem(OPEN_CHAT_USERS, JSON.stringify(userList));
};

var getOpenChatUsers = function(user) {
	return JSON.parse(sessionStorage.getItem(OPEN_CHAT_USERS));	
};

var createOpenChatUsersChatBox = function() {
	var userList = getOpenChatUsers();
	for(var i = 0; i < userList.length; ++i) {
		createNewChatBox(userList[i]);
	}
};
