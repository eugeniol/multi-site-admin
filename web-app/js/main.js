(function ($) {

	$('.selectpicker').selectpicker();

	function getCharacterOffsetWithin(range, node) {
		var treeWalker = document.createTreeWalker(
			node,
			NodeFilter.SHOW_TEXT,
			function (node) {
				var nodeRange = document.createRange();
				nodeRange.selectNode(node);
				return nodeRange.compareBoundaryPoints(Range.END_TO_END, range) < 1 ?
					NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_REJECT;
			},
			false
		);

		var charCount = 0;
		while (treeWalker.nextNode()) {
			charCount += treeWalker.currentNode.length;
		}
		if (range.startContainer.nodeType == 3) {
			charCount += range.startOffset;
		}
		return charCount;
	}


	function _saveTr(td) {

	}

	function _tdSaved(td) {
		td.addClass('saved');
		setTimeout(function () {
			td.removeClass('saved');
		}, 5000)
	}

	$('#translations')
		.on('keydown', 'td[contenteditable]', function (ev) {
			if(ev.keyCode == 13 && ! ev.shiftKey){
				ev.preventDefault()
			}
		})
		.on('blur', 'td[contenteditable]', function (ev) {
			var td = $(this),
				table = $(ev.delegateTarget),
				th = table.find('thead tr th').eq(td.index()),
				key = td.siblings().first().text()

			var val = $.trim($(this).text());

			if (td.data('old') != val) {
				$.post(location.toString(), {value: val, key: key, site: th.data('key')}, function (r) {
					td.removeClass('bg-danger')
					td.removeAttr('data-content')
					td.removeAttr('data-toggle')
					_tdSaved(td)
				});
			}
			td.removeAttr('contenteditable');

		})
		.on('mousedown', 'td', function (ev) {
			var td = $(this),
				table = $(ev.delegateTarget),
				th = table.find('thead tr th').eq(td.index()),
				key = td.siblings().first().text()

			if (td.attr('contenteditable')) {
				return;
			}


			var val = $.trim($(this).text());

			td.data('old', val)
			td.prop('contenteditable', true);
		});

	$(document).on('keydown', function (ev) {
		var t = ev.target, el = $(t),
			_ud = function (p) {
				var ix = el.index(),
					t = el.parent()[p ? 'prev' : 'next']().children().eq(ix)
				t.mousedown().focus();
				ev.preventDefault();
			},
			_lr = function (p) {
				var t = el.blur()[p ? 'prev' : 'next']()
				t.mousedown().focus();
				ev.preventDefault();
			}

		if (el.is('td')) {
			var range = window.getSelection().getRangeAt(0),
				l = t.innerHTML.length,
				pos = getCharacterOffsetWithin(range, t)


//			if (ev.keyCode in [37, 38, 39, 40, 9, 13]) {
//				ev.preventDefault();
//				ev.stopPropagation();
//			}

			switch (ev.keyCode) {
				case 38:
					if (pos == 0 || ev.altKey)
						_ud(true)
					break
				case 40:
					if (pos == l || ev.altKey)
						_ud()
					break
				case 37:
					if (pos == 0 || ev.altKey)
						_lr(true)
					break
				case 39:
					if (pos == l || ev.altKey)
						_lr()

					break;
				case 9:
					_lr(ev.shiftKey)
					break;
				case 13:
					if (!ev.shiftKey)
						_lr();
					break;

			}


		}

	});

	$('td.bg-danger').popover({
		trigger: 'focus',
		html: true,
	})

	function getNewName(th) {
		var newKey = prompt('Insert new key name', th.text()),
			table = th.parents('table').first()

		while (table.find('tbody th').filter(function () {
			return $.trim($(this).text()) == newKey
		}).size() > 0) {
			newKey = prompt('Insert new key name, the key exists already', newKey)
		}

		return newKey
	}


	function _deleteRow(th, action) {
		var tr = th.parent(),
			key = th.text(),
			table = tr.parents('table').first(),
			newKey

		if (action === 'rename' || action === 'duplicate') {
			newKey = getNewName(th)
			if (!newKey)
				return
		}

		$.post(action, {key: key, table: table.data('table'), newKey: newKey}, function (r) {
			console.log(action, 'saved');
			switch (action) {

				case 'rename':
					th.text(newKey)
					break
				case 'delete':
					tr.remove()
					break;
				case 'duplicate':
					var newRow = tr.clone(true);
					newRow.find('th').text(newKey);
					tr.after(newRow);
					break

			}
			_tdSaved(td)

		});
	}


	$("table tbody th").contextMenu({
		menuSelector: "#contextMenu",
		menuSelected: function (invokedOn, selectedMenu) {
//			console.log(invokedOn, selectedMenu)
			var action = selectedMenu.data('action')
			switch (action) {
				case 'duplicate':
				case 'delete':
				case 'rename':
					_deleteRow(invokedOn, action);
					break

			}
		}
	});


}(jQuery));


//$w
//	.load(setWidths)
//	.resize($.debounce(250, function () {
//		setWidths();
//		repositionStickyHead();
//		repositionStickyCol();
//	}))
//	.scroll($.throttle(250, repositionStickyHead));

$(function () {
	$('table thead th').each(function () {
		$(this).css('width', $(this).width() + 'px')
	})
})
$(window).on('scroll resize load', $.throttle(250, function () {

	if (!$('table').size())
		return

	if (scrollX > 230) {
		$('table tbody th').css('left', scrollX + 'px')
		$('table').addClass('sticky-x')
	}
	else {
		$('table').removeClass('sticky-x')
		$('table tbody th').css('height', 'auto')
	}

	if (scrollY > $('table thead').offset().top + $('table thead').outerHeight(true)) {
		$('table').addClass('sticky-y');
		$('table thead th strong').css('top', scrollY + 'px')
	}
	else {
		$('table').removeClass('sticky-y')
	}

}))
