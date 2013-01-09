package de.mobile.siteops

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler

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
        def result = []
        def deleteQueueEntries = []

        DeploymentQueue deploymentQueue = DeploymentQueue.findByEnvironment(environment)
        def queueEntries = DeploymentQueueEntry.finalizedEntries(deploymentQueue).list(sort: 'finalizedDate', order: 'desc')

        def apps = Application.findAll()
        apps.each { app ->
            result += new ApplicationVersion(application: app, revision: null)
        }

        for (DeploymentQueueEntry queueEntry: queueEntries) {

            def entryApplications = queueEntry.executionPlan.applicationVersions
            def deployedHosts = DeployedHost.findAllByEntryAndState(queueEntry, HostStateType.DEPLOYED)
            def previousApplications = []
            deployedHosts.each { deployedHost ->

                /*  get all the applications associated with this, using criteria.
                    Necessary because there may be an application that no longer exists
                    in the host_class_applications mapping table.  For example, if someone deletes the application
                    inappropriately by hand.
                */
                 def applicationsInHostClass = Application.createCriteria().list() {
                     hostclasses {
                         eq('id', deployedHost.host.className.id)
                     }
                 }

                // this fails if a mapped application in host_class_applications no longer exists
                //deployedHost.host.className.applications.intersect(entryApplications.collect {

                applicationsInHostClass.intersect(entryApplications.collect { it.application }).each { app ->
                    previousApplications += entryApplications.findAll { it.application == app }
                }
            }

            boolean deleteQueueEntry = true

            entryApplications.each { entryApp ->
                if (!result.find { it.application.id == entryApp.application.id && it.revision }) {
                    deleteQueueEntry = false;
                }
            }

            if (deleteQueueEntry) deleteQueueEntries += queueEntry.id

            previousApplications = previousApplications.unique()
            def appsWithoutVersion = result.findAll { !it.revision }
            if (!appsWithoutVersion) break
            if (previousApplications) {
                appsWithoutVersion.collect { it.application.id }.intersect(previousApplications.collect { it.application.id }).each { intersectApp ->
                    result.find { it.application.id == intersectApp}.revision = previousApplications.find { it.application.id == intersectApp }?.revision
                }
            }

        }

        return deleteQueueEntries
    }
}
