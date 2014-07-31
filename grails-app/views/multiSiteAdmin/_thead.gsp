<thead>
<tr>
	<th>
		<div>
			<div class="input-group ">
				<div class="input-group-addon"><i class="glyphicon glyphicon-search"></i></div>
				<input class="form-control" type="text" placeholder="Search key" id="keyFilter">
			</div>
		</div>
	</th>
	<g:each in="${list}">
		<th title="${it}" data-key="${it}"><div>${it instanceof Locale ? (it.displayName ?: it) : it}</div></th>
	%{--<img src="http://l10n.xwiki.org/xwiki/bin/download/L10N/Flags/${it.toString().replaceFirst('_', '-')}.png"/>--}%
	</g:each>
</tr>
</thead>
