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

<div class="jumbotron config-form">
	<h2>Project path</h2>

	<p>Configure the path to your site-app</p>

	<p><input name="project_path" id="project_path" class="form-control" value="${defaultPath}"/></p>

	<p><a class="btn btn-primary btn-lg" id="ok">Configure</a> or <a href="#" id="reset">Reset</a> your configuration
	</p>

</div>

</body>
</html>