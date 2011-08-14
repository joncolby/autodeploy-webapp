package de.mobile.siteops

import java.util.concurrent.ConcurrentHashMap

import org.springframework.transaction.annotation.Transactional
import de.mobile.siteops.Environment.DeployErrorType

class DeploymentQueueService {

    static transactional = false

    def deploymentPlanService

    def zookeeperHandlerService

    def deployQueueMap = new ConcurrentHashMap<DeploymentQueueEntry, List<DeployProcessEntry>>()

    def canDeploy(DeploymentQueueEntry queueEntry) {
        DeploymentQueue queue = queueEntry.queue
        if (queue.frozen) return [success: false, message: "This deployment queue is currently 'frozen'<br/>Currently no deployment allowed"]
        if (!zookeeperHandlerService.connected) return [success: false, message: "Zookeeper is not connected, no deployment possible"]

        def processingCount = DeploymentQueueEntry.processedEntries(queueEntry.queue).count()
        if (processingCount > 0) return [success: false, message: "Another deployment is already running."]

        return [success: true, message: "Deployment checks passed"]
    }

    @Transactional()
    def createDeployedHostsForQueueEntry(DeploymentQueueEntry queueEntry) {

        deployQueueMap[queueEntry] = createDeployProcessEntries(queueEntry)

        queueEntry.state = HostStateType.IN_PROGRESS
        queueEntry.lastUpdated = new Date()
        queueEntry.save()

        if (!deployQueueMap[queueEntry]) {
            deployQueueMap.remove(queueEntry)
            return null
        } else {
            return deployQueueMap[queueEntry]
        }
    }

    def deployNextHosts(DeploymentQueueEntry queueEntry) {
        def processHosts = deployQueueMap[queueEntry]
        handleDeployErrors(processHosts)
        def minPrioEntry = processHosts?.findAll { it.state == HostStateType.QUEUED || it.state == HostStateType.IN_PROGRESS }?.min { it.priority }
        if (minPrioEntry) {
            def hosts = processHosts.findAll { it.state == HostStateType.QUEUED && it.priority == minPrioEntry.priority }
            def deployHosts = applyConcurrency(hosts, processHosts)
            zookeeperHandlerService.submitDeployment(queueEntry, deployHosts)
        } else {
            if (processHosts && !processHosts.findAll { it.state == HostStateType.QUEUED || it.state == HostStateType.IN_PROGRESS }) {
                sleep(500)
                deploymentDone(queueEntry)
            }
        }
    }

    def handleDeployErrors(deployHosts) {
        def errorHosts = deployHosts.findAll { it.state == HostStateType.ERROR }
        if (errorHosts) {
            errorHosts.each { errorHost ->
                if (errorHost.deployErrorType == DeployErrorType.SKIP_HOSTCLASS) {
                    deployHosts.findAll { it.state == HostStateType.QUEUED && it.hostclass.id == errorHost.hostclass.id }.each {
                        it.changeState(HostStateType.ABORTED)
                        it.addDeploymentMessage('DEPLOYMENT_ERROR', 'Aborted due previous error in this hostclass')
                    }
                }
            }
        }
    }

    def applyConcurrency(selectedHosts, allDeployHosts) {
        def deployHosts = []
        selectedHosts.each { selectedHost ->
            def hostclassHosts = deployHosts.findAll { it.hostclass.id == selectedHost.hostclass.id }
            hostclassHosts += allDeployHosts.findAll { it.state == HostStateType.IN_PROGRESS && it.hostclass.id == selectedHost.hostclass.id }
            if (!selectedHost.useHostClassConcurrency || !hostclassHosts || hostclassHosts.size() < selectedHost.hostclass.concurrency) {
                deployHosts += selectedHost
            }
        }
        return deployHosts
    }

    @Transactional()
    def abortDeployment(DeploymentQueueEntry queueEntry) {
        def entries = deployQueueMap.find { it.key.id == queueEntry.id }?.value
        entries.findAll { it.state == HostStateType.QUEUED }.each {
            it.addDeploymentMessage('DEPLOYMENT_INFO', 'Abort deployment as requested')
            it.changeState(HostStateType.CANCELLED)
        }
        def hosts = entries.findAll { it.state == HostStateType.IN_PROGRESS }
        if (hosts) {
            zookeeperHandlerService.abortDeployment(queueEntry, hosts)
            sleep(1000)
            hosts.each { it.changeState(HostStateType.CANCELLED) }
        } else {
            if (entries) {
                deploymentDone(queueEntry)
            } else {
                queueEntry.setState(HostStateType.CANCELLED)
            }
        }
    }

    @Transactional()
    void deploymentDone(DeploymentQueueEntry queueEntry) {
        DeployedHost.withTransaction { status ->
            def totalDuration = 0
            def finalState
            deployQueueMap[queueEntry].each { DeployProcessEntry entry ->
                int duration = entry.processTime()
                totalDuration += duration
                def deployedHost = new DeployedHost(
                        entry: entry.queueEntry,
                        host: entry.host,
                        environment: entry.environment,
                        state: entry.state,
                        priority: entry.priority,
                        duration: duration,
                        message: entry.messagesAsJSON())
                deployedHost.save(flush: true)
                if (!finalState || !entry.state.isHigherPriority(finalState)) finalState = entry.state
            }
            def e = DeploymentQueueEntry.get(queueEntry.id)
            e.duration = totalDuration
            e.state = finalState ? finalState : HostStateType.CANCELLED
            e.finalizedDate = new Date()
            e.save(flush: true)
            deployQueueMap.remove(queueEntry)
            log.info "No queued hosts found, deployment done for $queueEntry.id"
        }

    }

    def createDeployProcessEntries(queueEntry) {
        def queue = queueEntry.queue
        def env = queue.environment
        def plan = queueEntry.executionPlan
        def applications = plan.applicationVersions.collect { it.application }
        def hostclasses = applications.collect { it.hostclasses }.flatten().unique()
        def hosts = Host.findAllByClassNameInListAndEnvironment(hostclasses, env)

        if (!hosts || hosts.isEmpty()) {
            return null
        }

        def avgDurations = [:]
        def results = DeployedHost.avgDurationByHosts(hosts).list()
        results?.each {
            avgDurations[it[0].id] = it[1].round()
        }

        def hostclassAppMap = getHostclassApplicationMap(queueEntry)

        def entries = []
        hosts.each { host ->
            def deploymentPlan = deploymentPlanService.createDeploymentXml(queueEntry, plan, host, env)
            def priority = hostclasses.find { it.id == host.className.id }?.priority
            int avgDuration = avgDurations.find { it.key == host.id }?.value
            if (!avgDuration) avgDuration = 0
            def entry = new DeployProcessEntry(
                    queueEntry: queueEntry,
                    host: host,
                    hostname: host.name,
                    hostid: host.id,
                    environment: env,
                    deploymentPlan: deploymentPlan,
                    priority: priority,
                    deployErrorType: env.deployErrorType,
                    useHostClassConcurrency: env.useHostClassConcurrency,
                    avgDuration: avgDuration,
                    hostclass: hostclassAppMap[host.className.id],
                    queueService: this)
            if (!deploymentPlan) {
                entry.changeState(HostStateType.ERROR)
                entry.addDeploymentMessage('DEPLOYMENT_ERROR', 'Deployment plan could not be created')
            }
            entries += entry
        }

        return entries
    }

    def getDeployedHostsForQueueEntry(DeploymentQueueEntry queueEntry) {
        def entries = deployQueueMap.find { it.key.id == queueEntry.id }?.value
        if (!entries) {
            entries = []
            def deployedHosts = DeployedHost.findAllByEntry(queueEntry)
            if (!deployedHosts) {
                entries = createDeployProcessEntries(queueEntry)
            } else {
                def hostclassAppMap = getHostclassApplicationMap(queueEntry)
                deployedHosts.each {
                    def entry = new DeployProcessEntry(
                            queueEntry: it.entry,
                            host: it.host,
                            hostname: it.host.name,
                            hostid: it.host.id,
                            environment: it.host.environment,
                            state: it.state,
                            priority: it.priority,
                            deployErrorType: it.host.environment.deployErrorType,
                            useHostClassConcurrency: it.host.environment.useHostClassConcurrency,
                            avgDuration: it.duration,
                            hostclass: hostclassAppMap[it.host.className.id],
                            queueService: this)
                    entry.addMessages(it.message)
                    entry.timestamp = queueEntry.lastUpdated.time
                    entries += entry
                }
            }
        }
        return entries
    }

    def getHostclassApplicationMap(DeploymentQueueEntry queueEntry) {
        def queue = queueEntry.queue
        def plan = queueEntry.executionPlan

        def hostclassAppMap = [:]
        plan.applicationVersions.each { appVersion ->
            appVersion.application.hostclasses.each { hostclass ->
                if (!hostclassAppMap[hostclass.id]) {
                    hostclassAppMap[hostclass.id] = [id: hostclass.id, concurrency: hostclass.concurrency, name: hostclass.name, apps: []]
                }
                hostclassAppMap[hostclass.id].apps += [
                        name: appVersion.application.filename,
                        type: appVersion.application.type,
                        pillar: appVersion.application.pillar.name,
                        revision: appVersion.revision,
                        id: appVersion.application.id,
                        context: appVersion.application.context,
                        actions: []
                ]
            }
        }
        hostclassAppMap
    }

}
