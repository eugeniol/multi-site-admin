<%@ page import="java.text.DateFormat" %>
<html>
<body>

<ul>
	<h1>Welcome to the Multi Sites Admin</h1>
	<p>The application that helps you to manage multi sites</p>
	<h4>What do you want to do?</h4>
	<%
		def desc = [
				config: "Configure the path to you site app",
				translationsByLanguage: "Edit translations grouped by language",
				translationsBySite: "Edit translations grouped by site",
				siteParams: "Edit site params",
				siteParamsByKey: "Browse site params gruped by values "
		]
	%>
	<g:each in="${['config', 'translationsByLanguage', 'translationsBySite', 'siteParams', 'siteParamsByKey']}"
			var="el">
		<li class="${el == actionName ? 'active' : ''}">

			<g:link action="${el}">
				${desc[el]}

				(<%=el.replaceAll(/\B[A-Z]/) { ' ' + it }.toLowerCase()%>)
			</g:link>

		</li>
	</g:each>
</ul>

</body>
</html>