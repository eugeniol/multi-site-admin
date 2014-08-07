<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"><!--<![endif]-->
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>AdminTool</title>
	<meta name="description" content="">
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

	<link rel="stylesheet" href="${g.resource(dir: 'css', file: 'bootstrap.css')}"/>
	<link rel="stylesheet" href="${g.resource(dir: 'css', file: 'bootstrap-select.css')}"/>
	<link rel="stylesheet" href="${g.resource(dir: 'css', file: 'main.css')}"/>
	<g:layoutHead/>
</head>

<body>

<nav class="navbar navbar-default" role="navigation">
	<div class="container-fluid">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target="#bs-example-navbar-collapse-1">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>

			<span class="navbar-brand">${g.cookie(name: 'project_path')?.decodeURL()?.split(/[\\\/]/)?.last()}</span>
		</div>

		<!-- Collect the nav links, forms, and other content for toggling -->
		<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
			<g:render template="/layouts/navigation"/>
		</div><!-- /.navbar-collapse -->
	</div><!-- /.container-fluid -->
</nav>


<div class="tab-content">
	<div class="tab-pane active">
		<g:layoutBody/>
	</div>
</div>



<ul id="contextMenu" class="dropdown-menu" role="menu" style="display:none">
	<li><a data-action="duplicate" tabindex="-1"><i class="glyphicon glyphicon-file"></i> Copy</a></li>
	<li><a data-action="rename" tabindex="-1"><i class="glyphicon glyphicon-pencil"></i> Rename key</a></li>
	<li><a data-action="delete" tabindex="-1"><i class="glyphicon glyphicon-trash"></i> Delete</a></li>


	%{--<li class="divider"></li>--}%

</ul>



<script>window.jQuery || document.write('<script src="${g.resource(dir: 'js/vendor', file: 'jquery-1.10.2.min.js')}"><\/script>')</script>
<script src="${g.resource(dir: 'js/vendor', file: 'rangy-core.js')}"></script>
<script src="${g.resource(dir: 'js/vendor', file: 'bootstrap.js')}"></script>
<script src="${g.resource(dir: 'js/vendor', file: 'bootstrap-select.js')}"></script>


<script src="${g.resource(dir: 'js', file: 'plugins.js')}"></script>

<script src="${g.resource(dir: 'js', file: 'main.js')}"></script>

</body>
</html>