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

	$('#translations').
		on('mousedown', 'td', function (ev) {
			var td = $(this),
				table = $(ev.delegateTarget),
				th = table.find('thead tr th').eq(td.index()),
				key = td.siblings().first().text()


			if (td.attr('contenteditable')) {
				return;
			}

			var old = this.innerHTML


			td.one('blur', function () {
				var val = this.innerHTML
				if (old != val) {
					$.post(location.toString(), {value: val, key: key, site: th.data('key')}, function (r) {
						console.log('saved');
					});
				}
			});

			td.attr('contenteditable', true);

			return


			var input = $('<input/>').val(td.text()),
				ok = $('<button/>').text('ok'),
				key = td.siblings().first().text()

			input.blur(function (ev) {
				$.post('update', {text: input.val(), key: key, site: th.text()}, function (r) {
					console.log(r);
					td.html(input.val());
				});
				ev.stopPropagation();
			});

			td.html(input);
			input.focus();
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
				case 13:
					_lr(ev.shiftKey)
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

		$.post(action, {key: key, file: table.data('type'), newKey: newKey}, function (r) {
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

			}

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
