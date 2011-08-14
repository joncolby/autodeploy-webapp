
<%@ page import="de.mobile.siteops.Instance" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'instance.label', default: 'Instance')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <%-- <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span> --%>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>

          <g:render template="/shared/messages" />
          
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'instance.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'instance.name.label', default: 'Name')}" />
                        
                            <th><g:message code="instance.host.label" default="Host" /></th>

                            <th># Apps</th>
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${instanceInstanceList}" status="i" var="instanceInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td>${fieldValue(bean: instanceInstance, field: "id")}</td>
                        
                            <td><g:link action="show" id="${instanceInstance.id}">${fieldValue(bean: instanceInstance, field: "name")}</g:link></td>
                        
                            <td><g:link controller="host" action="show" id="${instanceInstance?.host?.id}">${fieldValue(bean: instanceInstance, field: "host")}</g:link></td>

                            <td>${instanceInstance.applications.size()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${instanceInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
