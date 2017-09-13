$.fn.chat = function(options) {
	
	var container = '<div></div>';

	// Extend our default options with those provided.
	// Note that the first argument to extend is an empty
	// object – this is to keep from overriding our "defaults" object.
	var opts = $.extend(true, $.fn.chat.defaults, options);

	if ('data' in options && options.data.lenght > 0) {
		$.each(options.data, function(i, option) {
			$('.container_message').appendTo(combo);
		});
	}

	/*
	 * if('sendmessage' in options ){
	 *  }
	 */

	// Our plugin implementation code goes here.
};

// Plugin defaults – added as a property on our plugin function.

$.fn.chat.defaults = {
	foreground : "red",
	background : "yellow",
	append : "<div class='col-md-10 col-xs-10'></div>"
};

