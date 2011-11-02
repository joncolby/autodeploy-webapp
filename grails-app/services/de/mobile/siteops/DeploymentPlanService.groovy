package de.mobile.siteops

import groovy.xml.MarkupBuilder

import org.springframework.transaction.annotation.Transactional
import de.mobile.siteops.ExecutionPlan.PlanType

class DeploymentPlanService {

	static transactional = false

	@Transactional(readOnly = true)
	def createDeploymentXml(DeploymentQueueEntry entry, ExecutionPlan plan, Host host, Environment environment) {
		HostClass hostclass = host.className
		Repository repository = environment.repository
		def planApplications = plan.applicationVersions
        def applications = []
        planApplications.each {
            if (hostclass.applications.contains(it.application)) {
                applications += it
            }
        }

		def writer = new StringWriter()
		MarkupBuilder xml = new MarkupBuilder(writer)

		xml.plan(id: plan.id) {

			xml.host(name: host.name, id: host.id, hostclass: host.className, environment: host.environment) {
				xml.repository {
					xml.url(url: repository?.baseUrl)
					xml.type(type: repository?.type)
				}

                xml.forceDeploy(plan.forceDeploy)

				applications.each { ApplicationVersion a ->
					xml.application(name: a.application.filename, id: a.application.id) {
						xml.type(a.application.type.name())
                        xml.marketPlace(a.application.marketPlace)
						xml.balancerType(a.application.balancerType)
						xml.startStopScript(a.application.startStopScript)
						xml.context(a.application.context)
                        xml.downloadName(a.application.downloadName)
						xml.artifactId(a.application.artifactId)
						xml.groupId(a.application.groupId)
						xml.suffix(a.application.suffix())
                        xml.releaseInfoJMXBean(a.application.releaseInfoJMXBean)
                        xml.releaseInfoJMXAttribute(a.application.releaseInfoJMXAttribute)
						xml.release("git")
						xml.revision(a.revision)
						xml.install_path(a.application.installDir)
						xml.properties_path(a.application.propertiesPath)
						xml.start_on_deploy(a.application.startOnDeploy)
						xml.assemble_properties(a.application.assembleProperties)
						xml.instance_properties(a.application.instanceProperties)
						xml.doProbe(a.application.doProbe)
						xml.modulename(a.application.modulename)
					}
				}
			}
		}

		return writer.toString()
	}

    @Transactional()
    def addPlanToQueue(deploymentQueueId, deploymentPlanId, revision) {
        def result = [type: 'error', queueEntryId: null, message: "Unknown error"]
        def targetQueue = DeploymentQueue.get(deploymentQueueId)
        if (!targetQueue) {
            result.message = "Could not find queue for deploymentQueueId '$deploymentQueueId'"
            return result
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
                result.message = "This enviroment does not support any applications in this plan, could not add to queue"
                return result
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
        if (!targetQueue.save(false)) {
            result.message = "Could not save queue entry"
            return result
        }

        result.queueEntryId = queueEntry.id

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
            result.message = "Plan '<strong>$executionPlan.name</strong>' submitted to queue but the following applications in this plan have no host in this environment:<br/><br/>" + missingAppsText.join("<br/>") + "</p>"
            result.type = 'warn'
        } else {
            result.message = "Plan '<strong>$executionPlan.name</strong>' submitted to queue."
            result.type = 'success'
        }

        return result
    }

}
