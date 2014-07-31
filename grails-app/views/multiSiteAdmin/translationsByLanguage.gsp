<html>
<body>

<table class="table" id="translations" data-table="messages">
	<g:render template="thead" model="[list: localesList]"/>
	<tbody>
	<g:each in="${allTranslations?.keySet() ?: []}" var="key">
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
				} %>>${sites.first()?.messages.getString(key)?.encodeAsHTML()}</td>
			</g:each>
		</tr>
	</g:each>

	</tbody>
</table>
</body>
</html>