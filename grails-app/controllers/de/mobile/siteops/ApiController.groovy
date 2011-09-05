package de.mobile.siteops

class ApiController {

    def deploymentPlanService

    def modulesForPlan = {
        def planId = params.planId
        if (planId) {
            def plan = DeploymentPlan.get(planId)
            if (plan) {
                def modules = plan.applications.collect { it.modulename }
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

    def deployPlan = {
        def planId = params.id
        if (planId) {
            def plan = DeploymentPlan.get(planId)
            if (plan) {

            }
        }
    }

}
