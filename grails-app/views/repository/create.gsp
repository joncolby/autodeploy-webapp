

<%@ page import="de.mobile.siteops.Repository" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'repository.label', default: 'Repository')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/home.html')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>

          <g:render template="/shared/messages" />
          
            <g:hasErrors bean="${repositoryInstance}">
            <div class="errors">
                <g:renderErrors bean="${repositoryInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name"><g:message code="repository.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: repositoryInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${repositoryInstance?.name}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="type"><g:message code="repository.type.label" default="Type" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: repositoryInstance, field: 'type', 'errors')}">
                                    <g:select name="type" from="${repositoryInstance.constraints.type.inList}" value="${repositoryInstance?.type}" valueMessagePrefix="repository.type"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="baseUrl"><g:message code="repository.baseUrl.label" default="Base Url" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: repositoryInstance, field: 'baseUrl', 'errors')}">
                                    <g:textField size="70" name="baseUrl" value="${repositoryInstance?.baseUrl}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
