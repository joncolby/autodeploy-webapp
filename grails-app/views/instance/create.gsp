

<%@ page import="de.mobile.siteops.Instance" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'instance.label', default: 'Instance')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">

        <g:form name="instanceForm" action="save" >
         <g:hiddenField name="host.id" value="${hostInstance?.id}" />
        <%-- column --%>
        <div id="container">
        <div id="col_one">


            <h1><g:message code="default.create.label" args="[entityName]" /> on Host ${hostInstance} in ${hostInstance?.environment}</h1>

          <g:render template="/shared/messages" />
          
            <g:hasErrors bean="${instanceInstance}">
            <div class="errors">
                <g:renderErrors bean="${instanceInstance}" as="list" />
            </div>
            </g:hasErrors>

                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name"><g:message code="instance.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: instanceInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${instanceInstance?.name}" />
                                </td>
                            </tr>

                          <%--
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="host"><g:message code="instance.host.label" default="Host" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: instanceInstance, field: 'host', 'errors')}">
                                    <g:select name="host.id" from="${de.mobile.siteops.Host.list()}" optionKey="id" value="${instanceInstance?.host?.id}"  />
                                </td>
                            </tr>
                          --%>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>


          <%-- column --%>
                </div>
                <div id="col_two">
                  <h3>Select Applications</h3>
                  <g:checkBoxList name="applications" form="instanceForm" from="${hostInstance?.className?.applications}" value="${instanceInstance?.applications?.collect{it.id}}" optionKey="id"/>
                </div>
              </div>
          <%-- column --%>
                                <%-- de.mobile.siteops.Application.list() --%>

            </g:form>
        </div>
    </body>
</html>
