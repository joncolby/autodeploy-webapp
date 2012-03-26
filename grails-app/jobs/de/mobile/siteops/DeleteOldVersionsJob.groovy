package de.mobile.siteops


class DeleteOldVersionsJob {
    // def timeout = 5000l // execute job once in 5 seconds

    def execute() {
        // execute task

        def environment = Environment.findByName('Integra204')
        DeploymentQueue deploymentQueue = DeploymentQueue.findByEnvironment(environment)

        // get finalized queue entries
        def queueEntries = DeploymentQueueEntry.finalizedEntries(deploymentQueue).list(sort: 'finalizedDate', order: 'desc')

        // loop through each queue entry
        for (DeploymentQueueEntry queueEntry: queueEntries) {

            // get execution plan
            ExecutionPlan plan = queueEntry.executionPlan

            // get latest application versions

            // get app versions
            def appVersions = plan.applicationVersions

            // delete versions older than latest application versions




        }


    }
}
