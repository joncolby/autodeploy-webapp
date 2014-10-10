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

    // Get the version of the application prior to the latest successful release
    def previousVersionsFromApplication(apps,Environment environment)  {
            DeploymentQueue deploymentQueue = DeploymentQueue.findByEnvironment(environment)
            def queueEntries = DeploymentQueueEntry.finalizedEntries(deploymentQueue).list(sort: 'finalizedDate', order: 'desc')
            def result = []
            apps.each { Application app ->
                def appVersion = new Expando(application: app, revision: null, seenCount: 0)
                result += appVersion
            }

            for (DeploymentQueueEntry queueEntry: queueEntries) {
                def entryApplications = queueEntry.executionPlan.applicationVersions
                def deployedHosts = DeployedHost.findAllByEntryAndState(queueEntry, HostStateType.DEPLOYED)
                // this list will contain successfully deployed applications for the deployment queue entry
                def previousApplications = []

                // get only the applications deployed successfully to a host for this deployment queue (execution plan)
                deployedHosts.each { deployedHost ->
                   deployedHost.host.className.applications.intersect(entryApplications.collect { it.application }).each { app ->
                       // collects successfully deployed applicationVersions
                       previousApplications += entryApplications.findAll { it.application == app }
                   }
                }

                // remove duplicate applications found
                previousApplications = previousApplications.unique()
                previousApplications.each { ApplicationVersion applicationVersion ->
                    def found = result.find { it.application == applicationVersion.application }
                    if (found.seenCount < 2) {
                        result.find { it.application == applicationVersion.application }.revision = applicationVersion.revision
                        result.find { it.application == applicationVersion.application }.seenCount += 1
                    }
                }
            }
          return result.findAll { it.seenCount == 2 }
    }

    def queueEntriesForDeletion(Environment environment) {

        // GOAL:  do not delete the last deployed version of any app - there must be at least one app-version for every app

        def deleteQueueEntries = []
        DeploymentQueue deploymentQueue = DeploymentQueue.findByEnvironment(environment)
        //def queueEntries = DeploymentQueueEntry.finalizedEntries(deploymentQueue).list(sort: 'finalizedDate', order: 'desc')
        def queueEntries = DeploymentQueueEntry.allEntries(deploymentQueue).list(sort: 'dateCreated', order: 'desc')

        def applicationsInstalled = [:]

        // initialize map of all apps that have been installed in this environment minus inactive apps
          queueEntries.each { it.executionPlan.applicationVersions.each { ApplicationVersion appVersion ->
                if (appVersion.application.pillar.name != "INACTIVE")
                    applicationsInstalled[appVersion.application] = null
            }
          }

          for (DeploymentQueueEntry queueEntry: queueEntries) {

              def entryApplications = queueEntry.executionPlan.applicationVersions

              def deployedHosts = DeployedHost.findAllByEntryAndState(queueEntry, HostStateType.DEPLOYED)

              // has everything in this queue-entry already been deployed?
              def nullVersionsInQueueEntry = applicationsInstalled.findAll { applicationsInstalled.collect { it.key }.intersect(entryApplications.collect { it.application }).contains(it.key) }.findAll { it.value == null }

              if (nullVersionsInQueueEntry.size() == 0) {
                  deleteQueueEntries += queueEntry
              }
              //println "VERSIONS IN QUEUE ${queueEntry} (${queueEntry.id}) -->" + nullVersionsInQueueEntry
              deployedHosts.each { deployedHost ->
                     def applicationsOnDeployedHost = Application.createCriteria().list() {
                            hostclasses {
                             eq('id', deployedHost.host.className.id)
                            }
                     }
                    // update applications installed map for next pass
                    applicationsOnDeployedHost.intersect(entryApplications.collect { it.application }).each { Application app ->
                        entryApplications.findAll { it.application == app }.each { ApplicationVersion appVersion ->
                            if (!applicationsInstalled[appVersion.application]) {
                                    applicationsInstalled[appVersion.application] = appVersion.revision
                            }
                        }
                    }
              }

           }
          return deleteQueueEntries

    }
}
