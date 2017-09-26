var chatsocket = function() {

	var ws;

	var connect = function() {
		var user = $("#user").val();
		try{
			ws = new WebSocket("ws://localhost:8090/EnterpriceChat/chat/" + user);
		} catch(err) {			
			console.log("error");
		}
		ws.onmessage = function(event) {
			var message = JSON.parse(event.data);
			var img = imgUrl + message.from;				 	
			//Refresh the list since there is a user which is online/offline now
			if(message.content == 'refreshContact') {
				loadUserContactList();
			} else {
				var idSuffix;
				var status;
				var id;
				if(isChatContext()) {				
					if(message.isGroupChat) {
						idSuffix = getElementIdSuffix(message.id);
						status = createNewGroupChatBox(message.id, message.id);
						id = message.id;
						fetchAndPopulateGroupChatContactList()					
					} else {
						idSuffix = getElementIdSuffix(message.from);						
						status = createNewChatBox(message.from);
						id = message.from;
						fetchAndPopulateUserContactList();					
					}				
					if(!status) {
						var msg_panel_id = getMsgPanelId(idSuffix);			
						if(message.isGroupChat) {
							$("#"+msg_panel_id).append(groupChatMessageReceive(message.content, message.from, message.messageTime, img));
						} else {
							$("#"+msg_panel_id).append(messageReceive(message.content, message.messageTime, img));						
						}
						
						}
			    	scrollToBottom(id);
					updateNotification(id);
				} else {

					if(message.isGroupChat) {
						if(getInboxGroupChatId() == message.id) {
							appendReceiveMessageToInboxGroupChat(img, message.content, message.from, message.messageTime);
						}
						fetchAndPopulateGroupChatContactList();					
					} else {
						if(getInboxChatUser() == message.from) {
							appendReceiveMessageToInboxChat(img, message.content, message.messageTime);
						}
						fetchAndPopulateUserContactList();					
					}				
					scrollToInboxBottom();
					updateInboxNotification(id, message.isGroupChat);
					
				}
			}
		};

	};

	var sendPersonalMessage = function(element) {
		var inputElementId = element.attr("data-id");
		var to = element.attr("data-id-email");
		var msg_panel_id = element.attr("data-id-msg-panel");
		
		//Validating the inputs
		var content = $("#"+inputElementId).val();
		//Reset the input fields
		$("#"+inputElementId).val('');
		if(!content) {
			return;
		}

		var json = JSON.stringify({
			"to" : to,
			"content" : content,
			"isGroupChat" : false,
			"id" : null 
		});

		ws.send(json);
		var img = imgUrl + getLoggedInUser();
		$("#"+msg_panel_id).append(messageSend(content, getCurrentTime(), img));
	};

	var sendPersonalInboxMessage = function(user) {
		var messageElement;
		var isGroupChat = isInboxGroupChatSet();		
		//Validating the inputs
		var content = $('#inputMessageContent').val();
		//Reset the input fields
		$('#inputMessageContent').val('');
		if(!content) {
			return;
		}

		if(isGroupChat) {
			var chatId = getInboxGroupChatId();
			var json = JSON.stringify({
				"to" : null,
				"content" : content,
				"isGroupChat" : true,
				"id" : chatId 
			});
			
		} else {
			var to = getInboxChatUser();
			var json = JSON.stringify({
				"to" : to,
				"content" : content,
				"isGroupChat" : false,
				"id" : null 
			});			
		}
		ws.send(json);
		var img = imgUrl + getLoggedInUser();
		appendSendMessageToInboxChat(img, content, getCurrentTime());
	};
	
	var sendGroupChatMessage = function(element) {
		var inputElementId = element.attr("data-id");
		var chatId = element.attr("data-chat-id");
		var msg_panel_id = element.attr("data-id-msg-panel");
		//Validating the inputs
		var content = $("#"+inputElementId).val();
		//Reset the input fields
		$("#"+inputElementId).val('');
		if(!content) {
			return;
		}

		var json = JSON.stringify({
			"to" : null,
			"content" : content,
			"isGroupChat" : true,
			"id" : chatId 
		});

		ws.send(json);
		var img = imgUrl + getLoggedInUser();
		$("#"+msg_panel_id).append(messageSend(content, getCurrentTime(), img));
	};
	
	var eventClick = function() {
		$('.personal_chat_window').find('.btn-chat-send').unbind('click');
		$('#btnSendMessage').unbind('click');
		$('.group_chat_window').find('.btn-chat-send').unbind('click');
		$('#inputMessageContent').unbind('keypress');
		$('.chat_input').unbind('keypress');
		$('#userListCategory').find('li').unbind('click');
		$('#contactList').find('.user_list_entry').unbind('click');
		$('#contactList').find('.group_chat_list_entry').find('.chat_box_open_class').unbind('click');
		$('.personal_chat_window').find('.chat_input').unbind('focus');			
		$('#inputMessageContent').unbind('focus');
		$('.group_chat_window').find('.chat_input').unbind('focus');			
		$('#newGroupChat').unbind('click');
		$('.panel-title-groupchat').find('.chat_box_heading_text').unbind('click');
		$('.panel-title-groupchat').find('.chat_box_heading_text').unbind('blur');
		$('.chat_box_heading_text').unbind('keypress');
		$('.selectGroupChatUser').unbind('click');
		$('#groupChatUserSelection').unbind('shown.bs.modal');
		$('#groupChatUserSelection').find('i').unbind('click');
		$('#contactList').find('.group_chat_list_entry').find('.option_open_class').unbind('click');
		$('#clearGroupChat').unbind('click');
		$('#leaveGroupChat').unbind('click');
		$('#inboxGroupChatName').unbind('click');
		$('#inboxGroupChatName').unbind('blur');
		$('#inboxGroupChatName').unbind('keypress');
		$('#searchSideBar').unbind('keyup');
		
		
		$('.personal_chat_window').find('.btn-chat-send').click(function() {
			console.log();
			sendPersonalMessage($(this));
	    	var to = $(this).closest('.input-group').find('.btn-chat-send').attr("data-id-email");
	    	scrollToBottom(to);
		});
		$('#btnSendMessage').click(function() {
			sendPersonalInboxMessage($(this));
			scrollToInboxBottom();			
		});
		$('.group_chat_window').find('.btn-chat-send').click(function() {
			console.log();
			sendGroupChatMessage($(this));
	    	var id = $(this).closest('.input-group').find('.btn-chat-send').attr("data-chat-id");
	    	scrollToBottom(id);
		});
		$('#inputMessageContent').keypress(function(e) {
		    if(e.which == 13) {
		    	$('#btnSendMessage').click();
		    }
		});	
		$('.chat_input').keypress(function(e) {
		    if(e.which == 13) {
		    	$(this).closest('.input-group').find('.btn-chat-send').click();
		    }
		});	

		$('#userListCategory').find('li').click(function() {
			var type = $(this).attr("data-id");
			$('#userListCategory').attr("data-current-selection", type);			
			loadUserContactList(type);
		});				
		$('#contactList').find('.user_list_entry').click(function() {
			var selectedUserEmail = $(this).attr('data-id-email');
			performContactListUserClickActionOnContext(selectedUserEmail);
		});
		$('#contactList').find('.group_chat_list_entry').find('.chat_box_open_class').click(function() {
			var el = $(this).closest('.group_chat_list_entry');
			var chatId = el.attr('data-chat-id');
			var chatName = el.attr('data-chat-name');
			performGroupChatListClickActionOnContext(chatId, chatName);
		});

		$('.personal_chat_window').find('.chat_input').focus(function() {			
	    	var to = $(this).closest('.input-group').find('.btn-chat-send').attr("data-id-email");
	    	var notificationId = getChatUserNotificationId(getElementIdSuffix(to));
	    	if($('#'+notificationId).text() != '0') {
	    		markMessagesAsRead(to);
	    	}
		});	
		$('#inputMessageContent').focus(function() {			
			if(isInboxGroupChatSet()) {
				var chatId = getInboxGroupChatId();
		    	var notificationId = getListUserNotificationId(getElementIdSuffix(chatId));
		    	if($('#'+notificationId).text() != '0') {
		    		markGroupChatMessagesAsRead(chatId);
		    	}				
			} else {
				var user = getInboxChatUser();
		    	var notificationId = getListUserNotificationId(getElementIdSuffix(user));
		    	if($('#'+notificationId).text() != '0') {
		    		markMessagesAsRead(user);
		    	}
			}
		});	

		$('.group_chat_window').find('.chat_input').focus(function() {			
	    	var chatId = $(this).closest('.input-group').find('.btn-chat-send').attr("data-chat-id");
	    	var notificationId = getChatUserNotificationId(getElementIdSuffix(chatId));
	    	var count = $('#'+notificationId).text();
	    	if($('#'+notificationId).text() != '0' && $('#'+notificationId).text() !='') {
	    		markGroupChatMessagesAsRead(chatId);
	    	}
		});	

		$('#newGroupChat').click(function() {
			createNewGroupChat();
		});				

		$('.panel-title-groupchat').find('.chat_box_heading_text').click(function() {
	        $(this).attr('contentEditable', true);
	        $(this).removeClass('chat_box_heading_text');
	        $(this).addClass('chat_box_heading_text_input');
	        $(this).focus();
		}).blur(
	        function() {
	            $(this).attr('contentEditable', false);
		        $(this).removeClass('chat_box_heading_text_input');
		        $(this).addClass('chat_box_heading_text');
		        var chatId = $(this).closest('.chat-window').attr('data-chat-id');
	            updateGroupChat(chatId, $(this).text());

	        });		
		$('.chat_box_heading_text').keypress(function(e) {
		    if(e.which == 13) {
		    	$(this).blur();
		    }
		});	
		
		$('.selectGroupChatUser').click(function (e) {
			var chatId;
			if(isChatContext()) {
				chatId = $(this).closest('.chat-window').attr('data-chat-id');
			} else {
				chatId = getInboxGroupChatId();
			}
			$('#groupChatUserSelection').attr('data-chat-id', chatId);
			$('#groupChatUserSelection').modal('show');				
		});
		
		$('#groupChatUserSelection').on('shown.bs.modal', function (e) {
			updateUserSelectionModal();
		});
		
		$('#groupChatUserSelection').find('i').click(function(e) {
			updateUserSelectionList($(this));
		});
		$('#contactList').find('.group_chat_list_entry').find('.option_open_class').click(function() {
			var chatId = $(this).closest('.group_chat_list_entry').attr('data-chat-id');
			$('#groupChatOption').attr('data-chat-id', chatId);			
			$('#groupChatOption').modal('show');				
		});
		$('#clearGroupChat').click( function(e) {
			var chatId = $(this).closest('#groupChatOption').attr('data-chat-id');
			clearGroupChatHistory(chatId);
			$('#groupChatOption').modal('hide');
			closeGroupChatWindow(chatId);			
			
		});
		$('#leaveGroupChat').click( function(e) {
			var chatId = $(this).closest('#groupChatOption').attr('data-chat-id');
			leaveGroupChatHistory(chatId);			
			$('#groupChatOption').modal('hide');				
			closeGroupChatWindow(chatId);			
		});
		
		$('#inboxGroupChatName').click(function() {
	        $(this).attr('contentEditable', true);
	        $(this).removeClass('inboxChatTitleEditable');
	        $(this).addClass('inboxChatTitleEditing');
	        $(this).focus();
		}).blur(
	        function() {
	            $(this).attr('contentEditable', false);
		        $(this).removeClass('inboxChatTitleEditing');
		        $(this).addClass('inboxChatTitleEditable');
		        var chatId = getInboxGroupChatId();
	            updateGroupChat(chatId, $(this).text());

	        });		
		$('#inboxGroupChatName').keypress(function(e) {
		    if(e.which == 13) {
		    	$(this).blur();
		    }
		});			
		$('#searchSideBar').keyup(function() {
			var value = $(this).val();
			filterSideBarList(value);
		});
	};

	return {
		initEvent : function() {
			connect();
		}, initAction : function() {
			eventClick();
		}		
	}
}();