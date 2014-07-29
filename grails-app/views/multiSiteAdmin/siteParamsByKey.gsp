<html>
<body>
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
<div class="tree well">
	<ul>
		<li>
			<span><i class="glyphicon glyphicon-folder-open"></i> Parent</span> <a href="">Goes somewhere</a>
			<ul>
				<li>
					<span><i class="glyphicon glyphicon-minus-sign"></i> Child</span> <a href="">Goes somewhere</a>
					<ul>
						<li>
							<span><i class="glyphicon glyphicon-leaf"></i> Grand Child</span> <a href="">Goes somewhere</a>
						</li>
					</ul>
				</li>
				<li>
					<span><i class="glyphicon glyphicon-minus-sign"></i> Child</span> <a href="">Goes somewhere</a>
					<ul>
						<li>
							<span><i class="glyphicon glyphicon-leaf"></i> Grand Child</span> <a href="">Goes somewhere</a>
						</li>
						<li>
							<span><i class="glyphicon glyphicon-minus-sign"></i> Grand Child</span> <a href="">Goes somewhere</a>
							<ul>
								<li>
									<span><i class="glyphicon glyphicon-minus-sign"></i> Great Grand Child</span> <a href="">Goes somewhere</a>
									<ul>
										<li>
											<span><i class="glyphicon glyphicon-leaf"></i> Great great Grand Child</span> <a href="">Goes somewhere</a>
										</li>
										<li>
											<span><i class="glyphicon glyphicon-leaf"></i> Great great Grand Child</span> <a href="">Goes somewhere</a>
										</li>
									</ul>
								</li>
								<li>
									<span><i class="glyphicon glyphicon-leaf"></i> Great Grand Child</span> <a href="">Goes somewhere</a>
								</li>
								<li>
									<span><i class="glyphicon glyphicon-leaf"></i> Great Grand Child</span> <a href="">Goes somewhere</a>
								</li>
							</ul>
						</li>
						<li>
							<span><i class="glyphicon glyphicon-leaf"></i> Grand Child</span> <a href="">Goes somewhere</a>
						</li>
					</ul>
				</li>
			</ul>
		</li>
		<li>
			<span><i class="glyphicon glyphicon-folder-open"></i> Parent2</span> <a href="">Goes somewhere</a>
			<ul>
				<li>
					<span><i class="glyphicon glyphicon-leaf"></i> Child</span> <a href="">Goes somewhere</a>
				</li>
			</ul>
		</li>
	</ul>
</div>

<div class="tree">
	<ul>
		<li>
			<span><i class="glyphicon glyphicon-calendar"></i> 2013, Week 2</span>
			<ul>
				<li>
					<span class="badge badge-success"><i class="glyphicon glyphicon-minus-sign"></i> Monday, January 7: 8.00 hours</span>
					<ul>
						<li>
							<a href=""><span><i class="glyphicon glyphicon-time"></i> 8.00</span> &ndash; Changed CSS to accomodate...</a>
						</li>
					</ul>
				</li>
				<li>
					<span class="badge badge-success"><i class="glyphicon glyphicon-minus-sign"></i> Tuesday, January 8: 8.00 hours</span>
					<ul>
						<li>
							<span><i class="glyphicon glyphicon-time"></i> 6.00</span> &ndash; <a href="">Altered code...</a>
						</li>
						<li>
							<span><i class="glyphicon glyphicon-time"></i> 2.00</span> &ndash; <a href="">Simplified our approach to...</a>
						</li>
					</ul>
				</li>
				<li>
					<span class="badge badge-warning"><i class="glyphicon glyphicon-minus-sign"></i> Wednesday, January 9: 6.00 hours</span>
					<ul>
						<li>
							<a href=""><span><i class="glyphicon glyphicon-time"></i> 3.00</span> &ndash; Fixed bug caused by...</a>
						</li>
						<li>
							<a href=""><span><i class="glyphicon glyphicon-time"></i> 3.00</span> &ndash; Comitting latest code to Git...</a>
						</li>
					</ul>
				</li>
				<li>
					<span class="badge badge-important"><i class="glyphicon glyphicon-minus-sign"></i> Wednesday, January 9: 4.00 hours</span>
					<ul>
						<li>
							<a href=""><span><i class="glyphicon glyphicon-time"></i> 2.00</span> &ndash; Create component that...</a>
						</li>
					</ul>
				</li>
			</ul>
		</li>
		<li>
			<span><i class="glyphicon glyphicon-calendar"></i> 2013, Week 3</span>
			<ul>
				<li>
					<span class="badge badge-success"><i class="glyphicon glyphicon-minus-sign"></i> Monday, January 14: 8.00 hours</span>
					<ul>
						<li>
							<span><i class="glyphicon glyphicon-time"></i> 7.75</span> &ndash; <a href="">Writing documentation...</a>
						</li>
						<li>
							<span><i class="glyphicon glyphicon-time"></i> 0.25</span> &ndash; <a href="">Reverting code back to...</a>
						</li>
					</ul>
				</li>
			</ul>
		</li>
	</ul>
</div>
</body>
</html>