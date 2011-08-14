

<%@ page import="de.mobile.siteops.Host; de.mobile.siteops.Instance" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'instance.label', default: 'Instance')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">

        <%-- column --%>
        <div id="container">
        <div id="col_one">

            <h1><g:message code="default.edit.label" args="[entityName]" /> on Host ${instanceInstance?.host} in ${instanceInstance?.host?.environment}</h1>

  <g:render template="/shared/messages" />
  
            <g:hasErrors bean="${instanceInstance}">
            <div class="errors">
                <g:renderErrors bean="${instanceInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form name="instanceForm" method="post" >
                <g:hiddenField name="id" value="${instanceInstance?.id}" />
                <g:hiddenField name="version" value="${instanceInstance?.version}" />
                <g:hiddenField name="host.id" value="${instanceInstance?.host?.id}" />
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
                                                
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>

          <%-- column --%>
                </div>
                <div id="col_two">
                  <h3>Select Applications</h3>
                  <g:checkBoxList name="applications" form="instanceForm" from="${instanceInstance?.host?.className?.applications}" value="${instanceInstance?.applications?.collect{it.id}}" optionKey="id"/>
                </div>
              </div>
          <%-- column --%>


            </g:form>
        </div>
    </body>
</html>
