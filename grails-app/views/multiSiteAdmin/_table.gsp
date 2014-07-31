<g:if test="${sites}">
	<table class="table" id="translations" data-table="${type}">
		<thead>
		<tr>
			<th>#</th>
			<g:each in="${sites}">
				<th data-key="${it}">${it}</th>
			</g:each>
		</tr>
		</thead>
		<tbody>

		<g:each in="${allTranslations.keySet().sort()}" var="t">
			<tr>
				<th>${t}</th>
				<g:each in="${sites}" var="site">
					<td>${allTranslations[t].containsKey(site) ? allTranslations[t][site] : ''}</td>
				</g:each>
			</tr>
		</g:each>

		</tbody>
	</table>
</g:if>
<g:else>
	<div class="alert alert-success" role="alert">Pick a site to start</div>
</g:else>