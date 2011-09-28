
<%@ page import="de.mobile.siteops.HostClass" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'hostClass.label', default: 'HostClass')}" />
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
                            <td valign="top" class="name"><g:message code="hostClass.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: hostClassInstance, field: "id")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="hostClass.name.label" default="Name" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: hostClassInstance, field: "name")}</td>
                            
                        </tr>


                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="hostClass.concurrency.label" default="Deployment Concurrency" /></td>

                            <td valign="top" class="value">${fieldValue(bean: hostClassInstance, field: "concurrency")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="hostClass.priority.label" default="Deployment Priority" /></td>

                            <td valign="top" class="value">${hostClassInstance.priority}</td>

                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="hostClass.description.label" default="Description" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: hostClassInstance, field: "description")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="hostClass.applications.label" default="Applications" /></td>
                            
                            <td valign="top" style="text-align: left;" class="value">
                                <ul>
                                <g:each in="${hostClassInstance.applications}" var="a">
                                    <li><g:link controller="application" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
                                </g:each>
                                </ul>
                            </td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="hostClass.dateCreated.label" default="Date Created" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${hostClassInstance?.dateCreated}" type="datetime" style="SHORT" timeStyle="SHORT" locale="de"  /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="hostClass.lastUpdated.label" default="Last Updated" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${hostClassInstance?.lastUpdated}" type="datetime" style="SHORT" timeStyle="SHORT" locale="de" /></td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>

            <sec:ifAnyGranted roles="ROLE_ADMIN">
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${hostClassInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
            </sec:ifAnyGranted>

        </div>
    </body>
</html>
