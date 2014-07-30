// Avoid `console` errors in browsers that lack a console.
(function() {
    var method;
    var noop = function () {};
    var methods = [
        'assert', 'clear', 'count', 'debug', 'dir', 'dirxml', 'error',
        'exception', 'group', 'groupCollapsed', 'groupEnd', 'info', 'log',
        'markTimeline', 'profile', 'profileEnd', 'table', 'time', 'timeEnd',
        'timeStamp', 'trace', 'warn'
    ];
    var length = methods.length;
    var console = (window.console = window.console || {});

    while (length--) {
        method = methods[length];

        // Only stub undefined methods.
        if (!console[method]) {
            console[method] = noop;
        }
    }
}());

// Place any jQuery/helper plugins in here.

$(function () {
	$('.tree li:has(ul)').addClass('parent_li').find(' > span').attr('title', 'Collapse this branch');
	$('.tree li.parent_li > span').on('click', function (e) {
		var children = $(this).parent('li.parent_li').find(' > ul > li');
		if (children.is(":visible")) {
			children.hide('fast');
			$(this).attr('title', 'Expand this branch').find(' > i').addClass('glyphicon glyphicon-plus-sign').removeClass('glyphicon glyphicon-minus-sign');
		} else {
			children.show('fast');
			$(this).attr('title', 'Collapse this branch').find(' > i').addClass('glyphicon glyphicon-minus-sign').removeClass('glyphicon glyphicon-plus-sign');
		}
		e.stopPropagation();
	});
});


(function ($, window) {

	$.fn.contextMenu = function (settings) {

		return this.each(function () {

			// Open context menu
			$(this).on("contextmenu", function (e) {
				//open menu
				$(settings.menuSelector)
					.data("invokedOn", $(e.target))
					.show()
					.css({
						position: "absolute",
						left: getLeftLocation(e),
						top: getTopLocation(e)
					})
					.off('click')
					.on('click', function (e) {
						$(this).hide();

						var $invokedOn = $(this).data("invokedOn");
						var $selectedMenu = $(e.target);

						settings.menuSelected.call(this, $invokedOn, $selectedMenu);
					});

				return false;
			});

			//make sure menu closes on any click
			$(document).click(function () {
				$(settings.menuSelector).hide();
			});
		});

		function getLeftLocation(e) {
			var mouseWidth = e.pageX;
			var pageWidth = $(window).width();
			var menuWidth = $(settings.menuSelector).width();

			// opening menu would pass the side of the page
			if (mouseWidth + menuWidth > pageWidth &&
				menuWidth < mouseWidth) {
				return mouseWidth - menuWidth;
			}
			return mouseWidth;
		}

		function getTopLocation(e) {
			var mouseHeight = e.pageY;
			var pageHeight = $(window).height();
			var menuHeight = $(settings.menuSelector).height();

			// opening menu would pass the bottom of the page
			if (mouseHeight + menuHeight > pageHeight &&
				menuHeight < mouseHeight) {
				return mouseHeight - menuHeight;
			}
			return mouseHeight;
		}

	};
})(jQuery, window);

