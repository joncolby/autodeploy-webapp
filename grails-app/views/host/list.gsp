
<%@ page import="de.mobile.siteops.Environment; de.mobile.siteops.Host" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'host.label', default: 'Host')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'/')}"><g:message code="default.home.label"/></a></span>

          <sec:ifAnyGranted roles="ROLE_ADMIN">
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
          </sec:ifAnyGranted>

        </div>
        <div class="body">

           <%-- start --%>

              <div id="wide_container">
                <div id="wide_col_one">

            <h1>${environmentInstance} <g:message code="default.list.label" args="[entityName]" /></h1>

          <g:render template="/shared/messages" />
          
   
         
           <g:form action="search" name="search">
              <p><b>Find by hostname:</b> <g:textField name="hostname"></g:textField></p>
            </g:form>  

          <br>

            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'host.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'host.name.label', default: 'Name')}" />
                        
                            <th><g:message code="host.environment.label" default="Environment" /></th>

                            <th><g:message code="host.className.label" default="Host Class" /></th>

                            <%-- <g:sortableColumn property="dateCreated" title="${message(code: 'host.dateCreated.label', default: 'Date Created')}" /> --%>
                        
                            <g:sortableColumn property="lastUpdated" title="${message(code: 'host.lastUpdated.label', default: 'Last Updated')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${hostInstanceList}" status="i" var="hostInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${hostInstance.id}">${fieldValue(bean: hostInstance, field: "id")}</g:link></td>
                        
                            <td><g:link action="show" id="${hostInstance.id}">${fieldValue(bean: hostInstance, field: "name")}</g:link></td>
                        
                            <td>${fieldValue(bean: hostInstance, field: "environment")}</td>                 

                            <td><g:link action="show" controller="hostClass" id="${hostInstance?.className?.id}">${fieldValue(bean: hostInstance, field: "className")}</g:link></td>

                            <%-- <td><g:formatDate date="${hostInstance.dateCreated}" /></td> --%>
                        
                            <td><g:formatDate date="${hostInstance.lastUpdated}" type="datetime" style="SHORT" timeStyle="SHORT" locale="de" /></td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>




            <div class="paginateButtons">
                <g:paginate total="${hostInstanceTotal}" params="[hostname:hostname, id: environmentInstance?.id]" />
            </div>


              <%-- column two --%>
                </div>
                <div id="wide_col_two">
                   <h3>Filter by Env</h3>
                  <ul>
                  <li><g:link action="list">[SHOW ALL]</g:link></li>                    
                  <g:each in="${Environment.list()}" var="env" >
                  <li><g:link action="listByEnv" id="${env.id}">[${env}]</g:link></li>
                  </g:each>
                  </ul>

                </div>
              </div>

              <%-- end --%>

        </div>
    </body>
</html>
