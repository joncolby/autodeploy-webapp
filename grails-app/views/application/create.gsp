

<%@ page import="de.mobile.siteops.Application" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'application.label', default: 'Application')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>

        <style type="text/css">
            tbody:nth-child(even) { background: #f5f5f5;  border: solid 1px #ddd; }
        </style>
    </head>
    <body>
        <div class="nav">
           <span class="menuButton"><a class="home" href="${createLink(uri: '/home')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" controller="userAdmin"><g:message code="admin.userAdmin.heading"  /></g:link></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">

        <g:form name="applicationForm" action="save" >

        <%-- column --%>
        <div id="container">
        <div id="col_one">


        <h1><g:message code="default.create.label" args="[entityName]" /></h1>

          <g:render template="/shared/messages" />
          
            <g:hasErrors bean="${applicationInstance}">
            <div class="errors">
                <g:renderErrors bean="${applicationInstance}" as="list" />
            </div>
            </g:hasErrors>



                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="pillar"><g:message code="application.pillar.label" default="Pillar" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'pillar', 'errors')}">
                                    <g:select name="pillar.id" from="${de.mobile.siteops.Pillar.list().sort{it.name}}" optionKey="id" value="${applicationInstance?.pillar?.id}"  />
                                    <br><i>set to </i><b>INACTIVE</b><i> if this app is obsolete</i>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="filename"><g:message code="application.filename.label" default="Filename" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'filename', 'errors')}">
                                    <g:textField name="filename" size="40" value="${applicationInstance?.filename}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="downloadName"><g:message code="application.downloadName.label" default="downloadName" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'downloadName', 'errors')}">
                                    <g:textField name="downloadName" size="40" value="${applicationInstance?.downloadName}" />
                                    <br>%REV% <i>can be used as macro for revision</i>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="startStopScript"><g:message code="application.startStopScript.label" default="Start/Stop Script" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'startStopScript', 'errors')}">
                                    <g:textField name="startStopScript" size="40" value="${applicationInstance?.startStopScript}" />
                                    <br><i>can be empty for tanuki apps</i>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="installDir"><g:message code="application.installDir.label" default="Install Directory" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'installDir', 'errors')}">
                                    <g:textField size="40" name="installDir" value="${applicationInstance?.installDir}" />
                                    <br><i>can be empty for tanuki apps</i>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="description"><g:message code="application.description.label" default="Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'description', 'errors')}">
                                    <g:textArea cols="10" rows="10" name="description" value="${applicationInstance?.description}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="context"><g:message code="application.context.label" default="Webapp Context" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'context', 'errors')}">
                                    <g:textField name="context" size="40" value="${applicationInstance?.context}" />
                                    <br><i>leave empty if this is not a web application</i>
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
                                        <br><i>only used if property file requires site-id</i>
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
                                    <g:textField name="groupId" size="40" value="${applicationInstance?.groupId}" />
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
                                    <br><i>optional</i>
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

                            <tbody>
                            <tr><td colspan="2"><h3>Application Probing Configuration</h3></td> </tr>
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
                                    <label for="probeAuthMethod"><g:message code="application.probeAuthMethod.label" default="Authentication Method" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'probeAuthMethod', 'errors')}">
                                    <g:radioGroup name="probeAuthMethod" labels="['none','basic','digest']" values="['none','basic','digest']" value="${applicationInstance?.probeAuthMethod}" >
                                    <p><g:message code="${it.label}" />: ${it.radio}</p>
                                    </g:radioGroup>
                                    <br><i>optional</i>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="probeAuthUser"><g:message code="application.probeAuthUser.label" default="Basic Authentication User" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'probeAuthUser', 'errors')}">
                                    <g:textField name="probeAuthUser" size="40" value="${applicationInstance?.probeAuthUser}" />
                                    <br><i>(optional) will be used with test url(s)</i>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="probeAuthPassword"><g:message code="application.probeAuthPassword.label" default="Basic Authentication Password" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'probeAuthPassword', 'errors')}">
                                    <g:textField name="probeAuthPassword" size="40" value="${applicationInstance?.probeAuthPassword}" />
                                    <br><i>(optional) will be used with test url(s)</i>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="testUrls"><g:message code="application.testUrls.label" default="Test Urls" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'testUrls', 'errors')}">
                                    <g:textArea cols="10" rows="10" name="testUrls" value="${applicationInstance?.testUrls}" />
                                    <br><i>%CONTEXT% and %REV% macros can be used. Specify each URL on one line.</i>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="verificationJMXBean"><g:message code="application.verificationJMXBean.label" default="Verification JMX Bean" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'verificationJMXBean', 'errors')}">
                                    <g:textField name="verificationJMXBean" size="40" value="${applicationInstance?.verificationJMXBean}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="verificationJMXAttribute"><g:message code="application.verificationJMXAttribute.label" default="Verification JMX Attribute" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: applicationInstance, field: 'verificationJMXAttribute', 'errors')}">
                                    <g:textField name="verificationJMXAttribute" size="40" value="${applicationInstance?.verificationJMXAttribute}" />
                                </td>
                            </tr>
                            </tbody>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
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
