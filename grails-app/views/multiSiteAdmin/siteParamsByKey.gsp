<html>
<body>

<h3>Site params goruped by key and value</h3>
<p>This is an easy way to check which sites shares the same configuration</p>
<div class="tree well">
	<ul>
		<g:each in="${allConfig}" var="c">
			<li>
				<i class="glyphicon glyphicon-folder-open"></i> <span>${c.key}</span> <strong>${c.value.size()}</strong>
				<ul>
					<g:each in="${c.value}" var="v">
						<li style="display: none">
							<i class="glyphicon glyphicon-minus-sign"></i> <span>${v.key}</span> <strong>${v.value.size()}</strong>

							<ul>
								<g:each in="${v.value}" var="site">
									<li style="display: none">
										<span><i class="glyphicon glyphicon-leaf"></i> ${site}</span>
									</li>
								</g:each>
							</ul>

							<p></p>
						</li>
					</g:each>
				</ul>
			</li>
		</g:each>
	</ul>
</div>

</body>
</html>