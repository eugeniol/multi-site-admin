<%
	def _name = { l ->
		out << (l instanceof Locale ? (l.displayName ?: l) : l)
	}

	def _flag = { l ->
		String src

		if (l instanceof Locale) {
			src = l?.toString().toLowerCase().replace('_', '-')
			src = g.resource(dir: 'images/flags', file: src.plus('.png'))
		} else {
			def cc = l.countryCode
			if (cc?.size() == 2)
				src = g.resource(dir: 'images/country', file: cc.plus('.png'))
		}

		if (src)
			out << '<img src="' << src << '"/>'

	}
%>
<thead>
<tr>
	<th>
		<div>
			<div class="input-group ">
				<div class="input-group-addon"><i class="glyphicon glyphicon-search"></i></div>
				<input class="form-control" type="text" placeholder="Search key" id="keyFilter">
			</div>
		</div>
	</th>
	<g:each in="${list}">
		<th title="${it}" data-key="${it}"><div>
			<% _flag(it) %>
			<% _name(it) %>
		</div>
		</th>
	</g:each>
</tr>
</thead>
