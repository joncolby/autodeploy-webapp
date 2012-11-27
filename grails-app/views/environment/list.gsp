
<%@ page import="de.mobile.siteops.Environment" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'environment.label', default: 'Environment')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
           <span class="menuButton"><a class="home" href="${createLink(uri: '/home')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" controller="userAdmin"><g:message code="admin.userAdmin.heading"  /></g:link></span>
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'environment.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'environment.name.label', default: 'Name')}" />

                            <th>Repository</th>

                            <th>Property Assembler</th>

                            <g:sortableColumn property="dateCreated" title="${message(code: 'environment.dateCreated.label', default: 'Date Created')}" />
                        
                            <g:sortableColumn property="lastUpdated" title="${message(code: 'environment.lastUpdated.label', default: 'Last Updated')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${environmentInstanceList}" status="i" var="environmentInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td>${fieldValue(bean: environmentInstance, field: "id")}</td>
                        
                            <td><g:link action="show" id="${environmentInstance.id}">${fieldValue(bean: environmentInstance, field: "name")}</g:link></td>

                            <td>${fieldValue(bean: environmentInstance, field: "repository")}</td>

                            <td>${fieldValue(bean: environmentInstance, field: "propertyAssembler")}</td>

                            <td><g:formatDate date="${environmentInstance.dateCreated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de" /></td>
                        
                            <td><g:formatDate date="${environmentInstance.lastUpdated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de"  /></td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${environmentInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
