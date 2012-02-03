package de.mobile.siteops

import static de.mobile.siteops.HostStateType.*
import grails.converters.JSON
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser
import org.springframework.security.core.GrantedAuthority

class DeploymentOverviewController {

	def deploymentQueueService
    def accessControlService
    def notificationService

	def index = {

        /*
        def hostClassMap = [
                [hostClass: 'static', priority: 1, useConcurrency: false, concurrency: 2 ],
                [hostClass: 'hostclass1', priority: 2, useConcurrency: true, concurrency: 2 ],
                [hostClass: 'hostclass2', priority: 2, useConcurrency: true, concurrency: 2 ],
        ]

        def avgMap = [
                [host: 'static1', hostClass: 'static', avg: 15],
                [host: 'host1.1', hostClass: 'hostclass1', avg: 10],
                [host: 'host1.2', hostClass: 'hostclass1', avg: 12],
                [host: 'host1.3', hostClass: 'hostclass1', avg: 15],
                [host: 'host1.4', hostClass: 'hostclass1', avg: 17],
                [host: 'host2.1', hostClass: 'hostclass2', avg: 20],
                [host: 'host2.2', hostClass: 'hostclass2', avg: 20],
        ]


        def totalAvg = 0
        def priorities = hostClassMap.collect { it.priority }.unique().sort()
        priorities.each { prio ->
            def avgPerPriority = 0
            hostClassMap.findAll { it.priority == prio }.each { hc ->
                println "processing prio $prio for hostclass $hc.hostClass"
                def hostClassAvgs = avgMap.findAll { it.hostClass == hc.hostClass }?.collect { it.avg }
                if (hostClassAvgs) {
                    def concurrentHosts = hc.concurrency
                    if (!hc.useConcurrency || hostClassAvgs.size() < hc.concurrency) {
                        concurrentHosts = hostClassAvgs.size()
                    }
                    print "calc " + hostClassAvgs.sum() + " / " + concurrentHosts + " ... "
                    def avgPerHostClass = (hostClassAvgs.sum() / concurrentHosts as double).round()
                    println avgPerHostClass
                    if (avgPerHostClass > avgPerPriority) {
                        avgPerPriority = avgPerHostClass
                    }

                }
            }
            totalAvg += avgPerPriority
        }

        println "total: $totalAvg"

        def avgDurations = []

        def numhosts = avgMap.size()

        def avgTotal = avgMap.collect { it.avg }.sum() / numhosts as double
        */


        def teamId = g.cookie(name: 'autodeploy_teamId')
        def planId = g.cookie(name: 'autodeploy_planId')

        def teams =  Team.findAllByShortNameNotEqual("System", [sort: "fullName", order: "asc"])
        def teamModel = teams.collect { [id: it.id, name: it.fullName, url: g.createLink(action: 'plans', id: it.id)] }
        if ((!teamId || teamId.equals("")) && teams && teams.size() > 0) {
            teamId = teams[0].id
        } else {
            teamId = teamId as long
        }
        if (planId) planId = planId as long
        def currentTeam = Team.load(teamId)

        def planModel = [ [:] ]
        if (currentTeam) {
            def plans = DeploymentPlan.findAllByTeam(currentTeam, [sort: "lastUpdated", order: "desc"])
            planModel = plans.collect { [id: it.id, name: "$it.name (ID $it.id)"] }
        }

		def deploymentQueues = DeploymentQueue.findAll()
		def queues = deploymentQueues.collect { [name: it.environment.name, id: it.id, locked: !accessControlService.hasWriteAccessForQueue(it) ] }.sort { it.name }
        def model = [queues: queues, teams: teamModel, plans: planModel, selectedTeamId: teamId, selectedPlanId: planId]

		[model: model]
	}

    def plans = {
        def currentTeam = Team.load(params.id)
        def plans = DeploymentPlan.findAllByTeam(currentTeam)
        def planModel = plans.collect { [id: it.id, name: "$it.name (ID $it.id)"] }
        render planModel as JSON
    }

	def queueEntries = {
		def timestamp = params.timestamp as long
		def queueEntryId = params.entryId
        def fullDetails = params.fulldetails as boolean
        def queue = DeploymentQueue.get(params.id)
        def viewType = g.cookie(name: 'autodeploy_cookie')
        if (!viewType) viewType = 'hostView'

		def model = [lastTimeStamp: new Date().time,
			queueEntries: []]

		if (!queue) {
			render model as JSON
			return
        }

		model['id'] = queue.id
         if (notificationService.hasNotification()) {
             model['notification'] = notificationService.getNotification()
         }


		def queueEntries = []
		if (timestamp > 0) {
			queueEntries = DeploymentQueueEntry.overview(queue, timestamp).list()
		} else {
			queueEntries = DeploymentQueueEntry.findAllByQueue(queue, [max: 50, sort: "id", order: "desc"])
            model['settings'] = [ releaseMailByDefault: queue.environment.releaseMailByDefault ]
		}

		def detailModel

		// create a model by an extract of queueEntries order by newest to oldest queueEntry
		queueEntries.sort { a,b -> a.id < b.id ? 1 : -1 }.each { entry ->
			def modelEntry = [entryId: entry.id, team: entry.executionPlan.team.shortName, name: entry.executionPlan.outputName(), hasDbChanges: entry.executionPlan.databaseChanges, revision: entry.revision, status: entry.state.toString(), planId: entry.executionPlan.id, created: entry.dateCreated.format('dd.MM.yy HH:mm')]
            modelEntry['colorState'] = colorState(entry.state)
			modelEntry['actions'] = []
			switch (entry.state) {
				case QUEUED:
                    modelEntry['actions'] += [title: 'Deploy plan', type: 'deploy-all', action: g.createLink(action: 'deployAll', controller: 'deployAction', id: entry.id)]
                    modelEntry['actions'] += [title: 'Remove from list', type: 'remove', action: g.createLink(action: 'remove', controller: 'deployAction', id: entry.id)]
					break;
				case IN_PROGRESS:
					def processEntries = deploymentQueueService.getDeployedHostsForQueueEntry(entry)
                    if (processEntries.find { HostStateType.isFailed(it.state) }) {
                        modelEntry['actions'] += [title: 'Redeploy failed hosts', type: 'retryInProcess', action: g.createLink(action: 'redeployProcessFailed', controller: 'deployAction', id: entry.id)]
                    }
                    modelEntry['actions'] += [title: 'Abort deployment', type: 'cancel', action: g.createLink(action: 'abort', controller: 'deployAction', id: entry.id)]

					def totalAvgDuration = processEntries.collect { it.avgDuration }.sum()
					def duration = new Date().time - entry.lastUpdated?.time
                    if (duration && totalAvgDuration) {
                        def progress = (duration * 100 / totalAvgDuration) as int
                        modelEntry['duration'] = duration <= totalAvgDuration ? TimeUtils.formatDuration(totalAvgDuration - duration) + " left" : "overtime " + TimeUtils.formatDuration(duration - totalAvgDuration)
                        modelEntry['progress'] = progress <= 100 ? progress : 100
                    } else {
                        modelEntry['duration'] = "running "  + TimeUtils.formatDuration(duration)
                        modelEntry['progress'] = 100
                    }
					break;
				case DEPLOYED:
                    modelEntry['duration'] = TimeUtils.formatDuration(entry.duration)
                    modelEntry['actions'] += [title: 'Redeploy', type: 'redeploy', action: g.createLink(action: 'reDeployAll', controller: 'deployAction', id: entry.id)]
                    modelEntry['actions'] += [title: 'Rollback complete plan', type: 'rollback', action: g.createLink(action: 'rollback', controller: 'deployAction', id: entry.id)]
                    break;
                case CANCELLED:
                    modelEntry['duration'] = TimeUtils.formatDuration(entry.duration)
                    modelEntry['actions'] += [title: 'Redeploy failed hosts', type: 'retry', action: g.createLink(action: 'redeployFailed', controller: 'deployAction', id: entry.id)]
                    modelEntry['actions'] += [title: 'Remove from list', type: 'remove', action: g.createLink(action: 'remove', controller: 'deployAction', id: entry.id)]
                    break;
                case ERROR:
                case ABORTED:
					modelEntry['duration'] = TimeUtils.formatDuration(entry.duration)
                    modelEntry['actions'] += [title: 'Redeploy failed hosts', type: 'retry', action: g.createLink(action: 'redeployFailed', controller: 'deployAction', id: entry.id)]
                    modelEntry['actions'] += [title: 'Remove from list', type: 'remove', action: g.createLink(action: 'remove', controller: 'deployAction', id: entry.id)]
					break;
            }

            if ((queueEntryId && queueEntryId == entry.id) || (!queueEntryId && !detailModel)) {
                detailModel = createDetailModel(entry, viewType, fullDetails ? 0 : timestamp)
            }

            if (!accessControlService.hasWriteAccessForQueue(queue)) {
                modelEntry.actions = []
            }

			model['queueEntries'] += modelEntry
		}

		if (queueEntryId && !detailModel) {
			def queueEntry = DeploymentQueueEntry.findById(queueEntryId)
            if (queueEntry) {
                detailModel = createDetailModel(queueEntry, viewType, fullDetails ? 0 : timestamp)
            }
        }

        if (!detailModel) {
            detailModel = [content: []]
        }
		model['entryDetails'] = detailModel

        if (!model.entryDetails.content && !model.queueEntries && timestamp) {
            model['lastTimeStamp'] = timestamp
        }

        render model as JSON
	}

    private def colorState(HostStateType state) {
        switch (state) {
            case CANCELLED: return "default"
            case ERROR: return "error"
            case ABORTED: return "error"
            case DEPLOYED: return "success"
            case IN_PROGRESS: return "working"
            case QUEUED: return "default"
        }
    }

	private def createDetailModel(queueEntry, viewType, timestamp) {
		def result = [entryId: queueEntry.id]
        def details = []
		def processEntries = deploymentQueueService.getDeployedHostsForQueueEntry(queueEntry)
		if (processEntries) {
			processEntries.each { entry ->
				def resultEntry = details.find { it.hostclassId == entry?.hostclass?.id }
				if (!resultEntry) {
                    def apps = entry.getApplications()
                    if (accessControlService.hasWriteAccessForQueue(queueEntry.queue)) {
                        if (entry.state == HostStateType.DEPLOYED) {
                            apps.each {
                                it.actions += [title: 'Rollback this application', type: 'rollback', action: g.createLink(action: 'rollbackApplication', controller: 'deployAction', id: queueEntry.id, params: [appId: it.id])]
                            }
                        } else if ([ABORTED, ERROR, CANCELLED].contains(entry.state)) {
                            apps.each {
                                it.actions += [title: 'Redeploy this application', type: 'retry', action: g.createLink(action: 'retryApplication', controller: 'deployAction', id: queueEntry.id, params: [appId: it.id])]
                            }
                        }
                    }
					resultEntry = [ hostclassId: entry.hostclass.id, hostclassName: entry.hostclass.name, applications: apps, hosts: []]
					details += resultEntry
				}
				def hosts = createHostModel(entry, viewType, timestamp)
				if (hosts) {
					resultEntry.hosts += createHostModel(entry, viewType, timestamp)
				}
			}
		}
		details.findAll { !it.hosts  }.each { details.remove(it) }
        result['content'] = details

		return result
	}
	
	private def createHostModel(processEntry, viewType, timestamp) {
		if (!processEntry.newerThan(timestamp)) {
            return []
        }
		def model = [hostId: processEntry.hostid, name: processEntry.hostname, status: processEntry.state.toString(), duration: processEntry.duration(), messages: []]
		if (processEntry.state == IN_PROGRESS) {
			model['progress'] = processEntry.progress()
		}
        model['colorState'] = colorState(processEntry.state)

        if (viewType != 'appView') {
            processEntry.messages.reverse().each { msg ->
                if (msg.newerThan(timestamp)) model.messages += [ id: msg.id, type: msg.status, message: msg.date + " " + msg.message ]
            }
        }

		return model
	}
}
