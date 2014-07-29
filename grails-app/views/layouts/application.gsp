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

<ul class="nav nav-tabs" role="tablist">
	<g:each in="${['index', 'config', 'translationsByLanguage', 'translationsBySite', 'siteParams', 'siteParamsByKey']}" var="el">
		<li class="${el == actionName ? 'active' : ''}"><g:link action="${el}"><%= el.replaceAll(/\B[A-Z]/) { ' ' + it }.toLowerCase() %></g:link></li>
	</g:each>
</ul>

<div class="tab-content">
	<div class="tab-pane active">
		<g:layoutBody/>
	</div>
</div>


<script>window.jQuery || document.write('<script src="${g.resource(dir: 'js/vendor', file: 'jquery-1.10.2.min.js')}"><\/script>')</script>
<script src="${g.resource(dir: 'js/vendor', file: 'rangy-core.js')}"></script>
<script src="${g.resource(dir: 'js/vendor', file: 'bootstrap.js')}"></script>
<script src="${g.resource(dir: 'js/vendor', file: 'bootstrap-select.js')}"></script>
<script src="${g.resource(dir: 'js', file: 'plugins.js')}"></script>

<script src="${g.resource(dir: 'js', file: 'main.js')}"></script>

</body>
</html>