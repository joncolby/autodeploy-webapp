package de.mobile.siteops

class ApiController {

    def modulesForPlan = {
        def planId = params.planId
        if (planId) {
            def plan = DeploymentPlan.get(planId)
            if (plan) {
                def modules = plan.applications.collect { it.modulename }
                if (modules) {
                    render modules.join(" ")
                }
            }
        }
        render ""
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
