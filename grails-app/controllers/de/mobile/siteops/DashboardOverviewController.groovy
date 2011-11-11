package de.mobile.siteops

class DashboardOverviewController {

    def index = {
        def queueId = params.id

        def queue = DeploymentQueue.get(queueId)
        def queueEntries = DeploymentQueueEntry.dashboard(queue).list(max: 30, sort: "lastUpdated", order: "desc")

        def entries = []
        queueEntries.each { DeploymentQueueEntry entry ->
            def model = [:]
            def plan = entry.executionPlan
            model.id = entry.id
            model.team = plan.team.fullName
            model.contribution = plan.contribution
            model.ticket = plan.ticket
            model.date = entry.lastUpdated.format("dd.MM.yyyy")
            model.time = entry.lastUpdated.format("HH:mm")
            model.state = entry.state
            model.multiplerevs = true
            model.revision = "(multiple)"

            model.apps = plan.applicationVersions.collect { [name: it.application.modulename, revision: it.revision ]}.sort { it.name }
            def revlist = model.apps.collect { it.revision }.unique()
            if (revlist.size() <= 1) {
                model.revision = revlist[0]
                model.multiplerevs = false
            }

            if ([HostStateType.ABORTED, HostStateType.ERROR, HostStateType.CANCELLED].contains(entry.state)) {
                model.stateColor = "error"
            } else if (entry.state == HostStateType.DEPLOYED) {
                model.stateColor = "success"
            } else if (entry.state == HostStateType.IN_PROGRESS) {
                model.stateColor = "working"
            } else if (entry.state == HostStateType.QUEUED) {
                model.stateColor = "queued"
            } else {
                model.stateColor = "default"
            }

            entries += model
        }

        [entries: entries]
    }
}
