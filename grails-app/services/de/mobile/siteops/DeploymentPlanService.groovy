package de.mobile.siteops

import groovy.xml.MarkupBuilder

import org.springframework.transaction.annotation.Transactional

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

				applications.each { ApplicationVersion a ->
					xml.application(name: a.application.filename, id: a.application.id) {
						xml.type(a.application.type.name())
						xml.pillar(a.application.pillar)
						xml.context(a.application.context)
						xml.artifactId(a.application.artifactId)
						xml.groupId(a.application.groupId)
						xml.suffix(a.application.suffix())
                        xml.releaseInfoJMXBean(a.application.releaseInfoJMXBean)
                        xml.releaseInfoJMXAttribute(a.application.releaseInfoJMXAttribute)
						xml.release("git") // TODO remove if no longer used in agent deployscript
						xml.revision(a.revision)
						xml.install_path(a.application.installDir)
						xml.start_on_deploy(a.application.startOnDeploy)
						xml.assemble_properties(a.application.assembleProperties)
						xml.instance_properties(a.application.instanceProperties)
					}
				}
			}
		}

		return writer.toString()
	}
}
