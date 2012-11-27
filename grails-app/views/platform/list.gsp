
<%@ page import="de.mobile.siteops.Platform" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'platform.label', default: 'Platform')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
           <span class="menuButton"><a class="home" href="${createLink(uri: '/home')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" controller="userAdmin"><g:message code="admin.userAdmin.heading"  /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'platform.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'platform.name.label', default: 'Name')}" />
                        
                            <g:sortableColumn property="description" title="${message(code: 'platform.description.label', default: 'Description')}" />
                        
                            <g:sortableColumn property="dateCreated" title="${message(code: 'platform.dateCreated.label', default: 'Date Created')}" />
                        
                            <g:sortableColumn property="lastUpdated" title="${message(code: 'platform.lastUpdated.label', default: 'Last Updated')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${platformInstanceList}" status="i" var="platformInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${platformInstance.id}">${fieldValue(bean: platformInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: platformInstance, field: "name")}</td>
                        
                            <td>${fieldValue(bean: platformInstance, field: "description")}</td>
                        
                            <td><g:formatDate date="${platformInstance.dateCreated}" /></td>
                        
                            <td><g:formatDate date="${platformInstance.lastUpdated}" /></td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${platformInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
