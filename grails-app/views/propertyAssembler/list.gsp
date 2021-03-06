
<%@ page import="de.mobile.siteops.PropertyAssembler" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'propertyAssembler.label', default: 'PropertyAssembler')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'propertyAssembler.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'propertyAssembler.name.label', default: 'Name')}" />
                        
                            <g:sortableColumn property="configAssemblerUrl" title="${message(code: 'propertyAssembler.configAssemblerUrl.label', default: 'Config Assembler Url')}" />
                        
                            <g:sortableColumn property="dateCreated" title="${message(code: 'propertyAssembler.dateCreated.label', default: 'Date Created')}" />
                        
                            <g:sortableColumn property="lastUpdated" title="${message(code: 'propertyAssembler.lastUpdated.label', default: 'Last Updated')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${propertyAssemblerInstanceList}" status="i" var="propertyAssemblerInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${propertyAssemblerInstance.id}">${fieldValue(bean: propertyAssemblerInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: propertyAssemblerInstance, field: "name")}</td>
                        
                            <td>${fieldValue(bean: propertyAssemblerInstance, field: "configAssemblerUrl")}</td>
                        
                            <td><g:formatDate date="${propertyAssemblerInstance.dateCreated}" /></td>
                        
                            <td><g:formatDate date="${propertyAssemblerInstance.lastUpdated}" /></td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${propertyAssemblerInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
