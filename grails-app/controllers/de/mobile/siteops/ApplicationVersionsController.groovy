package de.mobile.siteops

import grails.converters.JSON
import grails.converters.XML
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder

class ApplicationVersionsController {

    def applicationService

    def index = {
        def model = [:]
        def environmentParam = params.environment
        if (!environmentParam) {
            render MessageResult.errorMessage("No environment parameter provided") as JSON
            return
        }

        Environment env = Environment.findByName(environmentParam)


        if (!env) {
            render MessageResult.errorMessage("No environment with name ${environmentParam} found") as JSON
            return
        }

        DeploymentQueue deploymentQueue = DeploymentQueue.findByEnvironment(env)

        def allApps = Application.findAll()
        def modelApps = allApps.collect { [id: it.id, name: it.filename, pillar: it.pillar.name, type: it.type.name(), context: it.context, revision: null, existsInEnv: false, actions: []]}
        model['apps'] = modelApps

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

        if ( params.format == "xml" ) {
            render(contentType:"text/xml",encoding:"UTF-8") {

                environment(name: "${env.name}") {
                    for ( a in model.apps ) {
                        application() {
                            name("${a.name}")
                            id("${a.id}")
                            revision("${a.revision}")
                            context("${a.context}")
                            type("${a.type}")
                            pillar("${a.pillar}")
                        }
                    }
                }
            }
    } else {
        render model as JSON
    }

    }

}
