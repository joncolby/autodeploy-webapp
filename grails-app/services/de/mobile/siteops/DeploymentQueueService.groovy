package de.mobile.siteops

import de.mobile.siteops.Environment.DeployErrorType
import java.util.concurrent.ConcurrentHashMap
import org.springframework.transaction.annotation.Transactional

class DeploymentQueueService {

    static transactional = false

    def autoPlayService

    def deploymentPlanService

    def accessControlService

    def zookeeperHandlerService

    def deployQueueMap = new ConcurrentHashMap<DeploymentQueueEntry, List<DeployProcessEntry>>()

    def deployAllowed = true

    def canDeploy(DeploymentQueueEntry queueEntry) {

        DeploymentQueue queue = queueEntry.queue
        if (!deployAllowed) {
            return [success: false, message: "No deployments currently allowed, application will be restarted soon"]
        }
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
        queueEntry.executor = accessControlService.currentUser
        queueEntry.save()

        def deployQueueMapEntry = getDeployQueueMapEntry(queueEntry)
        if (!deployQueueMapEntry) {
            return null
        } else {
            return deployQueueMapEntry
        }
    }

    def createdRedeployDeployProcessEntries(DeploymentQueueEntry queueEntry, boolean deploymentRunning) {
        def queue = queueEntry.queue
        def env = queue.environment
        def plan = queueEntry.executionPlan

        def entries = getDeployedHostsForQueueEntry(queueEntry)
        if (entries) {
            entries.each { DeployProcessEntry entry ->
                if (HostStateType.isFailed(entry.state)) {
                    def host = Host.get(entry.hostid)
                    entry.deploymentPlan = deploymentPlanService.createDeploymentXml(queueEntry, plan, host, env)
                    entry.host = host
                    entry.state = HostStateType.QUEUED
                    entry.startTime = new Date().time
                    entry.messages = []
                    entry.updateSelf()
                }
            }
        }

        if (!deploymentRunning) {
            def existingEntry = deployQueueMap.find { it.key.id == queueEntry.id }
            if (existingEntry) {
                deployQueueMap[existingEntry.key] = entries
            } else {
                deployQueueMap[queueEntry] = entries
            }
            queueEntry.state = HostStateType.IN_PROGRESS
            queueEntry.lastUpdated = new Date()
            queueEntry.executor = accessControlService.currentUser
            if (!queueEntry.save(flush: true)) {
                log.error "Error persisting queue entry " + queueEntry.id
                queueEntry.errors.each {
                    log.error it
                }
            }
        }

        def deployQueueMapEntry = getDeployQueueMapEntry(queueEntry)
        if (!deployQueueMapEntry) {
            return null
        } else {
            return deployQueueMapEntry
        }
    }

    def deployNextHosts(DeploymentQueueEntry queueEntry) {
        def processHosts = getDeployQueueMapEntry(queueEntry)
        if (processHosts == null || processHosts.empty) {
            log.fatal "Could not find queue entry in deploy queue map when trying to deploy next hosts"
            return
        }
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
        def deployProcessEntries = entries.findAll { it.state == HostStateType.QUEUED }
        for (DeployProcessEntry entry: deployProcessEntries) {
            entry.addDeploymentMessage('DEPLOYMENT_INFO', 'Abort deployment as requested')
            entry.changeState(HostStateType.CANCELLED)
        }
        def hosts = entries.findAll { it.state == HostStateType.IN_PROGRESS }
        if (hosts) {
            def done = zookeeperHandlerService.abortDeployment(queueEntry, hosts)
            sleep(1000)
            hosts.each { it.changeState(HostStateType.CANCELLED) }
            if (done) deploymentDone(queueEntry)
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
            def finalState = null
            def deployQueueMapEntry = getDeployQueueMapEntry(queueEntry)
            if (!deployQueueMapEntry) {
                log.fatal "Could not find queue entry in deployment queue map!"
                def e = DeploymentQueueEntry.get(queueEntry.id)
                e.state = HostStateType.ERROR
                e.finalizedDate = new Date()
                e.save(flush: true)
                return
            }
            deployQueueMapEntry.each { DeployProcessEntry entry ->
                int duration = entry.processTime()
                totalDuration += duration
                def deployedHost
                if (entry.deployedHostId) {
                    deployedHost = DeployedHost.get(entry.deployedHostId)
                    deployedHost.state = entry.state
                    deployedHost.duration = duration
                    deployedHost.message = entry.messagesAsJSON()
                } else {
                    deployedHost = new DeployedHost(
                            entry: entry.queueEntry,
                            host: entry.host,
                            environment: entry.environment,
                            state: entry.state,
                            priority: entry.priority,
                            duration: duration,
                            message: entry.messagesAsJSON())
                }

                if (!deployedHost.save(flush: true)) {
                    finalState = HostStateType.ERROR
                    log.fatal("Could not save deployed host, due validation error!")
                    deployedHost.errors.each {
                        log.fatal(it)
                    }
                }
                if (!finalState || !entry.state.isHigherPriority(finalState)) finalState = entry.state
            }
            def e = DeploymentQueueEntry.get(queueEntry.id)
            e.duration = totalDuration
            e.state = finalState ? finalState : HostStateType.CANCELLED
            e.finalizedDate = new Date()
            e.save(flush: true)

            def queueEntryKey = deployQueueMap.find { it.key.id == queueEntry.id }?.key
            if (queueEntryKey) {
                deployQueueMap.remove(queueEntryKey)
                log.info "No queued hosts found, deployment done for $queueEntry.id"
            } else {
                log.error "Could not find queue entry in map for queueentry id $queueEntry.id"
            }

            if (autoPlayService.isEnabled(e.queue)) {
                if (finalState == HostStateType.DEPLOYED) {
                    def nextQueuedEntry = autoPlayService.nextQueuedEntry(e, e.queue)

                    if (nextQueuedEntry) {
                        def canDeploy = canDeploy(nextQueuedEntry)

                        if (canDeploy.success) {
                            def deployedHosts = createDeployedHostsForQueueEntry(nextQueuedEntry)
                            if (deployedHosts) deployNextHosts(nextQueuedEntry)
                        }
                    }
                }
            }

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
                    def hostclass = hostclassAppMap[it.host.className.id]
                    if (hostclass) {
                        def entry = new DeployProcessEntry(
                                deployedHostId: it.id,
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
                                hostclass: hostclass,
                                queueService: this)
                        entry.addMessages(it.message)
                        entry.timestamp = queueEntry.lastUpdated.time
                        entries += entry
                    }
                }
            }
        }
        return entries
    }

    def getHostclassApplicationMap(DeploymentQueueEntry queueEntry) {
        def plan = queueEntry.executionPlan

        def hostclassAppMap = [:]
        plan.applicationVersions.each { appVersion ->
            appVersion.application.hostclasses.each { hostclass ->
                if (!hostclassAppMap[hostclass.id]) {
                    hostclassAppMap[hostclass.id] = [id: hostclass.id, concurrency: hostclass.concurrency, name: hostclass.name, apps: []]
                }
                hostclassAppMap[hostclass.id].apps += [
                        name: appVersion.application.filename,
                        type: appVersion.application.type.name(),
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

    private def getDeployQueueMapEntry(queueEntry) {
        return deployQueueMap.find { it.key.id == queueEntry.id }?.value
    }

}
