<html>
<body>

<table class="table" id="translations" data-table="messages">
	<thead>
	<tr>
		<th>#</th>
		<g:each in="${localesList}">
			<th title="${it}" data-key="${it}">
				%{--<img src="http://l10n.xwiki.org/xwiki/bin/download/L10N/Flags/${it.toString().replaceFirst('_', '-')}.png"/>--}%
				${it.displayName ?: it}
			</th>
		</g:each>
	</tr>
	</thead>
	<tbody>

	<g:each in="${allTranslations?.keySet().sort() ?: []}" var="key">
		<tr>
			<th>${key}</th>
			<g:each in="${localesList}" var="locale">
				<%
					def sites = sitesByLocale[locale]
					def trans = sites.groupBy { it.messages.getString(key) }

					def _toTable = { data ->
						def ret = '<table>'
						data.each { k, v -> ret += "<tr><td nowrap>${k ?: '<i>none</i>'}</td><td nowrap>${v.join('<br/>')}</td></tr>" }
						ret += '</table>'
					}
				%>
				<td <% if (trans.size() > 1) {
					out << 'data-toggle="popover" title="inconsistent" class="bg-danger" data-content=\'' <<
							_toTable(trans)?.encodeAsHTML() <<
							'\''
				} %>>${sites.first()?.messages.getString(key)}</td>
			</g:each>
		</tr>
	</g:each>

	</tbody>
</table>
</body>
</html>