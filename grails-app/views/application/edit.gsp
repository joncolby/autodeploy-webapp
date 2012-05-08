

<%@ page import="de.mobile.siteops.Application" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'application.label', default: 'Application')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">

        <%-- column --%>
        <div id="container">
        <div id="col_one">

            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:render template="/shared/messages" />
            <g:hasErrors bean="${applicationInstance}">
            <div class="errors">
                <g:renderErrors bean="${applicationInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form name="applicationForm" method="post" >
                <g:hiddenField name="id" value="${applicationInstance?.id}" />
                <g:hiddenField name="version" value="${applicationInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="pillar.id"><g:message code="application.pillar.label" default="Pillar" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'pillar', 'errors')}">
                                    <g:select name="pillar.id" from="${de.mobile.siteops.Pillar.list()}" optionKey="id" value="${applicationInstance?.pillar?.id}"  />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="filename"><g:message code="application.filename.label" default="Filename" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'filename', 'errors')}">
                                    <g:textField size="40" name="filename" value="${applicationInstance?.filename}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="downloadName"><g:message code="application.downloadName.label" default="downloadName" /></label>
                                  <br>(%REV% can be used for revision)
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'downloadName', 'errors')}">
                                    <g:textField size="40" name="downloadName" value="${applicationInstance?.downloadName}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="startStopScript"><g:message code="application.startStopScript.label" default="Start/Stop Script" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'startStopScript', 'errors')}">
                                    <g:textField size="40" name="startStopScript" value="${applicationInstance?.startStopScript}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="installDir"><g:message code="application.installDir.label" default="Install Directory" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'installDir', 'errors')}">
                                    <g:textField size="40" name="installDir" value="${applicationInstance?.installDir}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="description"><g:message code="application.description.label" default="Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'description', 'errors')}">
                                    <g:textArea rows="3" cols="40" name="description" value="${applicationInstance?.description}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="context"><g:message code="application.context.label" default="Context" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'context', 'errors')}">
                                    <g:textField name="context" size="40" value="${applicationInstance?.context}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="releaseInfoJMXBean"><g:message code="application.releaseInfoJMXBean.label" default="Release Info JMX Bean" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'releaseInfoJMXBean', 'errors')}">
                                    <g:textField name="releaseInfoJMXBean" size="40" value="${applicationInstance?.releaseInfoJMXBean}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="releaseInfoJMXAttribute"><g:message code="application.releaseInfoJMXAttribute.label" default="Release Info JMX Attribute" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'releaseInfoJMXAttribute', 'errors')}">
                                    <g:textField name="releaseInfoJMXAttribute" size="40" value="${applicationInstance?.releaseInfoJMXAttribute}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="type"><g:message code="application.type.label" default="Type" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'type', 'errors')}">
                                    <g:select name="type" from="${Application.ApplicationType.values()}" value="${applicationInstance?.type}" valueMessagePrefix="application.type"  />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="type"><g:message code="application.marketPlace.label" default="Marketplace" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'marketPlace', 'errors')}">
                                    <g:select name="marketPlace" from="${Application.MarketPlace.values()}" value="${applicationInstance?.marketPlace}" valueMessagePrefix="application.marketPlace"  />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="type"><g:message code="application.balancerType.label" default="Load Balancer Type" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'balancerType', 'errors')}">
                                    <g:select name="balancerType" from="${de.mobile.siteops.Application.LoadBalancerType.values()}" value="${applicationInstance?.balancerType}" valueMessagePrefix="application.balancerType"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="modulename"><g:message code="application.modulename.label" default="Module Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'modulename', 'errors')}">
                                    <g:textField size="40" name="modulename" value="${applicationInstance?.modulename}" />
                                </td>
                            </tr>
    
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="artifactId"><g:message code="application.artifactId.label" default="Artifact Id" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'artifactId', 'errors')}">
                                    <g:textField name="artifactId" size="40" value="${applicationInstance?.artifactId}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="groupId"><g:message code="application.groupId.label" default="Group Id" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'groupId', 'errors')}">
                                    <g:textField name="groupId" size="30" value="${applicationInstance?.groupId}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="assembleProperties"><g:message code="application.assembleProperties.label" default="Assemble Properties" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'assembleProperties', 'errors')}">
                                    <g:checkBox name="assembleProperties" value="${applicationInstance?.assembleProperties}" />
                                </td>
                            </tr>                        
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="instanceProperties"><g:message code="application.instanceProperties.label" default="Instance Properties" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'instanceProperties', 'errors')}">
                                    <g:checkBox name="instanceProperties" value="${applicationInstance?.instanceProperties}" />
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="propertiesPath"><g:message code="application.propertiesPath.label" default="Properties Directory" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'propertiesPath', 'errors')}">
                                    <g:textField size="40" name="propertiesPath" value="${applicationInstance?.propertiesPath}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="doProbe"><g:message code="application.doProbe.label" default="Probe after deployment" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'doProbe', 'errors')}">
                                    <g:checkBox name="doProbe" value="${applicationInstance?.doProbe}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="startOnDeploy"><g:message code="application.startOnDeploy.label" default="Start On Deploy" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'startOnDeploy', 'errors')}">
                                    <g:checkBox name="startOnDeploy" value="${applicationInstance?.startOnDeploy}" />
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
                   <h3>Select Host Classes</h3>
                  <g:checkBoxList name="hostclasses" form="applicationForm" from="${de.mobile.siteops.HostClass.list(sort:'name')}" value="${applicationInstance?.hostclasses?.collect{it.id}}" optionKey="id" />
                </div>
              </div>
          <%-- column --%>

            </g:form>
        </div>
    </body>
</html>
