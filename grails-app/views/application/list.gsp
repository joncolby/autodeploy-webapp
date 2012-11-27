
<%@ page import="de.mobile.siteops.Application" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'application.label', default: 'Application')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'application.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="filename" title="${message(code: 'application.filename.label', default: 'Filename')}" />
                        
                            <%-- <g:sortableColumn property="alias" title="${message(code: 'application.alias.label', default: 'Alias')}" /> --%>

                            <th><g:message code="application.pillar.label" default="Pillar" /></th>

                            <g:sortableColumn property="description" title="${message(code: 'application.description.label', default: 'Description')}" />
                        
                            <th>Host Classes</th>
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${applicationInstanceList}" status="i" var="applicationInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td>${fieldValue(bean: applicationInstance, field: "id")}</td>

                            <td><g:link action="show" id="${applicationInstance.id}">${fieldValue(bean: applicationInstance, field: "filename")}</g:link></td>
                        
                            <%-- <td>${fieldValue(bean: applicationInstance, field: "alias")}</td> --%>

                            <td>${fieldValue(bean: applicationInstance, field: "pillar")}</td>

                            <td>${fieldValue(bean: applicationInstance, field: "description")}</td>
                        
                            <td> 
                                <g:concatList from="${applicationInstance.hostclasses}" />
                            </td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${applicationInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
