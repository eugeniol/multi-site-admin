<g:form action="${actionName}" method="get" class="form-inline" role="form">
	<div class="form-group">
		<button class="btn btn-default" onclick="$('.selectpicker').selectpicker('selectAll');">All</button>
		<button class="btn btn-default" onclick="$('.selectpicker').selectpicker('deselectAll');">None</button>
	</div>

	<div class="form-group">
		<label class="sr-only">Sites</label>
		<g:select name="filter" from="${allSites}" multiple="true"
				  value="${params.list('filter') ? sites : []}" class="selectpicker" size="1"/>
	</div>

	<button type="submit" class="btn btn-default">Filter</button>
</g:form>