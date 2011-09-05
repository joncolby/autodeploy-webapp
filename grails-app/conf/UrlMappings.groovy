import de.mobile.siteops.ApiController

class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

        "/api/plan/$planId/modules"(controller: "api", action: "modulesForPlan")
        "/api/plan/$planId/revision/$revision/addToQueue/$queueId"(controller: "api", action: "addToQueue")
        "/api/queueEntry/$queueEntryId/deploy"(controller: "api", action: "deploy")
        "/api/queueEntry/$queueEntryId/status"(controller: "api", action: "status")

        "/home"(view:"/home")
        "/admin"(controller: "deploymentAdmin")
		"/"(controller:"deploymentOverview")
		"500"(view:'/error')
	}
}
