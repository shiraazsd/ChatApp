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
		ws = new WebSocket("ws://localhost:8090/EnterpriceChat/chat/" + user);

		ws.onmessage = function(event) {
			var message = JSON.parse(event.data);
			//log.innerHTML += message.from + " : " + message.content + "\n";
			$(".panel-body").append(messageReceive(message.content));
		};

	};

	var sendMessage = function(element) {
		var inputElementId = element.attr("data-id");
		var to = element.attr("data-id-email");
		var msg_panel_id = element.attr("data-id-msg-panel");
		
		var content = $("#"+inputElementId).val();
		var json = JSON.stringify({
			"to" : to,
			"content" : content
		});

		ws.send(json);
		$("#"+msg_panel_id).append(messageSend(content));
	};

	var eventClick = function() {
		console.log($("#btn-input").val());
		$("#btn-chat").click(function() {
			console.log();
			sendMessage($(this));
		});
	};

	return {
		initEvent : function() {
			connect();
			eventClick();
		}
	}
}();