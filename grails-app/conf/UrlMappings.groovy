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
        "/api/getPlatform" (controller: "api", action: "platform")
        "/api/getQueueId/$environmentName" (controller: "api", action: "queueId")

        "/dashboard"(controller: "dashboardOverview", action: "index")

        "/$format/versions/$environment/"(controller: "applicationVersions", action: "index")

        "/home"(view:"/home")
        "/admin"(controller: "deploymentAdmin")
        "/admin/createNote"(controller: "deploymentAdmin", action: "createNote")
		"/"(controller:"deploymentOverview")
		"500"(view:'/error')
	}
}
