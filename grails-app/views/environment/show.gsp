
<%@ page import="de.mobile.siteops.Environment" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'environment.label', default: 'Environment')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/home.html')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>

            <sec:ifAnyGranted roles="ROLE_ADMIN">
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
            </sec:ifAnyGranted>
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>

          <g:render template="/shared/messages" />
          
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="environment.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: environmentInstance, field: "id")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="environment.name.label" default="Name" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: environmentInstance, field: "name")}</td>
                            
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="environmentInstance.useHostClassConcurrency.label" default="Use Hostclass concurrency" /></td>

                            <td valign="top" class="value"><g:formatBoolean boolean="${environmentInstance?.useHostClassConcurrency}" /></td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="environment.repository.label" default="Repository" /></td>

                            <td valign="top" class="value"><g:link action="show" controller="repository" id="${environmentInstance?.repository?.id}">${fieldValue(bean: environmentInstance, field: "repository")}</g:link></td>

                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="environment.dateCreated.label" default="Date Created" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${environmentInstance?.dateCreated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de"  /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="environment.lastUpdated.label" default="Last Updated" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${environmentInstance?.lastUpdated}" type="datetime" style="SHORT" timeStyle="MEDIUM" locale="de" /></td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
      <sec:ifAnyGranted roles="ROLE_ADMIN">
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${environmentInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
      </sec:ifAnyGranted>

        </div>
    </body>
</html>
