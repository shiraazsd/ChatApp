var chatsocket = function() {

	var messageReceive = "";

	var ws;

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
				var idSuffix = getElementIdSuffix(message.from);
				var status = createNewChatBox(message.from);				
				if(!status) {
					var msg_panel_id = getMsgPanelId(idSuffix);			
					$("#"+msg_panel_id).append(messageReceive(message.content));
				}
		    	scrollToBottom(message.from);
				updateNotification(message.from);
			}
		};

	};

	var sendMessage = function(element) {
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
			"content" : content
		});

		ws.send(json);
		$("#"+msg_panel_id).append(messageSend(content));
	};

	var eventClick = function() {
		$('.btn-chat-send').unbind('click');		
		$('.chat_input').unbind('keypress');
		$('.chat_input').unbind('focus');
		$('#userListCategory').find('li').unbind('click');
		$('#contactList').find('.user_list_entry').unbind('click');		
		$('#contactList').find('.group_chat_list_entry').unbind('çlick');
		$('.chat_input').unbind('focus');
		$('#newGroupChat').unbind('click');
		$('.panel-title-groupchat').find('.chat_box_heading_text').unbind('click');				
		$('.panel-title-groupchat').find('.chat_box_heading_text').unbind('blur');				
		$('.chat_box_heading_text').unbind('keypress');		
		$('.selectGroupChatUser').unbind('click');
		$('#groupChatUserSelection').unbind('shown.bs.modal');
		$('#groupChatUserSelection').find('i').unbind('click');

		
		$('.btn-chat-send').click(function() {
			console.log();
			sendMessage($(this));
	    	var to = $(this).closest('.input-group').find('.btn-chat-send').attr("data-id-email");
	    	scrollToBottom(to);
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
		$('#contactList').find('.group_chat_list_entry').click(function() {
			var chatId = $(this).attr('data-chat-id');
			var chatName = $(this).attr('data-chat-name');
			createNewGroupChatBox(chatId, chatName, 'receipients');
			var id = getChatWindowId(getElementIdSuffix(chatId));
			$('#'+id).find('.chat_input').focus();
		});

		$('.chat_input').focus(function() {			
	    	var to = $(this).closest('.input-group').find('.btn-chat-send').attr("data-id-email");
	    	var notificationId = getChatUserNotificationId(getElementIdSuffix(to));
	    	if($('#'+notificationId).text() != '0') {
	    		markMessagesAsRead(to);
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
	};

	return {
		initEvent : function() {
			connect();
		}, initAction : function() {
			eventClick();
		}		
	}
}();