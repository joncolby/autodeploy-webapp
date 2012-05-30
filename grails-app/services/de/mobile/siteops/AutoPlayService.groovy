package de.mobile.siteops

class AutoPlayService {

    static transactional = true

    def autoPlayMap = new HashMap<DeploymentQueue, Boolean>()

    def toggleAutoPlay(DeploymentQueue deploymentQueue) {

    }

    def nextQueueEntry(DeploymentQueue queue) {
        // get next queueEntry result, ordered by oldest date
        return DeploymentQueueEntry.queuedEntries(queue).list(sort:"dateCreated",order:"asc", max: 1)
    }

}
