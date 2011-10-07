<%--
  Created by IntelliJ IDEA.
  User: ibartel
  Date: 05.10.11
  Time: 14:34
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head><title>Current available users</title></head>
<body>

<table>
    <thead>
        <tr>
            <td>Username</td>
            <td>Role</td>
            <td></td>
        </tr>
    </thead>
    <tbody>
    <g:each in="${model.users}" var="user">
        <tr>
            <td>${user.name}</td>
            <td>${user.roles}</td>
            <td><g:link action="remove" id="${user.entry.id}">remove</g:link></td>
        </tr>
    </g:each>
    </tbody>
</table>


</body>
</html>