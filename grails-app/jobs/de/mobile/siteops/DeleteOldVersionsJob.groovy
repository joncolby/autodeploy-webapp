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

        def days = 30
        def minutes = 60 * 24 * days
        def c = Calendar.getInstance()
        c.add(Calendar.MINUTE, -minutes)
        def oldDate = c.getTime()

        log.info "starting cleanup job"

        Environment.list().each { Environment environment ->

                log.info "starting cleanup for environment ${environment.name} (id ${environment.id})"

                def queueEntriesForDeletion = applicationService.queueEntriesForDeletion(environment)

                for ( id in queueEntriesForDeletion ) {

                    def entry = DeploymentQueueEntry.get(id)

                    if ( entry.dateCreated > oldDate ) {
                        continue
                    }

                    def deployedHosts = DeployedHost.findAllByEntry(entry)

                    deployedHosts.each { DeployedHost deployedHost ->
                        deployedHost.delete()
                    }
                    entry.delete()
                }
        }

        log.info "cleanup job finished"

    }
}
