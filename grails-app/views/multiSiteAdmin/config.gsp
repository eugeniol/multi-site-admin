<%--
  Created by IntelliJ IDEA.
  User: elattanzio
  Date: 29/07/14
  Time: 17:27
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<body>
<g:form method="post" action="config">
	<div class="form-group">
		<label for="project_path">Project path</label>
		<input name="project_path" id="project_path" class="form-control" value="${defaultPath}"/>

	</div>


	<g:actionSubmit value="config" class="btn btn-default"/>
	<g:actionSubmit value="reset" class="btn"/>

</g:form>
</body>
</html>