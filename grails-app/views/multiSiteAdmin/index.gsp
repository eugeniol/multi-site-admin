<%@ page import="java.text.DateFormat" %>
<html>
<body>

<%
	DateFormat.availableLocales.each {
//		out << it.language
		out << it.displayName
		out << '<br/>'
	}


%>

</body>
</html>