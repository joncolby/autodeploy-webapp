package de.mobile.siteops

import grails.converters.JSON

class AutoPlayController {

    def autoPlayService
    def accessControlService

    def toggle = {

        if (!accessControlService.isLoggedIn()) {
            render MessageResult.errorMessage(message(code:"default.not.logged.in")) as JSON
            return
        }

        def queueId = params.id
        def queue

        if (queueId) {
            queue = DeploymentQueue.get(queueId)
        }

        if (queue) {
            println "is enabled? " + autoPlayService.isEnabled(queue)
            autoPlayService.isEnabled(queue) ? autoPlayService.disable(queue) : autoPlayService.enable(queue)
        } else {
            render MessageResult.errorMessage("no queue with id ${queueId} found") as JSON
            return
        }

        redirect(controller: "deploymentOverview")

    }



}
