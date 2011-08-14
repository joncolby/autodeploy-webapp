<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="Grails" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="stylesheet" href="${resource(dir:'css',file:'autodeploy.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
        <g:javascript library="application" />
        <g:javascript library="autodeploy" />
    </head>
    <body>

        <div style="float: right;padding-right: 100px;margin-top: 20px;">
          <sec:ifNotLoggedIn>
            <g:link controller="login">Login</g:link>
          </sec:ifNotLoggedIn>
          <sec:ifLoggedIn>
          Logged in as <sec:username /> (<g:link controller="logout">Logout</g:link>)
          </sec:ifLoggedIn>
        </div>

      <div id="spinner" class="spinner" style="display:none;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
        </div>
        <%-- <div id="grailsLogo"><a href="http://grails.org"><img src="${resource(dir:'images',file:'grails_logo.png')}" alt="Grails" border="0" /></a></div> --%>
        <div class="minorHeading"><a href="${resource(dir:'/')}">mobile.de</a></div>
        <div class="mainHeading"><a href="${resource(dir:'/')}">Autodeploy</a></div>
        <g:layoutBody />
    </body>
</html>