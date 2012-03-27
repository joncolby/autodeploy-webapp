package de.mobile.siteops


class DeleteOldVersionsJob {
    // def timeout = 5000l // execute job once in 5 seconds

    def concurrent = false

    def applicationService

    static triggers = {
       //cron name: 'poller', startDelay: 10000, cronExpression: "0 0/60 * * * ?"
       simple name:'initQueueEntryDelete', startDelay:10000, repeatCount: 1
       cron name: 'queueEntryDelete', startDelay: 10000, cronExpression: "0 0 12 ? * SUN"
    }

    def execute() {
        // execute task

        log.info "starting cleanup job"

        def environment = Environment.findByName('Production')

        def queueEntriesForDeletion = applicationService.queueEntriesForDeletion(environment)

        queueEntriesForDeletion.each {
            def entry = DeploymentQueueEntry.get(it)
            def deployedHosts = DeployedHost.findAllByEntry(entry)

            deployedHosts.each { DeployedHost deployedHost ->
                deployedHost.delete()
            }

            entry.delete()
        }

        log.info "cleanup job finished"

    }
}
