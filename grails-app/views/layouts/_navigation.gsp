<%
	mode = mode ?: 'top'
	def desc = [
			index: 'Home Page',
			config: "Configure the path to you site app",
			translationsByLanguage: "Edit translations grouped by language",
			translationsBySite: "Edit translations grouped by site",
			siteParams: "Edit site params",
			siteParamsByKey: "Browse site params gruped by values ",
			templatesInspector: 'Browser templates'
	]
%>



<ul class="${mode == 'top' ? 'nav navbar-nav' : ''}" role="tablist">
	<g:each in="${['index', 'config', 'translationsByLanguage', 'translationsBySite',
			'siteParams', 'siteParamsByKey', 'templatesInspector']}"
			var="el">
		<li class="${el == actionName ? 'active' : ''}">
			<g:link action="${el}">
				<g:if test="${showDescription}">
					${desc[el]}
				</g:if>
				<%=el.replaceAll(/\B[A-Z]/) { ' ' + it }.toLowerCase()%>
			</g:link>
		</li>
	</g:each>
</ul>