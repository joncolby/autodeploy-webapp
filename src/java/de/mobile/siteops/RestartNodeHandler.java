package de.mobile.siteops;

import de.mobile.zookeeper.AbstractNodeHandler;
import de.mobile.zookeeper.ZookeeperNode;
import de.mobile.zookeeper.ZookeeperService;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class RestartNodeHandler extends AbstractNodeHandler {

    private static final int CHECK_INTERVAL = 30000; // 30 seconds

    private final String nodeName;

    private final RestartProcessEntry processEntry;

    private final RestartAgentsService restartAgentsService;

    private final ZookeeperService zookeeperService;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final RestartObserver observer;

    public RestartNodeHandler(String nodeName, RestartProcessEntry processEntry, ZookeeperService zookeeperService, RestartAgentsService restartAgentsService) {
        this.nodeName = nodeName;
        this.processEntry = processEntry;
        this.zookeeperService = zookeeperService;
        this.restartAgentsService = restartAgentsService;
        this.observer = new RestartObserver();
    }

    public void onNodeInitialized(final ZookeeperNode node) {
        if (node != null) {
            if (node.exists()) {
                executorService.schedule(new Runnable() {
                    public void run() {
                        System.err.println("SET DATA for node " + node);
                        node.setData("restart - " + new Date().toString());
                    }
                }, 5000, TimeUnit.MILLISECONDS);
                processEntry.restartRequested();
                observer.start();
            } else {
                processEntry.restartFailed("Node is not registered in zookeeper (most likely agent is not running)");
                zookeeperService.unregisterNode(node);
                restartAgentsService.agentRestartFinished(processEntry);
            }
        }
    }

    public void onNodeCreated(final ZookeeperNode node) {
        // agent came back
        observer.stop();
        executorService.schedule(new Runnable() {
            public void run() {
                processEntry.restartDone();
                restartAgentsService.agentRestartFinished(processEntry);
                zookeeperService.unregisterNode(node);
            }
        }, 2000, TimeUnit.MILLISECONDS);
    }

    public void onNodeDeleted(ZookeeperNode node) {
        // seems agent is restarting
        processEntry.restartInProgress();
    }

    public void onNodeUnregistered(ZookeeperNode node) {
        if (observer != null) {
            observer.stop();
        }
    }

    public void onNodeData(ZookeeperNode node, Object data) {
        System.err.println("Recieved data for " + node + " : " + data);
        processEntry.currentVersion(data.toString());
    }

    public String getNodeName() {
        return nodeName;
    }

    private static void sleep(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {}
    }

    class RestartObserver {
        Thread thread;

        void start() {
            thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            Thread.sleep(CHECK_INTERVAL);
                            if (!processEntry.finished()) {
                                processEntry.restartFailed("Agent did not came back after " + CHECK_INTERVAL + "ms");
                                restartAgentsService.agentRestartFinished(processEntry);
                                ZookeeperNode node = getNode();
                                if (node != null) {
                                    zookeeperService.unregisterNode(node);
                                }
                            }
                            break;
                        }
                    } catch (InterruptedException e) {
                        //
                    }
                }
            });
            thread.setDaemon(false);
            thread.start();
        }

        void stop() {
            if (thread != null) {
                thread.interrupt();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    //
                }
            }
        }
    }

}
