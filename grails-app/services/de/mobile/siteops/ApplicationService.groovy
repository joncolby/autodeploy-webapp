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

    def queueEntriesForDeletion(Environment environment) {

        // GOAL:  do not delete the last deployed version of any app - there must be at least one app-version for every app

        def deleteQueueEntries = []
        DeploymentQueue deploymentQueue = DeploymentQueue.findByEnvironment(environment)
        def queueEntries = DeploymentQueueEntry.finalizedEntries(deploymentQueue).list(sort: 'finalizedDate', order: 'desc')

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
