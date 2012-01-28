package de.mobile.siteops

import grails.converters.JSON
import de.mobile.siteops.ExecutionPlan.PlanType

class DeploymentPlanManagmentController {

    def deploymentPlanService
    def releaseMailService

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

        def model = [deleteUrl: g.createLink(action: 'deletePlan', id: params.id), url: g.createLink(action: 'editPlan', id: params.id), contribution: plan.contribution, ticket: plan.ticket, name: plan.name, requiresDatabaseChanges: plan.requiresDatabaseChanges, requiresPropertyChanges: plan.requiresPropertyChanges, created: plan.dateCreated, modified: plan.lastUpdated, apps: []]
        allApps.sort { a, b -> a.filename.compareTo(b.filename) }.each { app ->
            model['apps'] += [selected: apps.contains(app), name: "$app.filename ($app.pillar)", id: app.id]
        }

        render model as JSON
    }

    def createNew = {
        def allApps = Application.list()
        def model = [url: g.createLink(action: 'createPlan'), deleteUrl: '', apps: []]
        allApps.sort { a, b -> a.filename.compareTo(b.filename) }.each { app ->
            model['apps'] += [selected: false, name: "$app.filename ($app.pillar)", id: app.id]
        }

        render model as JSON

    }

    def deletePlan = {
        if (!params.id) {
            render MessageResult.errorMessage('Internal error: No plan id specified') as JSON
        }
        DeploymentPlan plan = DeploymentPlan.get(params.id)
        plan.delete()

        render MessageResult.successMessage("Removed plan") as JSON
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

    def exampleModel() {
        def queueIdTest = 3797
        def queueEntry = DeploymentQueueEntry.load(queueIdTest)
        def model = [:]

        model.team = queueEntry.executionPlan.team.fullName
        model.planName = queueEntry.executionPlan.name
        model.contribution = queueEntry.executionPlan.contribution
        model.ticket = queueEntry.executionPlan.ticket
        model.databaseChanges = queueEntry.executionPlan.databaseChanges
        model.applications = []
        model.multipleRevs = false
        def lastRev = null
        queueEntry.executionPlan.applicationVersions.each { ApplicationVersion app ->
            if (lastRev && lastRev != app.revision) {
                model.multipleRevs = true
            }
            model.applications += [ name: app.application.modulename + " (" + app.application.pillar.name + ")", revision: app.revision]
            lastRev = app.revision
        }
        model.revision = model.multipleRevs ? "(multiple, see applications)" : lastRev

        return model
    }

    def addToQueue = {
        def deploymentQueueId = params.queueId
        def deploymentPlanId = params.long("planId")
        def revision = params.revision
        def releaseMail = params.releaseMail

        def mailModel = exampleModel()
        println mailModel
        sendMail {
            to "ibartel@gmail.com"
            subject "Testmail"
            body( view: "/mail/releaseMail", model: mailModel)
        }

        if (!revision || revision.size() <= 1) {
            render MessageResult.errorMessage("Please enter a revision number") as JSON
            return
        }

        def result = deploymentPlanService.addPlanToQueue(deploymentQueueId, deploymentPlanId, revision)
        if (result.type == 'success') {
            if (releaseMail) {
                releaseMailService.releaseMail(result.queueEntryId)
            }

            render MessageResult.successMessage(result.message) as JSON
        } else if (result.type == 'error') {
            render MessageResult.errorMessage(result.message) as JSON
        } else if (result.type == 'warning') {
            render MessageResult.warningMessage(result.message) as JSON
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
