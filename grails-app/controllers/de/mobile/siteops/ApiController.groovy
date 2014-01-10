package de.mobile.siteops

import org.hibernate.event.def.DefaultPostLoadEventListener

class ApiController {

    def deploymentPlanService

    def deploymentQueueService

    def modulesForPlan = {
        def planId = params.planId
        if (planId) {
            def plan = DeploymentPlan.get(planId)
            if (plan) {
                def modules = plan.applications.collect { Application a -> if (a.pillar.name != 'INACTIVE') { a.modulename } }
                if (modules) {
                    render modules.join(",")
                    return
                }
            }
        }
        render ""
    }

    def addToQueue = {
        def planId = params.planId
        def queueId = params.queueId
        def revision = params.revision

        def result = deploymentPlanService.addPlanToQueue(queueId, planId, revision)
        render "status:$result.type\nmessage:$result.message\nqueueEntryId:$result.queueEntryId"
    }

    def deploy = {
        def queueEntryId = params.queueEntryId
        def queueEntry = DeploymentQueueEntry.findById(queueEntryId)
        def canDeploy = deploymentQueueService.canDeploy(queueEntry)
        if (!canDeploy.success) {
            render "status:error\nmessage:$canDeploy.message"
            return
        }

        def deployedHosts = deploymentQueueService.createDeployedHostsForQueueEntry(queueEntry)
        if (!deployedHosts) {
            render "status:error\nmessage:Could not find any deployable hosts"
            return
        }

        deploymentQueueService.deployNextHosts(queueEntry)

        render "status:succcess\nmessage:deployment for '$queueEntry.executionPlan.name' started."
    }

    def platform = {
        def platform = Platform.getAll()[0]
        render platform.name
    }

    def queueId = {
        if (!params.environmentName) {
         render "status:error\nmessage: missing environment name"
         return
        }
        def environment = Environment.findByName(params.environmentName.toString().trim())
        def deploymentQueue = DeploymentQueue.findByEnvironment(environment)
        render deploymentQueue.id
    }

    def status = {
        def queueEntryId = params.queueEntryId

        if (!queueEntryId) return
        def queueEntry = DeploymentQueueEntry.findById(queueEntryId)

        render queueEntry.state
    }

}
