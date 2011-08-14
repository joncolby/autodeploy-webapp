package de.mobile.siteops

import de.mobile.zookeeper.ZookeeperService
import java.util.concurrent.ConcurrentHashMap
import de.mobile.zookeeper.ZookeeperNode
import de.mobile.siteops.RestartProcessEntry.RestartProcessState

class RestartAgentsService {

    static transactional = false

    def zookeeperHandlerService

    def restartQueueMap = new ConcurrentHashMap<Long, List<RestartProcessEntry>>();

    def restartAgents(queueId, hosts) {
        def zookeeperService = zookeeperHandlerService.getZookeeperService()

        def result = [status: true, messages: []]
        if (restartQueueMap.containsKey(queueId)) {
            result.status = false
            result.messages += "Restart already in progress"
            return result
        }

        def nodesData = hosts.collect { Host host ->
            [host: host, nodeName: "/control/restart/" + host.environment + "/" + host.name]
        }

        def entries = []
        nodesData.each { nodeData ->
            if (zookeeperService.exists(nodeData.nodeName)) {
                ZookeeperNode node = zookeeperService.getNodeByName(nodeData.nodeName)
                zookeeperService.unregisterNode(node)
            }

            def entry = new RestartProcessEntry(hostname: nodeData.host.name, queueId: queueId, state: RestartProcessState.IDLE, statusMessage: "Scheduled for restart")
            entries += entry
            RestartNodeHandler handler = new RestartNodeHandler(nodeData.nodeName, entry, zookeeperService, this)
            zookeeperService.registerNode(handler)
            result.messages += "Agent restart on host '" + nodeData.host.name + "' requested"
        }
        if (entries.size() > 0) {
            restartQueueMap[queueId] = entries
        } else {
            result.status = false
            result.messages += "Could not restart any hosts, no hosts in this envrionment?!"
        }


        return result
    }

    def agentRestartFinished(RestartProcessEntry entry) {
        def processEntries = restartQueueMap.get(entry.queueId)
        if (processEntries) {
            if (processEntries.findAll { !it.finished() }.size() <= 0) {
                restartQueueMap.remove(entry.queueId)
                log.info "Restart of environment id '" + entry.queueId + "' is completed"
            }
        }
    }
}
