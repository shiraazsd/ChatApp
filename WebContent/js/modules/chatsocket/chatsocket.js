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
			//Refresh the list since there is a user which is online/offline now
			if(message.content == 'refreshContact') {
				loadUserContactList();
			} else {
				var idSuffix;
				var status;
				var id;
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
						$("#"+msg_panel_id).append(groupChatMessageReceive(message.content, message.from, message.messageTime));
					} else {
						$("#"+msg_panel_id).append(messageReceive(message.content, message.messageTime));						
					}
					
					}
		    	scrollToBottom(id);
				updateNotification(id);
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
		$("#"+msg_panel_id).append(messageSend(content, getCurrentTime()));
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
		$("#"+msg_panel_id).append(messageSend(content, getCurrentTime()));
	};
	
	var eventClick = function() {
		$('.personal_chat_window').find('.btn-chat-send').unbind('click');
		$('.group_chat_window').find('.btn-chat-send').unbind('click');
		$('.chat_input').unbind('keypress');
		$('#userListCategory').find('li').unbind('click');
		$('#contactList').find('.user_list_entry').unbind('click');
		$('#contactList').find('.group_chat_list_entry').find('.chat_box_open_class').unbind('click');
		$('.personal_chat_window').find('.chat_input').unbind('focus');			
		$('.group_chat_window').find('.chat_input').unbind('focus');			
		$('#newGroupChat').unbind('click');
		$('.panel-title-groupchat').find('.chat_box_heading_text').unbind('click');
		$('.chat_box_heading_text').unbind('keypress');
		$('.selectGroupChatUser').unbind('click');
		$('#groupChatUserSelection').unbind('shown.bs.modal');
		$('#groupChatUserSelection').find('i').unbind('click');
		$('#contactList').find('.group_chat_list_entry').find('.option_open_class').unbind('click');
		$('#clearGroupChat').unbind('click');
		$('#leaveGroupChat').unbind('click');
		
		$('.personal_chat_window').find('.btn-chat-send').click(function() {
			console.log();
			sendPersonalMessage($(this));
	    	var to = $(this).closest('.input-group').find('.btn-chat-send').attr("data-id-email");
	    	scrollToBottom(to);
		});
		$('.group_chat_window').find('.btn-chat-send').click(function() {
			console.log();
			sendGroupChatMessage($(this));
	    	var id = $(this).closest('.input-group').find('.btn-chat-send').attr("data-chat-id");
	    	scrollToBottom(id);
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
			createNewChatBox(selectedUserEmail);
			var id = getChatWindowId(getElementIdSuffix(selectedUserEmail));
			$('#'+id).find('.chat_input').focus();
		});
		$('#contactList').find('.group_chat_list_entry').find('.chat_box_open_class').click(function() {
			var el = $(this).closest('.group_chat_list_entry');
			var chatId = el.attr('data-chat-id');
			var chatName = el.attr('data-chat-name');
			createNewGroupChatBox(chatId, chatName);
			var id = getChatWindowId(getElementIdSuffix(chatId));
			$('#'+id).find('.chat_input').focus();
		});

		$('.personal_chat_window').find('.chat_input').focus(function() {			
	    	var to = $(this).closest('.input-group').find('.btn-chat-send').attr("data-id-email");
	    	var notificationId = getChatUserNotificationId(getElementIdSuffix(to));
	    	if($('#'+notificationId).text() != '0') {
	    		markMessagesAsRead(to);
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
			var chatId = $(this).closest('.chat-window').attr('data-chat-id');
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
		
	};

	return {
		initEvent : function() {
			connect();
		}, initAction : function() {
			eventClick();
		}		
	}
}();