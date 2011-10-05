<%--
  Created by IntelliJ IDEA.
  User: ibartel
  Date: 04.07.11
  Time: 17:24
  To change this template use File | Settings | File Templates.
--%>

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
<script src="${resource(dir:'js',file:'autodeploy.deploymentOverview.Row.js')}" type="text/javascript"></script>
<script src="${resource(dir:'js',file:'autodeploy.deploymentOverview.Table.js')}" type="text/javascript"></script>
<script src="${resource(dir:'js',file:'autodeploy.deploymentOverview.List.js')}" type="text/javascript"></script>
<script src="${resource(dir:'js',file:'autodeploy.deploymentOverview.js')}" type="text/javascript"></script>
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
		
		<li><h2>Queues</h2></li>
		<g:each in="${model.queues}" var="queue">
			<li <g:if test="${queue.id == 2}">class="locked"</g:if>><g:link action="queueEntries" id="${queue.id}" queueId="${queue.id}">
			${queue.name}
			<g:if test="${queue.id == 2}"><span class="ui-icon ui-icon-key"></span></g:if>
			</g:link></li>
		</g:each>
		</ul>		
		<ul class="queues ui-widget-content">
		<li><h2><a href="<g:createLink controller="deploymentPlanManagment" action="index" />" class="modal">Plan Managment</a></h2></li>
		<li><strong><a href="<g:createLink controller="applicationVersions" action="index" />" class="modalDiv">App Revisions</a></strong></li>
		</ul>
	</div>
        <div class="queueEntryHeader ui-state-hover">Queue: <span class="queueText">(none selected yet)</span></div>

        <div class="fastDeploy">
        
	        <form name="syncEnv" action="<g:createLink controller="deployAction" action="syncEnv"/>">
                <label>Sync enviroment: </label>
		        <select>
		        <option value="0">sync with</option>
		        <g:each in="${model.queues}" var="queue">
					<option value="${queue.id}">${queue.name}</option>
				</g:each>
		        </select>
	        </form>
        	<form name="assignPlan" action="<g:createLink controller="deploymentPlanManagment" action="addToQueue" />">
                <label>Fast deploy plan: </label>
		        <select name="teamId">
		            <g:each in="${model.teams}" var="team">
		            <option value="${team.id}" url="${team.url}"
				        <g:if test="${model.selectedTeamId && model.selectedTeamId == team.id}" >
				        selected = "selected"
				        </g:if> >
		        		${team.name}
		        	</option>
		            </g:each>
		        </select>
	      	  <select name="planId">
		            <g:each in="${model.plans}" var="plan">
		            <option value="${plan.id}"
				        <g:if test="${model.selectedPlanId && model.selectedPlanId == plan.id}" >
				        selected = "selected"
				        </g:if> >
		        		${plan.name}
		        	</option>
		            </g:each>
		        </select>
		        <input type = "text" name = "revision" />
		        <input type = "submit" value = "Assign" />
	        </form>
	        
		</div>
		
		<div class="queueEntries"> 
		<table>
		<thead>
			<tr class="ui-widget-header">
				<td class="col1">Team</td>
				<td class="col2">Deployment Plan Name</td>
				<td class="col3">Revision</td>
				<td class="col4">Status</td>
				<td class="col5">Created</td>
				<td class="col6"></td>
			</tr>
		</thead>
		</table>
		</div>
		<div class="clr"></div>

	<div  class="entryDetails">
	<div class="ui-tabs">
		<ul class="ui-tabs-nav">
			<li class="ui-state-default ui-corner-top<g:if test="${cookie(name:'autodeploy_view') == 'hostView' }"> ui-tabs-selected ui-state-active</g:if>"><a href="#hostView">HostView</a></li>
			<li class="ui-state-default ui-corner-top<g:if test="${cookie(name:'autodeploy_view') == 'appView' }"> ui-tabs-selected ui-state-active</g:if>"><a href="#appView">AppView</a></li>
		</ul>
	</div>
	<table>
	</table>
	</div>
</div>
</body>
</html>