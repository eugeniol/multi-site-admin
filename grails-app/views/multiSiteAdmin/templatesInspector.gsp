<%--
  Created by IntelliJ IDEA.
  User: elattanzio
  Date: 01/08/14
  Time: 14:09
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>

<div class="panel-group" id="accordion">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" data-parent="#accordion" href="#collapseOne">
					templates without parent
				</a>
			</h4>
		</div>

		<div id="collapseOne" class="panel-collapse">
			<div class="panel-body">
				<%
					def group = project.topLevelTemplates.groupBy {
						def list = it.name.split(/\//)
						list.size() > 1 ? list[1] : ''
					}
				%>
				<g:each in="${group}" var="item">
					<h4>${item.key}</h4>
					<ul>

						<g:each in="${item.value}" var="template">
							<li>
								<strong>${template.name}</strong>

								<div>
									${template.translations?.sort()*.encodeAsHTML() .join(', ')}
								</div>
								<uke:tree template="${template}"/>
							</li>

						</g:each>
					</ul>
				</g:each>

			</div>
		</div>
	</div>

	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" class="collapsed">
					All templates
				</a>
			</h4>
		</div>

		<div id="collapseTwo" class="panel-collapse collapse">
			<div class="panel-body">
				<ul>
					<g:each in="${project.templates}" var="template">
						<li>${template.name}</li>
						<ul>
							<g:each in="${template.parents}" var="child">
								<li>${child.name}</li>
							</g:each>
						</ul>
					</g:each>
				</ul>
			</div>
		</div>
	</div>
</div>

</body>
</html>