Hi *,

this is the approval for the ${team} features "${planName}". The approval has been done in ${revision}.
The following pillars/applications are affected by this contribution:

<g:each in="${applications}" var="app">
${app.name}<g:if test="${multipleRevs}"> (revision: ${app.revision})</g:if>
</g:each>

Changes:
${contribution}

More information can be found in our Q-Gate ticket ${ticket}.
