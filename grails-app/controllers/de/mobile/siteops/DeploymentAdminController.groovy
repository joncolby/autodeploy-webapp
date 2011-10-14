package de.mobile.siteops

import grails.plugins.springsecurity.Secured
import javax.swing.text.html.HTML


class DeploymentAdminController {

    def notificationService

    def deploymentQueueService

	def index = {
	}

    @Secured(['ROLE_ADMIN'])
    def createNote = {
        if (params.containsKey('message') && params.message) {
            def msg = "<h1>Notifation created: " + params.message + "</h1><br/>"
            if (params.containsKey('lock')) {
                deploymentQueueService.deployAllowed = false
                msg += "No more deployments allowed<br/>"
            } else {
                msg += "Deployments still allowed. Use query param 'lock=1' if you want to disallow new deployments<br/>"
            }
            notificationService.createNotification(params.message)

            render msg
        } else {
            notificationService.removeNotification()
            deploymentQueueService.deployAllowed = true
            render "<h1>Notifation removed.</h1><br/>"
        }

    }

}