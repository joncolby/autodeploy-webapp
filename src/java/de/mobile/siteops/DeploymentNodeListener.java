package de.mobile.siteops;

import java.util.Date;

import org.apache.log4j.Logger;

import com.codahale.metrics.MetricRegistry;

import de.mobile.zookeeper.AbstractNodeHandler;
import de.mobile.zookeeper.ZookeeperNode;
import de.mobile.zookeeper.ZookeeperService;

public class DeploymentNodeListener extends AbstractNodeHandler {

    private static Logger logger = Logger.getLogger(DeploymentNodeListener.class);

    private static final int CHECK_INTERVAL = 1000;

    private static final int MAX_INACTIVE_PERIOD = 5 * 60 * 1000;

    private final String nodeName;

    private final String deploymentPlan;
    
    private final DeployProcessEntry processEntry;
    
    private final DeploymentQueueEntry queueEntry;

    private final DeploymentQueueService deploymentQueueService;
    
    private final ZookeeperService zookeeperService;

    private final DeploymentObserver observer;

    /*
     * initially set the timeout to 10 seconds, after the first message is received the timeout is set to MAX_INACTIVE_PERIOD.
     * If the agent is not running the host will be timeout after 10 seconds, since the agent starts with a message the 10 seconds should be enough.
     */
    private int inactivityPeriod = 10 * 1000;

    public DeploymentNodeListener(String nodeName, String deploymentPlan, DeploymentQueueEntry queueEntry, DeployProcessEntry processEntry, DeploymentQueueService deploymentQueueService, ZookeeperService zookeeperService) {
        this.nodeName = nodeName;
        this.deploymentPlan = deploymentPlan;
        this.queueEntry = queueEntry;
        this.processEntry = processEntry;
        this.deploymentQueueService = deploymentQueueService;
        this.zookeeperService = zookeeperService;
        this.observer = new DeploymentObserver();
    }

    public void onNodeInitialized(ZookeeperNode node) {
        // nothing to do here
    }

    public void onNodeCreated(ZookeeperNode node) {
        logger.debug("Created new node '" + node + "' and setting data '" + deploymentPlan + "'");
        if (getNode() != null) {
            getNode().setData(deploymentPlan);
            processEntry.changeState(HostStateType.IN_PROGRESS);
            observer.start();
            CodahaleMetricsUtil.getRegistry().meter(MetricRegistry.name("deployment", processEntry.getEnvironment().getName(), 
                (String) processEntry.getHostname(), "start")).mark();

        } else {
            processEntry.addDeploymentMessage("DEPLOYMENT_ERROR", "Could not set deployment plan, cannot write data on zookeeper");
            processEntry.changeState(HostStateType.ERROR);
            deploymentQueueService.deploymentDone(queueEntry);
        }
    }
    
    public void onNodeDeleted(ZookeeperNode node) {
    	logger.debug("Notifying zookeeper service to unregister node '" + node + "'");
        observer.stop();
        zookeeperService.unregisterNode(node);
        processEntry.changeState(HostStateType.DEPLOYED);

        deploymentQueueService.deployNextHosts(queueEntry);
    }

    public void onNodeData(ZookeeperNode node, Object data) {
        logger.debug("New Message received on node '" + node + "', message: '" + data + "'");
        observer.ping();
        processEntry.addMessage(data.toString());
        inactivityPeriod = MAX_INACTIVE_PERIOD;
    }

    public void onTimeout() {
        observer.stop();
        ZookeeperNode node = zookeeperService.getNodeByName(nodeName);
        if (node != null) {
            zookeeperService.deleteNode(node, true);
        }
        processEntry.addDeploymentMessage("DEPLOYMENT_ERROR", "Did not receive any data from the node for " + (inactivityPeriod / 1000) + " seconds");
        processEntry.changeState(HostStateType.ERROR);
        deploymentQueueService.deployNextHosts(queueEntry);
    }

    public void onNodeUnregistered(ZookeeperNode node) {
        if (observer != null) {
            observer.stop();
        }
    }

    public String getNodeName() {
        return nodeName;
    }

    class DeploymentObserver {
        Thread thread;

        private long lastPingTime;

        public DeploymentObserver() {
            this.lastPingTime = new Date().getTime();
        }

        void start() {
            thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            Thread.sleep(CHECK_INTERVAL);
                            long current = new Date().getTime();
                            if (current - lastPingTime > inactivityPeriod) {
                                onTimeout();
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        //
                    }
                }
            });
            thread.setDaemon(false);
            thread.start();
        }

        void ping() {
            lastPingTime = new Date().getTime();
        }

        void stop() {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                //
            }
        }
    }

}
