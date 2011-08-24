package de.mobile.siteops

import grails.converters.JSON

class RestartAgentsController {

    def restartAgentsService

    def index = {
        def deploymentQueues = DeploymentQueue.findAll()
        def queues = deploymentQueues.collect { [name: it.environment.name, id: it.id ] }.sort { it.name }
        def model = [queues: queues]

        [model: model]
    }

    def status = {
        def queueId = params.id as long

        def model = statusMessages(queueId)
        render model as JSON
    }

    def choose = {
        def queueId = params.id as long

        def model = statusMessages(queueId)
        render model as JSON
    }

    private def statusMessages(queueId) {
        def statusEntries = restartAgentsService.statusEntries(queueId);
        def processing = restartAgentsService.processing(queueId);
        def status = processing ? 'IN_PROGRESS' : 'IDLE';
        def model = [queueId: queueId, status: status, messages: []];
        if (statusEntries) {
            model.messages = statusEntries.collect { [hostname: it.hostname, state: it.state.toString(), statusMessage: it.statusMessage] }
        }

        return model
    }

    def restart = {
        if (!params.containsKey('id')) {
            def model = [info: MessageResult.errorMessage('No queueEntry id specified'), data: []]
            render model as JSON
            return false
        }
        def queue = DeploymentQueue.findById(params.id)
        if (!queue) {
            def model = [info: MessageResult.errorMessage("No queue found with id '$params.id'"), data: []]
            render model as JSON
            return false

        }
        if (queue.frozen) {
            def model = [info: MessageResult.errorMessage("This queue is currently frozen and cannot be restarted"), data: []]
            render model as JSON
            return false
        }

        def hosts = Host.findAllByEnvironment(queue.environment)
        def result = restartAgentsService.restartAgents(queue.id, hosts)

        if (result.status) {
            def status = statusMessages(queue.id)
            def model = [info: MessageResult.successMessage("Restart executed on queue $queue.environment"), data: [queueId: queue.id, status: 'IN_PROGRESS', messages: status.messages]]
            render model as JSON
        } else {
            def model = [info: MessageResult.errorMessage("Restart errors:<br/><p>" + messages.join("<br/>") + " <br/></p>"), data: []]
            render model as JSON
        }
    }

    def restartOld = {
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
