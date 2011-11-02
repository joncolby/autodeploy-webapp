
<%@ page import="de.mobile.siteops.Application" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'application.label', default: 'Application')}" />
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
                            <td valign="top" class="name"><g:message code="application.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "id")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.pillar.label" default="Pillar" /></td>
                            
                            <td valign="top" class="value"><g:link controller="pillar" action="show" id="${applicationInstance?.pillar?.id}">${applicationInstance?.pillar?.encodeAsHTML()}</g:link></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.filename.label" default="Filename" /></td>
                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "filename")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.downloadName.label" default="downloadName" /></td>
                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "downloadName")}</td>
                        </tr>


                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.hostclasses.label" default="Host Classes" /></td>

                            <td valign="top" class="value">
                              <ul>
                              <g:each in="${applicationInstance.hostclasses}" var="hc">
                                <li><g:link controller="hostClass" action="show" id="${hc.id}">${hc}</g:link> </li>
                              </g:each>
                              </ul>
                            </td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.description.label" default="Description" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "description")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.context.label" default="Context" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "context")}</td>
                            
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.startStopScript.label" default="Start/Stop Script" /></td>

                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "startStopScript")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.installDir.label" default="Install Directory" /></td>

                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "installDir")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.releaseInfoJMXBean.label" default="Release Info JMX Bean" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "releaseInfoJMXBean")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.releaseInfoJMXAttribute.label" default="Release Info JMX Attribute" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "releaseInfoJMXAttribute")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.type.label" default="Type" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "type")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.marketPlace.label" default="Marketplace" /></td>

                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "marketPlace")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.balancerType.label" default="Load Balancer Type" /></td>

                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "balancerType")}</td>

                        </tr>

                        <%--
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.instances.label" default="Instances" /></td>
                            
                            <td valign="top" style="text-align: left;" class="value">
                                <ul>
                                <g:each in="${applicationInstance.instances}" var="i">
                                    <li><g:link controller="instance" action="show" id="${i.id}">${i?.encodeAsHTML()}</g:link></li>
                                </g:each>
                                </ul>
                            </td>
                            
                        </tr>
                        --%>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.modulename.label" default="Module Name" /></td>

                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "modulename")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.artifactId.label" default="Artifact Id" /></td>

                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "artifactId")}</td>

                        </tr>


                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.groupId.label" default="Group Id" /></td>

                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "groupId")}</td>

                        </tr>


                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.assembleProperties.label" default="Assemble Properties" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${applicationInstance?.assembleProperties}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.dateCreated.label" default="Date Created" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${applicationInstance?.dateCreated}" /></td>
                            
                        </tr>                    
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.instanceProperties.label" default="Instance Properties" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${applicationInstance?.instanceProperties}" /></td>
                            
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.propertiesPath.label" default="Properties Directory" /></td>

                            <td valign="top" class="value">${fieldValue(bean: applicationInstance, field: "propertiesPath")}</td>

                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.lastUpdated.label" default="Last Updated" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${applicationInstance?.lastUpdated}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.doProbe.label" default="Probe after deployment" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${applicationInstance?.doProbe}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="application.startOnDeploy.label" default="Start On Deploy" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${applicationInstance?.startOnDeploy}" /></td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <sec:ifAnyGranted roles="ROLE_ADMIN">
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${applicationInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
            </sec:ifAnyGranted>
        </div>
    </body>
</html>
