<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
        <title>AutoDeploy User Management</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
  <body>
          <div class="nav">
           <span class="menuButton"><a class="home" href="${createLink(uri: '/home')}"><g:message code="default.home.label"/></a></span>
          <span class="menuButton"><g:link class="list" controller="userAdmin"><g:message code="admin.userAdmin.heading"  /></g:link></span>
        </div>
  <div class="body">
  <h1>AutoDeploy User Management</h1>
  <ul>
      <li><a href="${createLink(controller: 'user')}">User Management</a></li>
      <li><a href="${createLink(controller: 'role')}">Role Management</a></li>
  </ul>
      </div>
  </body>
</html>