package de.mobile.siteops

class DashboardOverviewController {

    private static int PAGE_SIZE = 20

    def index = {
        def queueId = params.id
        def queue
        if (!queueId) {
            def prodEnv = Environment.findByName('Production')
            if (prodEnv) {
                queue = DeploymentQueue.findByEnvironment(prodEnv)
            }
        } else {
            queue = DeploymentQueue.get(queueId)
        }

        def model = [:]
        if (queue) {
            model.queue = queue
            model.queueId = queue.id
        }
        [model: model]
    }

    def dashboard = {
        def queueId = params.id

        def queue = DeploymentQueue.get(queueId)
        def total = DeploymentQueueEntry.dashboard(queue).count()
        def pages = (total / PAGE_SIZE) as int

        def queueEntries = DeploymentQueueEntry.dashboard(queue).list(max: 150, sort: "lastUpdated", order: "desc")

        def entries = []
        queueEntries.each { DeploymentQueueEntry entry ->
            def model = [:]
            def plan = entry.executionPlan
            model.id = entry.id
            model.team = plan.team.fullName
            model.contribution = plan.contribution
            model.databaseChanges = plan.databaseChanges
            model.ticket = plan.ticket
            model.date = entry.lastUpdated.format("dd.MM.yyyy")
            model.time = entry.lastUpdated.format("HH:mm")
            model.state = entry.state
            model.multiplerevs = true
            model.revision = "(multiple)"
            model.user = entry.executionPlan.user

            model.apps = plan.applicationVersions.collect { [name: it.application.modulename, pillar: it.application.pillar.name, revision: it.revision ]}.sort { it.name }
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

        [entries: entries, pages: pages]
    }
}
