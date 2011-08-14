package de.mobile.siteops

import grails.converters.JSON

class RestartAgentsController {

    def restartAgentsService

    def index = {
        if (!params.containsKey('id')) {
            render MessageResult.errorMessage('No queueEntry id specified') as JSON
            return false
        }
        def queue = DeploymentQueue.findById(params.id)
        if (!queue) {
            render MessageResult.errorMessage("No queue found with id '$params.id'") as JSON
            return false

        }
        if (queue.frozen) {
            render MessageResult.errorMessage("This queue is currently frozen and cannot be restarted") as JSON
            return false
        }

        def hosts = Host.findAllByEnvironment(queue.environment)
        def result = restartAgentsService.restartAgents(queue.id, hosts)

        def messages = []
        result.messages.each { msg -> messages += "<span class='important small'>$msg</span>" }
        if (result.status) {
            render MessageResult.successMessage("Restart submitted:<br/><p>" + messages.join("<br/>") + " <br/></p>") as JSON
        } else {
            render MessageResult.errorMessage("Restart errors:<br/><p>" + messages.join("<br/>") + " <br/></p>") as JSON
        }

    }
}
