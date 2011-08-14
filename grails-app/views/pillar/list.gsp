
<%@ page import="de.mobile.siteops.Pillar" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'pillar.label', default: 'Pillar')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>

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
                        
                            <g:sortableColumn property="id" title="${message(code: 'pillar.id.label', default: 'Id')}" />

                            <g:sortableColumn property="name" title="${message(code: 'pillar.name.label', default: 'Name')}" />                            
                        
                            <g:sortableColumn property="description" title="${message(code: 'pillar.description.label', default: 'Description')}" />
                        
                            <g:sortableColumn property="dateCreated" title="${message(code: 'pillar.dateCreated.label', default: 'Date Created')}" />
                        
                            <g:sortableColumn property="lastUpdated" title="${message(code: 'pillar.lastUpdated.label', default: 'Last Updated')}" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${pillarInstanceList}" status="i" var="pillarInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td>${fieldValue(bean: pillarInstance, field: "id")}</td>

                            <td><g:link action="show" id="${pillarInstance.id}">${fieldValue(bean: pillarInstance, field: "name")}</g:link></td>
                        
                            <td>${fieldValue(bean: pillarInstance, field: "description")}</td>
                        
                            <td><g:formatDate date="${pillarInstance.dateCreated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de" /></td>
                        
                            <td><g:formatDate date="${pillarInstance.lastUpdated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de" /></td>
                        

                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${pillarInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
