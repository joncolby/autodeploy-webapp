package de.mobile.siteops

class DeploymentQueueEntry {

    ExecutionPlan executionPlan

    String revision

    Date dateCreated
    Date lastUpdated
    Date finalizedDate

    String comment
    String creator
    String executor
    String hostFilter

    int duration

    HostStateType state = HostStateType.QUEUED

    static belongsTo = [queue: DeploymentQueue]

    static transients = ['finalState']

    static constraints = {
        hostFilter(blank: true, nullable: true)
        revision(blank: false, nullable: false)
        state(nullable: false)
        finalizedDate(nullable: true)
        comment(blank: true, nullable: true, maxSize: 1000)
        creator(blank:true,nullable: true)
        executor(blank:true,nullable: true)
    }

    static namedQueries = {
        overview { queue, lastModification ->
            eq("queue", queue)
            or {
                ge("lastUpdated", new Date(lastModification))
                'in'("state", [HostStateType.IN_PROGRESS, HostStateType.QUEUED])
            }
        }
        dashboard { queue ->
            eq("queue", queue)
            'in'("state", [HostStateType.DEPLOYED, HostStateType.QUEUED])
        }
        previousEntries { DeploymentQueueEntry queueEntry ->
            eq("queue", queueEntry.queue)
            and {
                lt("id", queueEntry.id)
                'in'("state", [HostStateType.DEPLOYED, HostStateType.ABORTED, HostStateType.ERROR])
            }
        }
        queuedEntries { DeploymentQueue queue, lastDate ->
            eq("queue", queue)
            ge("dateCreated", lastDate)
            eq("state", HostStateType.QUEUED)

        }
        finalizedEntries { DeploymentQueue queue ->
            eq("queue", queue)
            'in'("state", [HostStateType.DEPLOYED, HostStateType.ABORTED, HostStateType.ERROR])
        }
        processedEntries { DeploymentQueue queue ->
            eq("queue", queue)
            'in'("state", [HostStateType.IN_PROGRESS])
        }
        queuedAndProgressEntries { DeploymentQueueEntry queueEntry ->
            eq("queue", queueEntry.queue)
            or {
                eq("id", queueEntry.id)
                'in'("state", [HostStateType.IN_PROGRESS])
            }
        }
        allEntries { DeploymentQueue queue ->
             eq("queue", queue)
        }
    }

    boolean finalState() {
        return [HostStateType.CANCELLED, HostStateType.ERROR, HostStateType.ABORTED, HostStateType.DEPLOYED].contains(state)
    }

    String toString() {
        return "$executionPlan ($state)"
    }

}
