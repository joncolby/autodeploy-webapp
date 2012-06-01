package de.mobile.siteops

class AutoPlayService {

    static transactional = true

    def isEnabled(DeploymentQueue deploymentQueue) {
        def environment = deploymentQueue.environment
        return environment.autoPlayEnabled ? true : false
    }

    def enable(DeploymentQueue deploymentQueue) {
        def environment = deploymentQueue.environment
        environment.autoPlayEnabled = true
    }

    def disable(DeploymentQueue deploymentQueue) {
        def environment = deploymentQueue.environment
        environment.autoPlayEnabled = false
    }

    def nextQueuedEntry(DeploymentQueue queue) {
        // get next queueEntry, ordered by oldest date
        return DeploymentQueueEntry.queuedEntries(queue).list(sort:"dateCreated",order:"asc", max: 1)
    }

}
