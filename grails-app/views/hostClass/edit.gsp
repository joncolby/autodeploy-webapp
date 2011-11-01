

<%@ page import="de.mobile.siteops.HostClass" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'hostClass.label', default: 'HostClass')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/home.html')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">

        <%-- column --%>
        <div id="container">
        <div id="col_one">


            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>

            <g:render template="/shared/messages" />
 
            <g:hasErrors bean="${hostClassInstance}">
            <div class="errors">
                <g:renderErrors bean="${hostClassInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form name="hostClass" method="post" >
                <g:hiddenField name="id" value="${hostClassInstance?.id}" />
                <g:hiddenField name="version" value="${hostClassInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="name"><g:message code="hostClass.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: hostClassInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${hostClassInstance?.name}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="concurrency"><g:message code="hostClass.concurrency.label" default="Deployment Concurrency" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: hostClassInstance, field: 'concurrency', 'errors')}">
                                    <g:select name="concurrency" from="${1..100}" value="${hostClassInstance?.concurrency}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="priority"><g:message code="hostClass.priority.label" default="Priority" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: hostClassInstance, field: 'priority', 'errors')}">
                                  <g:prioritySelect id="${hostClassInstance?.id}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="description"><g:message code="hostClass.description.label" default="Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: hostClassInstance, field: 'description', 'errors')}">
                                    <g:textArea name="description" value="${hostClassInstance?.description}" />
                                </td>
                            </tr>
                                                
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>


          </div>
          <%-- column --%>
                <div id="col_two">
                   <h3>Select Applications</h3>
                  <g:checkBoxList name="applications" from="${de.mobile.siteops.Application.list(sort:'filename')}" value="${hostClassInstance?.applications?.collect{it.id}}" optionKey="id"/>
                </div>
              </div>
          <%-- column --%>


            </g:form>
        </div>
    </body>
</html>
