package de.mobile.siteops

import grails.converters.JSON
import de.mobile.siteops.ExecutionPlan.PlanType

class DeployActionController {

    def deploymentQueueService
    def accessControlService
    def applicationService

    def beforeInterceptor = [action: this.&checkConditionsMet, except: ['syncEnv']]

    def checkConditionsMet() {
        if (!params.containsKey('id')) {
            render MessageResult.errorMessage('No queueEntry id specified') as JSON
            return false
        }
        def queueEntry = DeploymentQueueEntry.findById(params?.id)
        if (!queueEntry) {
            render MessageResult.errorMessage("No queueEntry with id '$params.id' found") as JSON
            return false
        }
        params.queueEntry = queueEntry
        return true
    }

    def deployAll = {
        DeploymentQueueEntry queueEntry = params.queueEntry
        def canDeploy = deploymentQueueService.canDeploy(queueEntry)
        if (!canDeploy.success) {
            render MessageResult.errorMessage(canDeploy.message) as JSON
            return
        }

        def deployedHosts = deploymentQueueService.createDeployedHostsForQueueEntry(queueEntry)
        if (!deployedHosts) render MessageResult.defaultErrorMessage() as JSON

        deploymentQueueService.deployNextHosts(queueEntry)

        render MessageResult.successMessage("Deployment started for '$queueEntry.executionPlan.name'") as JSON
    }

    def reDeployAll = {
        DeploymentQueueEntry queueEntry = params.queueEntry
        ExecutionPlan plan = queueEntry.executionPlan

        def revisions = []
        def newExecutionPlan = new ExecutionPlan(forceDeploy: true, name: plan.name, contribution: plan.contribution, ticket: plan.ticket, databaseChanges: plan.databaseChanges, team: plan.team, planType: PlanType.REDEPLOY, applicationVersions: [], user: accessControlService.getCurrentUser())
        plan.applicationVersions.each {
            if (it.revision) {
                revisions += it.revision
                newExecutionPlan.addToApplicationVersions(new ApplicationVersion(application: it.application, revision: it.revision))
            }
        }
        newExecutionPlan.save()

        def newQueueEntry = new DeploymentQueueEntry(state: HostStateType.QUEUED, executionPlan: newExecutionPlan, revision: revisions.unique().size() == 1 ? revisions[0] : '(Multiple)', duration: 0)
        def deploymentQueue = queueEntry.queue
        deploymentQueue.addToEntries(newQueueEntry)
        deploymentQueue.save(false)

        render MessageResult.successMessage("Created redeploy for plan '" + newExecutionPlan.outputName() + "'") as JSON
    }


    def redeployFailed = {
        DeploymentQueueEntry queueEntry = params.queueEntry
        def canDeploy = deploymentQueueService.canDeploy(queueEntry)
        if (!canDeploy.success) {
            render MessageResult.errorMessage(canDeploy.message) as JSON
            return
        }

        def deployedHosts = deploymentQueueService.createdRedeployDeployProcessEntries(queueEntry, false)
        if (!deployedHosts) {
            render MessageResult.errorMessage("No failed hosts to redeploy found") as JSON
            return
        }

        deploymentQueueService.deployNextHosts(queueEntry)

        render MessageResult.successMessage("Deployment started for '$queueEntry.executionPlan.name'") as JSON
    }

    def redeployProcessFailed = {
        DeploymentQueueEntry queueEntry = params.queueEntry

        def deployedHosts = deploymentQueueService.createdRedeployDeployProcessEntries(queueEntry, true)
        if (!deployedHosts) {
            render MessageResult.errorMessage("No failed hosts to redeploy found") as JSON
            return
        }

        deploymentQueueService.deployNextHosts(queueEntry)

        render MessageResult.successMessage("Rescheduled failed host for running process") as JSON
    }

    def abort = {
        DeploymentQueueEntry queueEntry = params.queueEntry
        deploymentQueueService.abortDeployment(queueEntry)
        render MessageResult.successMessage("Abort in process") as JSON
    }

    def remove = {
        DeploymentQueueEntry queueEntry = params.queueEntry
        try {
            def deployedHosts = DeployedHost.findAllByEntry(queueEntry)
            deployedHosts.each { it.delete() }
            queueEntry.delete()
            render MessageResult.successMessage("Removed plan from queue") as JSON
        } catch (Exception e) {
            render MessageResult.errorMessage('Could not remove queueEntry') as JSON
        }
    }

    def retry = {
        DeploymentQueueEntry queueEntry = params.queueEntry
        def deployedHosts = DeployedHost.findAllByEntryAndStateInList(queueEntry, [HostStateType.ABORTED, HostStateType.ERROR])
        if (!deployedHosts) {
            render MessageResult.errorMessage("No ABORTED or ERROR hosts found for queueEntry Id '$queueEntry.id'") as JSON
            return
        }

        def sourceExecutionPlan = queueEntry.executionPlan
        def retryApplications = sourceExecutionPlan.applicationVersions

        def appsToRetry = []
        retryApplications.each { ApplicationVersion appVersion ->
            appVersion.application.hostclasses.each { hostclass ->
                if (deployedHosts.find { it.host.className.id == hostclass.id}) {
                    appsToRetry += appVersion
                }
            }
        }

        def targetExecutionPlan = ExecutionPlan.copyFrom(sourceExecutionPlan)
        targetExecutionPlan.planType = PlanType.RETRY

        def revisions = []
        appsToRetry.each { ApplicationVersion appVersion ->
            targetExecutionPlan.addToApplicationVersions(new ApplicationVersion(application: appVersion.application, revision: appVersion.revision).save())
            revisions += appVersion.revision
        }
        targetExecutionPlan.save()

        def newQueueEntry = new DeploymentQueueEntry(state: HostStateType.QUEUED, executionPlan: targetExecutionPlan, revision: revisions.unique().size() == 1 ? revisions[0] : '(Multiple)', duration: 0)
        def deploymentQueue = queueEntry.queue
        deploymentQueue.addToEntries(newQueueEntry)
        deploymentQueue.save(false)

        render MessageResult.successMessage("Redeploy of '" + sourceExecutionPlan.name + "' scheduled.") as JSON
    }

    def retryApplication = {
        DeploymentQueueEntry queueEntry = params.queueEntry
        def appId = params.appId
        if (!appId) {
            render MessageResult.errorMessage("No parameter appId specified for rollbackApplication") as JSON
            return
        } else {
            appId = appId as long
        }

        def sourceExecutionPlan = queueEntry.executionPlan
        def retryApplication = sourceExecutionPlan.applicationVersions.find { it.application.id == appId }
        if (!retryApplication) {
            render MessageResult.errorMessage("Application not found in this plan, cannot rollback") as JSON
            return
        }

        def targetExecutionPlan = new ExecutionPlan(name: sourceExecutionPlan.name + " (" + retryApplication.application.filename + ")", contribution: sourceExecutionPlan.contribution, ticket: sourceExecutionPlan.ticket ? sourceExecutionPlan.ticket : "", databaseChanges: sourceExecutionPlan.databaseChanges, team: sourceExecutionPlan.team, planType: PlanType.RETRY, applicationVersions: [], user: accessControlService.getCurrentUser())
        targetExecutionPlan.addToApplicationVersions(new ApplicationVersion(application: retryApplication.application, revision: retryApplication.revision).save())
        targetExecutionPlan.save()

        def newQueueEntry = new DeploymentQueueEntry(state: HostStateType.QUEUED, executionPlan: targetExecutionPlan, revision: retryApplication.revision, duration: 0)
        def deploymentQueue = queueEntry.queue
        deploymentQueue.addToEntries(newQueueEntry)
        deploymentQueue.save(false)

        render MessageResult.successMessage("Redeploy of '" + targetExecutionPlan.name + "' scheduled.") as JSON
    }

    def rollbackApplication = {
        DeploymentQueueEntry queueEntry = params.queueEntry
        def appId = params.appId
        if (!appId) {
            render MessageResult.errorMessage("No parameter appId specified for rollbackApplication") as JSON
            return
        } else {
            appId = appId as long
        }

        def sourceExecutionPlan = queueEntry.executionPlan
        def rollbackApplication = sourceExecutionPlan.applicationVersions.find { it.application.id == appId }

        if (!rollbackApplication) {
            render MessageResult.errorMessage("Application not found in this plan, cannot rollback") as JSON
            return
        }

        def targetExecutionPlan = new ExecutionPlan(name: sourceExecutionPlan.name + " (" + rollbackApplication.application.filename + ")", contribution: sourceExecutionPlan.contribution, ticket: sourceExecutionPlan.ticket ? sourceExecutionPlan.ticket : "", databaseChanges: sourceExecutionPlan.databaseChanges, team: sourceExecutionPlan.team, planType: PlanType.ROLLBACK, applicationVersions: [], user: accessControlService.getCurrentUser())

        def previousQueueEntries = DeploymentQueueEntry.previousEntries(queueEntry).list(sort: 'finalizedDate', order: 'desc')
        def sourceApps = [rollbackApplication.application]
        def appsToRollback = applicationService.latestVersionsFromApplications(sourceApps, previousQueueEntries)

        rollbackInternal(appsToRollback, targetExecutionPlan, queueEntry)
    }

    def rollback = {
        DeploymentQueueEntry queueEntry = params.queueEntry

        def sourceExecutionPlan = queueEntry.executionPlan
        def targetExecutionPlan = new ExecutionPlan(name: sourceExecutionPlan.name, contribution: sourceExecutionPlan.contribution, ticket: sourceExecutionPlan.ticket ? sourceExecutionPlan.ticket : "", databaseChanges: sourceExecutionPlan.databaseChanges, team: sourceExecutionPlan.team, planType: PlanType.ROLLBACK, applicationVersions: [], user: accessControlService.getCurrentUser())

        def previousQueueEntries = DeploymentQueueEntry.previousEntries(queueEntry).list(sort: 'finalizedDate', order: 'desc')
        def sourceApps = sourceExecutionPlan.applicationVersions.collect { it.application }
        def appsToRollback = applicationService.latestVersionsFromApplications(sourceApps, previousQueueEntries)

        rollbackInternal(appsToRollback, targetExecutionPlan, queueEntry)
    }

    def rollbackInternal(appsToRollback, targetExecutionPlan, queueEntry) {
        def revisions = []
        appsToRollback.each {
            targetExecutionPlan.addToApplicationVersions(it)
            if (it.revision) revisions += it.revision
        }

        if (appsToRollback.findAll { !it.revision }.size() > 0) {
            def invalidApps = []
            appsToRollback.findAll { !it.revision }.each { invalidApps += "<span class='important error small'>$it.application.filename</span>" }
            render MessageResult.errorMessage("Could not execute rollback, some applications in this plan never had a successful deployment:<p><br/>" + invalidApps.join("<br/>") + "</p>") as JSON
            return false
        }

        targetExecutionPlan.save()

        def newQueueEntry = new DeploymentQueueEntry(state: HostStateType.QUEUED, executionPlan: targetExecutionPlan, revision: revisions.unique().size() == 1 ? revisions[0] : '(Multiple)', duration: 0)
        def deploymentQueue = queueEntry.queue
        deploymentQueue.addToEntries(newQueueEntry)
        deploymentQueue.save()

        render MessageResult.successMessage("Rollback of plan '$targetExecutionPlan.name' created.") as JSON
    }

    def syncEnv = {
        if (!params.id) {
            render MessageResult.errorMessage("No target queue specified (param 'id')") as JSON
            return
        }
        if (!params.sourceId) {
            render MessageResult.errorMessage("No source queue specified (param 'sourceId')") as JSON
            return
        }

        def sourceQueueId = params.sourceId as long
        def targetQueueId = params.id as long

        DeploymentQueue sourceQueue = DeploymentQueue.get(sourceQueueId)
        if (!sourceQueue) {
            render MessageResult.errorMessage("Could not find source queue with id '$sourceQueueId'") as JSON
            return
        }
        DeploymentQueue targetQueue = DeploymentQueue.get(targetQueueId)
        if (!targetQueue) {
            render MessageResult.errorMessage("Could not find target queue with id '$targetQueueId'") as JSON
            return
        }

        Team team = Team.findByShortName("System")
        if (!team) {
            render MessageResult.errorMessage("No 'Systen' team exists, please create!") as JSON
            return
        }

        def allApps = Application.findAll()
        def queueEntries = DeploymentQueueEntry.finalizedEntries(sourceQueue).list(sort: 'finalizedDate', order: 'desc')
        if (!queueEntries) {
            render MessageResult.errorMessage("Source queue '$sourceQueue.environment.name' does not have any deployed entries") as JSON
            return
        }

        def latestAppVersions = applicationService.latestVersionsFromApplications(allApps, queueEntries)
        if (!latestAppVersions) {
            render MessageResult.errorMessage("Could not find any applications deployed in queue '$sourceQueue.environment.name'") as JSON
            return
        }

        def applicationsNotFound = []
        def revisions = []
        def newExecutionPlan = new ExecutionPlan(name: "Sync of environment $sourceQueue.environment.name", contribution: "No specific contribution", ticket: "N/A", databaseChanges: false, team: team, planType: PlanType.SYNC, repository: sourceQueue.environment.repository, applicationVersions: [], user: accessControlService.getCurrentUser())
        latestAppVersions.each {
            if (it.revision) {
                revisions += it.revision
                newExecutionPlan.addToApplicationVersions(it)
            } else {
                applicationsNotFound += it
            }
        }

        newExecutionPlan.save()

        def newQueueEntry = new DeploymentQueueEntry(state: HostStateType.QUEUED, executionPlan: newExecutionPlan, plan: null, revision: revisions.unique().size() == 1 ? revisions[0] : '(Multiple)', duration: 0)
        targetQueue.addToEntries(newQueueEntry)
        if (!targetQueue.save(flush: true)) {
            render MessageResult.errorMessage("Could not save plan to destination queue $targetQueue.environment.name")
            return
        }

        if (applicationsNotFound && applicationsNotFound.size() > 0) {
            render MessageResult.warningMessage("Sync of environment '<strong>$sourceQueue.environment.name</strong>' created.<br/>" + applicationsNotFound.size() + " applications never deployed in '$sourceQueue.environment.name' and were skipped") as JSON
        } else {
            render MessageResult.successMessage("Sync of environment '$sourceQueue.environment.name' has been created.") as JSON
        }

    }

}
