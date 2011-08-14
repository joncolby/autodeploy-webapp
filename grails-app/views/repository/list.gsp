
<%@ page import="de.mobile.siteops.Repository" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'repository.label', default: 'Repository')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'repository.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'repository.name.label', default: 'Name')}" />
                        
                            <g:sortableColumn property="type" title="${message(code: 'repository.type.label', default: 'Type')}" />
                        
                            <g:sortableColumn property="baseUrl" title="${message(code: 'repository.baseUrl.label', default: 'Base Url')}" />
                        
                            <g:sortableColumn property="dateCreated" title="${message(code: 'repository.dateCreated.label', default: 'Date Created')}" />
                        
                            <g:sortableColumn property="lastUpdated" title="${message(code: 'repository.lastUpdated.label', default: 'Last Updated')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${repositoryInstanceList}" status="i" var="repositoryInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td>${fieldValue(bean: repositoryInstance, field: "id")}</td>
                        
                            <td><g:link action="show" id="${repositoryInstance.id}">${fieldValue(bean: repositoryInstance, field: "name")}</g:link></td>
                        
                            <td>${fieldValue(bean: repositoryInstance, field: "type")}</td>
                        
                            <td>${fieldValue(bean: repositoryInstance, field: "baseUrl")}</td>
                        
                            <td><g:formatDate date="${repositoryInstance.dateCreated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de" /></td>
                        
                            <td><g:formatDate date="${repositoryInstance.lastUpdated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de"  /></td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${repositoryInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
