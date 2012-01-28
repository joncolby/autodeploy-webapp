package de.mobile.siteops

class ReleaseMailService {

    static transactional = false

    def releaseMail(queueEntryId) {
        def queueEntry = DeploymentQueueEntry.load(queueEntryId)
        println queueEntry.executionPlan.name
    }
}
