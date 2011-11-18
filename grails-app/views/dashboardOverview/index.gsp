<%@page import="de.mobile.siteops.DashboardOverviewController"%>
<%@page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'ui-lightness/jquery-ui-1.8.14.custom.css')}"/>
<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'jquery.gritter.css')}"/>
<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'deploymentOverview.css')}" />
<title>Dashboard Overview</title>
</head>
<body>
<g:if test="${model.queueId}">
<div class="header">
<h1>Queue: ${model.queue.environment.name}</h1>
</div>
<g:include action="dashboard" params="[id: model.queueId]" />
</g:if>
<g:else>
<h2>Could not find any queue. Please specify parameter id for the queueid</h2>
</g:else>

</body>
