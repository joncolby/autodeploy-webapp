package de.mobile.siteops

class AutoPlayService {

    static transactional = true

    def autoPlayMap = [:]


    def isEnabled(DeploymentQueue deploymentQueue) {
        return autoPlayMap[deploymentQueue.id] ? true : false
    }

    def enable(DeploymentQueue deploymentQueue) {
        autoPlayMap[deploymentQueue.id] = true
        println "enabling: " + autoPlayMap

    }

    def disable(DeploymentQueue deploymentQueue) {
        autoPlayMap[deploymentQueue.id] = false
        println "disabling: " + autoPlayMap
    }

    def nextQueuedEntry(DeploymentQueue queue) {
        // get next queueEntry, ordered by oldest date
        return DeploymentQueueEntry.queuedEntries(queue).list(sort:"dateCreated",order:"asc", max: 1)
    }

}
