

<%@ page import="de.mobile.siteops.Pillar" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'pillar.label', default: 'Pillar')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
           <span class="menuButton"><a class="home" href="${createLink(uri: '/home')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" controller="userAdmin"><g:message code="admin.userAdmin.heading"  /></g:link></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">

            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>

            <g:render template="/shared/messages" /> 

            <g:hasErrors bean="${pillarInstance}">
            <div class="errors">
                <g:renderErrors bean="${pillarInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form name="pillarEditForm" method="post" >
                <g:hiddenField name="id" value="${pillarInstance?.id}" />
                <g:hiddenField name="version" value="${pillarInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                                                                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="name"><g:message code="pillar.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: pillarInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${pillarInstance?.name}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="description"><g:message code="pillar.description.label" default="Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: pillarInstance, field: 'description', 'errors')}">
                                    <g:textArea name="description" value="${pillarInstance?.description}" />
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
