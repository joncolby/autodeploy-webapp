
<%@ page import="de.mobile.siteops.HostClass" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'hostClass.label', default: 'HostClass')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'/')}"><g:message code="default.home.label"/></a></span>

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
                        
                            <g:sortableColumn property="id" title="${message(code: 'hostClass.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'hostClass.name.label', default: 'Name')}" />

                            <g:sortableColumn property="description" title="${message(code: 'hostClass.description.label', default: 'Description')}" />
                        
                            <g:sortableColumn property="dateCreated" title="${message(code: 'hostClass.dateCreated.label', default: 'Date Created')}" />
                        
                            <g:sortableColumn property="lastUpdated" title="${message(code: 'hostClass.lastUpdated.label', default: 'Last Updated')}" />

                            <th>Number of Applications</th>

                            <th>Concurrency</th>

                            <th>Deployment Priority</th>
                          
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${hostClassInstanceList}" status="i" var="hostClassInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td>${fieldValue(bean: hostClassInstance, field: "id")}</td>
                        
                            <td><g:link action="show" id="${hostClassInstance.id}">${fieldValue(bean: hostClassInstance, field: "name")}</g:link></td>

                            <td>${fieldValue(bean: hostClassInstance, field: "description")}</td>
                        
                            <td><g:formatDate date="${hostClassInstance.dateCreated}" type="datetime" style="SHORT" timeStyle="SHORT" locale="de" /></td>
                        
                            <td><g:formatDate date="${hostClassInstance.lastUpdated}" type="datetime" style="SHORT" timeStyle="SHORT" locale="de" /></td>

                            <td>${hostClassInstance.applications.size()}</td>

                            <td>${hostClassInstance.concurrency}</td>

                            <td>${hostClassInstance.priority}</td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${hostClassInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
