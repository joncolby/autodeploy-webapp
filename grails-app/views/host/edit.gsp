        

<%@ page import="de.mobile.siteops.Host" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'host.label', default: 'Host')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">

           <%--

           <div id="container">
           <div id="col_one">

            --%>

            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
<g:render template="/shared/messages" />
            <g:hasErrors bean="${hostInstance}">
            <div class="errors">
                <g:renderErrors bean="${hostInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form name="hostEdit" method="post" >
                <g:hiddenField name="id" value="${hostInstance?.id}" />
                <g:hiddenField name="version" value="${hostInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="name"><g:message code="host.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: hostInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${hostInstance?.name}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="className"><g:message code="application.className.label" default="Host Class" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: hostInstance, field: 'className', 'errors')}">
                                    <g:select name="className.id" from="${de.mobile.siteops.HostClass.list([sort: 'name'])}" optionKey="id" value="${hostInstance?.className?.id}"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="environment"><g:message code="host.environment.label" default="Environment" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: hostInstance, field: 'environment', 'errors')}">
                                    <g:select name="environment.id" from="${de.mobile.siteops.Environment.list()}" optionKey="id" value="${hostInstance?.environment?.id}"  />
                                </td>
                            </tr>
                                                
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>

                <%--
                </div>
                <div id="col_two">
                  <h3>Select Applications</h3>
                  <g:checkBoxList name="applications" form="hostEdit" from="${de.mobile.siteops.Application.list()}" value="${hostInstance?.applications?.collect{it.id}}" optionKey="id"/>
                </div>

                </div>

              --%>
            </g:form>
        </div>
    </body>
</html>
