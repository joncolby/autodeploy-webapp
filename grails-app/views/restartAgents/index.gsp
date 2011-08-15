<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" type="text/css"
          href="${resource(dir: 'css', file: 'ui-lightness/jquery-ui-1.8.14.custom.css')}"/>
    <link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'jquery.gritter.css')}"/>
    <script src="${resource(dir: 'js', file: 'jquery-1.6.min.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'jquery-ui-1.8.14.custom.min.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'jquery.dimensions.min.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'jquery.tooltip.min.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'jquery.periodicalupdater.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'jquery.gritter.min.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'restartAgents.js')}" type="text/javascript"></script>
    <link rel="stylesheet"
          href="${resource(dir: 'css', file: 'restartAgents.css')}"/>
    <title>Restart Agents</title>
</head>

<body>
<div class="wrapper">
    <div class="header">
    </div>
    <div class="chooseEnv">
        <form name="restartEnv" action="<g:createLink controller="restartAgents" action="restart"/>">
            <label>Restart Environment: </label>
            <select id="selectEnvId">
            <option value="0">--- select ---</option>
            <g:each in="${model.queues}" var="queue">
                <option value="${queue.id}">${queue.name}</option>
            </g:each>
            </select>
        </form>
    </div>
    <div class="restartStatus ui-widget-content">
        <div class="statusInformation">
            Current status:
        </div>
        <div class="statusTable" pollUrl="<g:createLink controller="restartAgents" action="status"/>">
        <table>
        <thead>
            <tr class="ui-widget-header">
                <td class="col1">Host</td>
                <td class="col2">Status</td>
                <td class="col3">Message</td>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Host1</td>
                <td>IN_PROGRESS</td>
                <td>Statusmessage</td>
            </tr>
            <tr>
                <td>Host2</td>
                <td>IN_PROGRESS</td>
                <td>Statusmessage</td>
            </tr>
        </tbody>
        </table>
        </div>
        <div class="clr"></div>
    </div>
</body>
</html>