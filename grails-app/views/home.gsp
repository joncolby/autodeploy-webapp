<html>
    <head>
        <title>Welcome to Grails</title>
        <meta name="layout" content="main" />
        <style type="text/css" media="screen">

        #nav {
            margin-top:20px;
            margin-left:30px;
            width:228px;
            float:left;

        }
        .homePagePanel * {
            margin:0px;
        }
        .homePagePanel .panelBody ul {
            list-style-type:none;
            margin-bottom:10px;
        }
        .homePagePanel .panelBody h1 {
            text-transform:uppercase;
            font-size:1.1em;
            margin-bottom:10px;
        }
        .homePagePanel .panelBody {
            background: url(images/leftnav_midstretch.png) repeat-y top;
            margin:0px;
            padding:15px;
        }
        .homePagePanel .panelBtm {
            background: url(images/leftnav_btm.png) no-repeat top;
            height:20px;
            margin:0px;
        }

        .homePagePanel .panelTop {
            background: url(images/leftnav_top.png) no-repeat top;
            height:11px;
            margin:0px;
        }
        h2 {
            margin-top:15px;
            margin-bottom:15px;
            font-size:1.2em;
        }
        #pageBody {
            margin-left:280px;
            margin-right:20px;
        }
        </style>
    </head>
    <body>

      <div class="nav">
           <span class="menuButton"><a class="home" href="${createLink(uri: '/home')}"><g:message code="default.home.label"/></a></span>
          <span class="menuButton"><g:link class="list" controller="userAdmin"><g:message code="admin.userAdmin.heading"  /></g:link></span>
        </div>

        <div id="nav">
            <h1>Autodeploy Admin</h1>

            <div id="controllerList" class="dialog">
                <h2>Actions:</h2>
                <ul>
                        <li class="controller"><g:link controller="deploymentOverview" action="index">Deployment Overview</g:link>
                </ul>

                <h2>Inventory:</h2>
                <ul>
                        <li><g:link controller="team" action="list">Teams</g:link></li>
                        <li><g:link controller="host" action="list">Hosts</g:link></li>
                        <li><g:link controller="application" action="list">Applications</g:link></li>
                        <li><g:link controller="hostClass" action="list">Host Classes</g:link></li>
                        <li><g:link controller="pillar" action="list">Pillars</g:link></li>
                        <li><g:link controller="environment" action="list">Environments</g:link></li>
                        <li><g:link controller="repository" action="list">Repositories</g:link></li>
                        <li><g:link controller="propertyAssembler" action="list">Property Assemblers</g:link></li>
                        <li><g:link controller="platform" action="list">Platform</g:link></li>
                </ul>
         
            </div>
        </div>
    </body>
</html>
