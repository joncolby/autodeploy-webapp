package de.mobile.siteops

import grails.converters.JSON

class ApplicationVersionsController {

    def applicationService

    def index = {
        def model = [:]
        def queueId = params.id
        if (!queueId) {
            render MessageResult.errorMessage("No id parameter provided (no queue selected)") as JSON
            return
        }

        DeploymentQueue deploymentQueue = DeploymentQueue.get(queueId)

        def allApps = Application.findAll()
        def modelApps = allApps.collect { [id: it.id, name: it.filename, pillar: it.pillar.name, type: it.type, context: it.context, revision: null, existsInEnv: false, actions: []]}
        model['apps'] = modelApps

        def env = deploymentQueue.environment
        def hostclasses = allApps.collect { it.hostclasses }.flatten().unique()
        def hosts = Host.findAllByClassNameInListAndEnvironment(hostclasses, env)
        def applicationsInThisEnv = []
        hosts.each { host ->
            host.className.applications.each { applicationsInThisEnv += it.id }
        }

        modelApps.each {
            if (applicationsInThisEnv.contains(it.id)) {
                it.existsInEnv = true
            }
        }

        def queueEntries = DeploymentQueueEntry.finalizedEntries(deploymentQueue).list(sort: 'finalizedDate', order: 'desc')
        if (!queueEntries) {
            render model as JSON
            return
        }

        def latestAppVersions = applicationService.latestVersionsFromApplications(allApps, queueEntries)
        latestAppVersions.each { ApplicationVersion appVersion ->
            if (appVersion.revision) {
                def entry = modelApps.find { it.id == appVersion.application.id }
                if (entry) {
                    entry.revision = appVersion.revision
                }
            }
        }

        render model as JSON
    }
}
