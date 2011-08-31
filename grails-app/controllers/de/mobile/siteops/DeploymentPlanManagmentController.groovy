package de.mobile.siteops

import grails.converters.JSON
import de.mobile.siteops.ExecutionPlan.PlanType

class DeploymentPlanManagmentController {

    def index = {
        def teams = Team.findAllByShortNameNotEqual("System", [sort: "fullName", order: "asc"])
        def model = teams.collect { [id: it.id, fullName: it.fullName, shortName: it.shortName, planCount: it.plans.size()]}
        [teams: model]
    }

    def plans = {
        def model = []
        if (!params.id) render model as JSON

        Team team = Team.get(params.id)
        model += [id: 0, name: "Create new plan", url: g.createLink(action: 'createNew')]
        def plans = DeploymentPlan.findAllByTeam(team, [sort: "lastUpdated", order: "desc"])
        model += plans.collect { [id: it.id, name: "$it.name (ID $it.id)", url: g.createLink(action: 'applications', id: it.id)]}

        render model as JSON
    }

    def applications = {
        if (!params.id) {
            render MessageResult.errorMessage('Internal error: No plan id specified') as JSON
        }

        DeploymentPlan plan = DeploymentPlan.read(params.id)
        def apps = plan.applications
        def allApps = Application.list()

        def model = [url: g.createLink(action: 'editPlan', id: params.id), contribution: plan.contribution, ticket: plan.ticket, name: plan.name, requiresDatabaseChanges: plan.requiresDatabaseChanges, requiresPropertyChanges: plan.requiresPropertyChanges, created: plan.dateCreated, modified: plan.lastUpdated, apps: []]
        allApps.sort { a, b -> a.filename.compareTo(b.filename) }.each { app ->
            model['apps'] += [selected: apps.contains(app), name: "$app.filename ($app.pillar)", id: app.id]
        }

        render model as JSON
    }

    def createNew = {
        def allApps = Application.list()
        def model = [url: g.createLink(action: 'createPlan'), apps: []]
        allApps.sort { a, b -> a.filename.compareTo(b.filename) }.each { app ->
            model['apps'] += [selected: false, name: "$app.filename ($app.pillar)", id: app.id]
        }

        render model as JSON

    }

    def createPlan = {
        DeploymentPlan plan = new DeploymentPlan()

        ['contribution', 'ticket', 'name', 'requiresDatabaseChanges', 'requiresPropertyChanges'].each { p ->
            if (params.containsKey(p)) plan.properties[p] = params[p]
        }

        def appIds = parseAppsFromParam()
        def apps = Application.findAllByIdInList(appIds)
        if (apps && !apps.isEmpty()) {
            handleApplications(apps, plan)
        }

        if (!params.teamId) {
            render MessageResult.errorMessage('No teamId specified for createPlan') as JSON
        }

        def team = Team.load(params.teamId)
        if (!team) {
            render MessageResult.errorMessage("Could not find team with id '$params.teamId'") as JSON
        }
        plan.team = team

        def model = persistPlan(plan)

        render model as JSON
    }

    def editPlan = {
        if (!params.id) {
            render MessageResult.errorMessage('Internal error: No plan id specified') as JSON
        }
        DeploymentPlan plan = DeploymentPlan.get(params.id)


        ['contribution', 'ticket', 'name', 'requiresDatabaseChanges', 'requiresPropertyChanges'].each { p ->
            if (params.containsKey(p)) plan.properties[p] = params[p]
        }

        def appIds = parseAppsFromParam()
        def apps = Application.findAllByIdInList(appIds)
        if (apps && !apps.isEmpty()) {
            handleApplications(apps, plan)
        }

        def model = persistPlan(plan)

        render model as JSON
    }

    def addToQueue = {
        def deploymentQueueId = params.queueId
        def deploymentPlanId = params.long("planId")
        def revision = params.revision

        def targetQueue = DeploymentQueue.get(deploymentQueueId)
        if (!targetQueue) {
            render MessageResult.errorMessage("Could not find queue for deploymentQueueId '$deploymentQueueId'") as JSON
            return
        }
        def plan = DeploymentPlan.get(deploymentPlanId)
        def applications = plan.applications

        def env = targetQueue.environment
        def hostclasses = applications.collect { it.hostclasses }.flatten().unique()
        def hosts = Host.findAllByClassNameInListAndEnvironment(hostclasses, env)
        def applicationsInThisEnv = []
        hosts.each { host ->
            host.className.applications.each { applicationsInThisEnv += it }
        }

        if (!applicationsInThisEnv.containsAll(applications)) {
            def foundAtLeastOne = false
            for (Application app: applications) {
                if (applicationsInThisEnv.contains(app)) {
                    foundAtLeastOne = true
                    break
                }
            }
            if (!foundAtLeastOne) {
                render MessageResult.errorMessage("This enviroment does not support any applications in this plan, could not add to queue") as JSON
                return
            }
        }

        def executionPlan = new ExecutionPlan(name: plan.name, contribution: plan.contribution, ticket: plan.ticket ? plan.ticket : "", planType: PlanType.NORMAL, team: plan.team, applicationVersions: [])

        applications.each { app ->
            if (applicationsInThisEnv.contains(app)) {
                executionPlan.addToApplicationVersions(new ApplicationVersion(application: app, revision: revision).save())
            }
        }
        executionPlan.save()

        def queueEntry = new DeploymentQueueEntry(state: HostStateType.QUEUED, executionPlan: executionPlan, plan: plan, revision: revision, duration: 0)
        targetQueue.addToEntries(queueEntry)
        targetQueue.save(false)

        if (executionPlan.applicationVersions.size() < plan.applications.size()) {
            def missingHostForApps = []
            def executionPlanApps = executionPlan.applicationVersions.collect { it.application.id }
            plan.applications.each { planApp ->
                if (!executionPlanApps.contains(planApp.id)) {
                    missingHostForApps += planApp
                }
            }
            def missingAppsText = []
            missingHostForApps.each { Application app -> missingAppsText += "<span class='important small'>$app.filename</span>" }
            render MessageResult.warningMessage("Plan '<strong>$executionPlan.name</strong>' submitted to queue but the following applications in this plan have no host in this environment:<br/><br/>" + missingAppsText.join("<br/>") + "</p>") as JSON
        } else {
            render MessageResult.successMessage("Plan '<strong>$executionPlan.name</strong>' submitted to queue.") as JSON
        }
    }

    def persistPlan(plan) {
        def model = [:]
        if (!plan.save()) {
            model = MessageResult.addFieldErrors(plan.id, plan)
        } else {
            model = MessageResult.successMessage(plan.id, "Plan '$plan.name' was successfully modified")
        }
        return model
    }

    def parseAppsFromParam() {
        def appIds = []
        if (params.apps) {
            if (params.apps.contains(',')) {
                params.apps.split(",").each {
                    appIds += it as long
                }
            } else {
                appIds += params.apps as long
            }
        }
        return appIds
    }

    def handleApplications(apps, DeploymentPlan plan) {
        apps.each { app ->
            if (plan.applications == null || !plan.applications.contains(app)) {
                plan.addToApplications(app)
            }
        }
        def removeApps = []
        plan.applications.each { app ->
            if (!apps.contains(app)) removeApps += app
        }
        removeApps.each {
            plan.removeFromApplications(it)
        }
    }

}
