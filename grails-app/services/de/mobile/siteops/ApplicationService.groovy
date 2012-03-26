package de.mobile.siteops

class ApplicationService {

    static transactional = false

    def latestVersionsFromApplications(apps, queueEntries) {
        def result = []
        apps.each { app ->
            result += new ApplicationVersion(application: app, revision: null)
        }
        for (DeploymentQueueEntry queueEntry: queueEntries) {
            def entryApplications = queueEntry.executionPlan.applicationVersions
            def deployedHosts = DeployedHost.findAllByEntryAndState(queueEntry, HostStateType.DEPLOYED)
            def previousApplications = []
            deployedHosts.each { deployedHost ->
                deployedHost.host.className.applications.intersect(entryApplications.collect { it.application }).each { app ->
                    previousApplications += entryApplications.findAll { it.application == app }
                }
            }

            previousApplications = previousApplications.unique()
            def appsWithoutVersion = result.findAll { !it.revision }
            if (!appsWithoutVersion) break
            if (previousApplications) {
                appsWithoutVersion.collect { it.application.id }.intersect(previousApplications.collect { it.application.id }).each { intersectApp ->
                    result.find { it.application.id == intersectApp}.revision = previousApplications.find { it.application.id == intersectApp }?.revision
                }
            }

        }
        return result
    }

    def queueEntriesForDeletion(apps,queueEntries) {
        def result = []
        def deleteQueueEntries = []
        apps.each { app ->
            result += new ApplicationVersion(application: app, revision: null)
        }

        for (DeploymentQueueEntry queueEntry: queueEntries) {

            def entryApplications = queueEntry.executionPlan.applicationVersions
            def deployedHosts = DeployedHost.findAllByEntryAndState(queueEntry, HostStateType.DEPLOYED)
            def previousApplications = []
            deployedHosts.each { deployedHost ->
                deployedHost.host.className.applications.intersect(entryApplications.collect { it.application }).each { app ->
                    previousApplications += entryApplications.findAll { it.application == app }
                }
            }

            boolean deleteQueueEntry = true;
            entryApplications.each { entryApp ->
                if (!result.find { it.application.id == entryApp.application.id && it.revision }) {
                    deleteQueueEntry = false;
                }
            }

            if (deleteQueueEntry) deleteQueueEntries += queueEntry.id

            //println "Delete queeentry $queueEntry.id = $deleteQueentry"
        }

        return deleteQueueEntries
    }
}
