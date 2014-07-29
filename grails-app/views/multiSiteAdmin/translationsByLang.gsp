<html>
<body>

<table class="table" id="translations">
	<thead>
	<tr>
		<th>#</th>
		<g:each in="${localesList}">
			<th title="${it}">${locales."$it"?.displayName ?: it}</th>
		</g:each>
	</tr>
	</thead>
	<tbody>

	<g:each in="${allTranslations?.keySet().sort() ?:[]}" var="key">
		<tr>

			<th>${key}</th>
			<g:each in="${localesList}" var="locale">
				<td <%
					def _toTable = { data ->
						def ret = '<table>'
						data.each { k, v -> ret += "<tr><td nowrap>$k</td><td nowrap>$v</td></tr>" }
						ret += '</table>'
					}
					if (problem.containsKey(key + '=' + locale)) {
						out << 'data-toggle="popover" title="inconsistent" class="bg-danger" data-content=\'' <<
								_toTable(problem[key + '=' + locale])?.encodeAsHTML() <<
								'\''
					}

				%>>${allTranslations[key].containsKey(locale) ? allTranslations[key][locale] : ''}</td>
			</g:each>
		</tr>
	</g:each>

	</tbody>
</table>
</body>
</html>