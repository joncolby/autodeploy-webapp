

<%@ page import="de.mobile.siteops.Environment" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'environment.label', default: 'Environment')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>

          <g:render template="/shared/messages" />
          
            <g:hasErrors bean="${environmentInstance}">
            <div class="errors">
                <g:renderErrors bean="${environmentInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${environmentInstance?.id}" />
                <g:hiddenField name="version" value="${environmentInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="name"><g:message code="environment.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: environmentInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${environmentInstance?.name}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="useHostClassConcurrency"><g:message code="environmentInstance.useHostClassConcurrency.label" default="Use Hostclass concurrency" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: environmentInstance, field: 'useHostClassConcurrency', 'errors')}">
                                    <g:checkBox name="useHostClassConcurrency" value="${environmentInstance?.useHostClassConcurrency}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="repository"><g:message code="application.repository.label" default="Repository" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: environmentInstance, field: 'repository', 'errors')}">
                                    <g:select name="repository.id" from="${de.mobile.siteops.Repository.list()}" optionKey="id" value="${environmentInstance?.repository?.id}"  />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="propertyAssembler"><g:message code="application.propertyAssembler.label" default="Property Assembler" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: environmentInstance, field: 'propertyAssembler', 'errors')}">
                                    <g:select name="propertyAssembler.id" from="${de.mobile.siteops.PropertyAssembler.list()}" optionKey="id" value="${environmentInstance?.propertyAssembler?.id}"  />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
