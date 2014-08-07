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


				<table class="table table-bordered">
					<thead><tr>
						<th colspan="3">Top Level Templates</th>
					</tr></thead>

					<tbody><g:each in="${group}" var="item">
						<tr>
							<th>${item.key}</th>
							<th>Child Templates</th>
							<th>Translations used</th>
							%{--<th>translate() calls</th>--}%
						</tr>
						<g:each in="${item.value}" var="template">
							<tr>
								<th>${template.name}</th>
								<td><uke:tree template="${template}"/></td>
								<td>
									<% def words = template.words?.groupBy { it.template.toString() } %>
									<dl>
										<g:each in="${words}" var="w">
											<dt>${w.key}</dt>
											<% def texts =w.value.collect{it.text}.unique() %>
											<g:each in="${texts}" var="text">
												<dd><%=text%></dd>
											</g:each>

										</g:each>

									</dl>
								</td>

								%{--<td><%=template.messagesCalls?.toList().sort()*.encodeAsHTML().join('<br>')%></td>--}%

							</tr>
						</g:each>

					</g:each></tbody>

				</table>
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