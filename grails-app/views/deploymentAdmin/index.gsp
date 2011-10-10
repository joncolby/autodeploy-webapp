
<%@page import="de.mobile.siteops.DeploymentOverviewController"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'ui-lightness/jquery-ui-1.8.14.custom.css')}"/>
<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'jquery.gritter.css')}"/>
<script src="${resource(dir:'js',file:'jquery-1.6.min.js')}" type="text/javascript"></script>
<script src="${resource(dir:'js',file:'jquery-ui-1.8.14.custom.min.js')}" type="text/javascript"></script>
<script src="${resource(dir:'js',file:'jquery.tooltip.min.js')}" type="text/javascript"></script>
<script src="${resource(dir:'js',file:'jquery.cookie.js')}" type="text/javascript"></script>
<script src="${resource(dir:'js',file:'jquery.periodicalupdater.js')}" type="text/javascript"></script>
<script src="${resource(dir:'js',file:'jquery.gritter.min.js')}" type="text/javascript"></script>
<script src="${resource(dir:'js',file:'autodeploy.generics.js')}" type="text/javascript"></script>
<script src="${resource(dir:'js',file:'autodeploy.deploymentAdmin.js')}" type="text/javascript"></script>
<link rel="stylesheet"
	href="${resource(dir:'css',file:'deploymentOverview.css')}" />
<title>Deployment Overview</title>
</head>
<body>
<div class="wrapper">
	<div class="header">
	</div>
	<div class="queueContainer">
		<ul class="queues ui-widget-content">
			<li><h2>Inventory</h2></li>
            <li><g:link controller="host" action="ajaxList">Hosts</g:link></li>
            <li><g:link controller="application" action="ajaxList" >Applications</g:link></li>
            <li><g:link controller="hostClass" action="ajaxList" >Host Classes</g:link></li>
            <li><g:link controller="pillar" action="ajaxList" >Pillars</g:link></li>
            <li><g:link controller="environment" action="ajaxList" >Environments</g:link></li>
            <li><g:link controller="repository" action="ajaxList" >Repositories</g:link></li>
            <li><g:link controller="team" action="ajaxList" >Teams</g:link></li>
        </ul>
	</div>
	
        <div class="queueEntryHeader ui-state-hover"><span class="queueText">(none selected yet)</span></div>

		
		<div class="tableEntries"> 
		<table>
		<thead>
			<tr class="ui-widget-header">
			</tr>
		</thead>
		</table>
		</div>
		<div class="clr"></div>

</div>
</body>
</html>