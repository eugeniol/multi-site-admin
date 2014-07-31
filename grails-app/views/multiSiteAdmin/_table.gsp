<g:if test="${sites}">
	<table class="table" id="translations" data-table="${type}">
		<g:render template="thead" model="[list: sites]"/>

		<tbody>

		<g:each in="${allTranslations.keySet()}" var="t">
			<tr>
				<th>${t}</th>
				<g:each in="${sites}" var="site">
					<td>${allTranslations[t].containsKey(site) ? allTranslations[t][site].encodeAsHTML() : ''}</td>
				</g:each>
			</tr>
		</g:each>

		</tbody>
	</table>
</g:if>
<g:else>
	<div class="alert alert-success" role="alert">Pick a site to start</div>
</g:else>