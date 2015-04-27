package de.mobile.siteops

import grails.plugins.springsecurity.Secured
import javax.swing.text.html.HTML

@Secured(['ROLE_ADMIN'])
class DeploymentAdminController {

    def notificationService

    def deploymentQueueService

	def index = {
	}

    def createNote = {
        if (params.containsKey('message') && params.message) {
            def msg = "<h1>Notification created: " + params.message + "</h1><br/>"
            notificationService.createNotification(params.message)
            render msg
        } else {
            // if any queues are locked, display message. otherwise remove notification
            freezeMessage()
        }
    }

    def unlockDeployment = {

        if (params.containsKey('id')) {
            def unlockEnvironment = params.id

            if (unlockEnvironment && unlockEnvironment.isInteger())  {
                def queue = DeploymentQueue.get(unlockEnvironment)
                if (queue) {
                    queue.frozen = false
                    queue.save(flush:true)
                    render "removed lock for " + queue.environment.name
                } else {
                    render "no queue found with id ${unlockEnvironment}"
                }
            } else {
                render "id parameter must contain an integer value."
            }
        } else {
            // default unlock all environments if no id provided to unlock param
            def frozenQueues =  DeploymentQueue.findAllByFrozen(true)
            frozenQueues.each { DeploymentQueue q -> q.frozen = false}
            render "removed all deployment locks."
        }
     freezeMessage()
    }

    def lockDeployment = {
        def production = Environment.findByName("Production")
        def productionDeployQueue = DeploymentQueue.findByEnvironment(production)

        if (params.containsKey('id')) {

            def lockEnvironment = params.id
            if (lockEnvironment && lockEnvironment.isInteger())  {
                def queue = DeploymentQueue.get(lockEnvironment)
                if (queue) {
                    queue.frozen = true
                    queue.save(flush:true)
                } else {
                     render "no queue found with id ${lockEnvironment}"
                }
            }
        } else {
                // default lock production if no id provided
                if (productionDeployQueue) {
                    productionDeployQueue.frozen = true
                    productionDeployQueue.save(flush:true)
                    render "locked production environment since no id parameter was set"
                }
        }

        freezeMessage()
    }

    private def freezeMessage() {
        // if any queues are locked, display message. otherwise remove notification
        def frozenQueues = DeploymentQueue.findAllByFrozen(true)

        if (frozenQueues) {
            def environmentNames = frozenQueues.collect { DeploymentQueue q ->
                q.environment.name
            }

            def message = "Deployment freeze in effect for " + environmentNames.join(', ')
            notificationService.createNotification(message)
            render "<h1>Notification created: " + message
        } else {
            notificationService.removeNotification()
            render "<h1>Notification removed.</h1><br/>"
        }

    }
}