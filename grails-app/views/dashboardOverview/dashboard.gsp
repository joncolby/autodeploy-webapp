<div class="container">
    <table class="dashboard">
        <tbody>
        <tr>
            <th width="60px">ID</th>
            <th width="85px">Status</th>
            <th width="130px">Last updated</th>
            <th width="100px">Team</th>
            <th width="250px">Contribution</th>
            <th width="100px">Revision</th>
            <th width="200px">Applications</th>
            <th width="80px">Ticket</th>
        </tr>
        <g:each in="${entries}" var="entry">
        <tr>
            <td>${entry.id}</td>
            <td class="center"><span class="${entry.stateColor}">${entry.state}</span></td>
            <td class="center">${entry.date} ${entry.time}</td>
            <td class="center">${entry.team}</td>
            <td>${entry.contribution}</td>
            <td>
                <div class="revision">${entry.revision}</div>
            </td>
            <td>
                <div class="apps">
                <ul>
                    <g:each in="${entry.apps}" var="app">
                    <li>${app.name} (${app.pillar}) <g:if test="${entry.multiplerevs}"><br/><strong>${app.revision}</strong></g:if></li>
                    </g:each>
                </ul>
                </div>
            </td>
            <td class="center"><a href="https://jira.corp.mobile.de/jira/browse/${entry.ticket}" target="_blank">${entry.ticket}</a></td>
        </tr>
        </g:each>
        </tbody>
    </table>
</div>
