package de.mobile.siteops

class AutoPlayService {

    static transactional = true

    def autoPlayMap = new HashMap<DeploymentQueue, Boolean>()

    def toggleAutoPlay(DeploymentQueue deploymentQueue) {

    }

    def getNextQueueEntry(DeploymentQueue deploymentQueue) {
        // call named query
        // get one result, ordered by oldest date

    }

}
