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

    def nextQueuedEntry(DeploymentQueueEntry entry, DeploymentQueue queue) {
        return DeploymentQueueEntry.queuedEntries(queue,entry.dateCreated).list(sort:"dateCreated",order:"asc", max: 1)
    }

}
