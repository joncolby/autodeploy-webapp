
<%@ page import="de.mobile.siteops.Team" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'team.label', default: 'Team')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/home.html')}"><g:message code="default.home.label"/></a></span>

            <sec:ifAnyGranted roles="ROLE_ADMIN">
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
            </sec:ifAnyGranted>

        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>

          <g:render template="/shared/messages" />
          
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'team.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="shortName" title="${message(code: 'team.shortName.label', default: 'Short Name')}" />
                        
                            <g:sortableColumn property="fullName" title="${message(code: 'team.fullName.label', default: 'Full Name')}" />
                        
                            <g:sortableColumn property="description" title="${message(code: 'team.description.label', default: 'Description')}" />
                        
                            <g:sortableColumn property="dateCreated" title="${message(code: 'team.dateCreated.label', default: 'Date Created')}" />
                        
                            <g:sortableColumn property="lastUpdated" title="${message(code: 'team.lastUpdated.label', default: 'Last Updated')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${teamInstanceList}" status="i" var="teamInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${teamInstance.id}">${fieldValue(bean: teamInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: teamInstance, field: "shortName")}</td>
                        
                            <td>${fieldValue(bean: teamInstance, field: "fullName")}</td>
                        
                            <td>${fieldValue(bean: teamInstance, field: "description")}</td>
                        
                            <td><g:formatDate date="${teamInstance.dateCreated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de" /></td>
                        
                            <td><g:formatDate date="${teamInstance.lastUpdated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de" /></td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${teamInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
