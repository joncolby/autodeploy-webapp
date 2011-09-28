
<%@ page import="de.mobile.siteops.Repository" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'repository.label', default: 'Repository')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/home.html')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>

          <g:render template="/shared/messages" />
          
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="repository.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: repositoryInstance, field: "id")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="repository.name.label" default="Name" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: repositoryInstance, field: "name")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="repository.type.label" default="Type" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: repositoryInstance, field: "type")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="repository.baseUrl.label" default="Base Url" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: repositoryInstance, field: "baseUrl")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="repository.dateCreated.label" default="Date Created" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${repositoryInstance?.dateCreated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="repository.lastUpdated.label" default="Last Updated" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${repositoryInstance?.lastUpdated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de" /></td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>

            <sec:ifAnyGranted roles="ROLE_ADMIN">
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${repositoryInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
            </sec:ifAnyGranted>

        </div>
    </body>
</html>
