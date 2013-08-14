package de.mobile.siteops

import org.codehaus.groovy.grails.commons.ConfigurationHolder

import com.codahale.metrics.MetricRegistry

import de.mobile.zookeeper.ZookeeperService
import de.mobile.zookeeper.ZookeeperStateMonitor
import de.mobile.zookeeper.ZookeeperService.ZookeeperState

class ZookeeperHandlerService {

  static transactional = false
  static def config = ConfigurationHolder.config

  ZookeeperService zookeeperService;
  boolean connected = false
  def deploymentQueueService

  void submitDeployment(queueEntry, deployedHosts) {
    def nodePrefix = config.zookeeper.deploymentQueue.root
    def nodeData = deployedHosts.collect { DeployProcessEntry entry ->
      [entry: entry, nodeName: nodePrefix + "/" + entry.environment + "/" + entry.hostname]
    }

    nodeData.each { node ->
      if (zookeeperService.exists(node.nodeName)) {
        log.error "Could not create node " + node.nodeName + ", this node is already processing"
      } else {
        def handler = new DeploymentNodeListener(node.nodeName, node.entry.deploymentPlan, queueEntry, node.entry, deploymentQueueService, zookeeperService)
        if (!zookeeperService.createNode(handler, true)) {
          log.error "Could not create node " + node.nodeName
          node.entry.state = HostStateType.ERROR
          node.entry.messages += "Could not create zookeeper node " + node.nodeName
        } else {
          // deployment submitted -> track deployment start event
          CodahaleMetricsUtil.registry.meter(MetricRegistry.name("deployment", node.entry.environment, node.entry.hostname, "start")).mark()
        }
      }
    }
  }

  def abortDeployment(queueEntry, deployedHosts) {
    def nodePrefix = config.zookeeper.deploymentQueue.root
    def nodeData = deployedHosts.collect { DeployProcessEntry entry ->
      [entry: entry, nodeName: nodePrefix + "/" + entry.environment + "/" + entry.hostname]
    }

    def deploymentDone = true
    nodeData.each { data ->
      def node = zookeeperService.getNodeByName(data.nodeName)
      if (node) {
        if (data.entry.hasMessages()) {
          node.setData("action=abort")
          deploymentDone = false
        } else {
          zookeeperService.unregisterNode(node)
        }
      }
    }

    return deploymentDone
  }

  def destroy() {
    if (zookeeperService) {
      zookeeperService.shutdown()
    }
  }

  def init() {
    def zookeeperUrl = config.zookeeper.url
    zookeeperService = new ZookeeperService(zookeeperUrl, false, new SimpleStateMonitor(log))
    zookeeperService.connect()
  }

  private class SimpleStateMonitor implements ZookeeperStateMonitor {

    def log

    public SimpleStateMonitor(def log) {
      this.log = log
    }

    public void notity(ZookeeperState state) {
      if (state == ZookeeperState.DISCONNECTED) {
        connected = false
        log.info("Reconnecting because of server disconnected")
      } else if (state == ZookeeperState.EXPIRED) {
        connected = false
        log.info("Reconnecting because of expired session")
        zookeeperService.connect()
      } else if (state == ZookeeperState.CONNECTED) {
        connected = true
        log.info("Successfully connected to zookeeper server")
      } else {
        log.warn("Unhandled state in StateMonitor: " + state)
      }
    }

  }
}
