package de.mobile.siteops

class RestartProcessEntry {

    enum RestartProcessState { IDLE, REQUESTED_RESTART, RESTARTING, FINISHED, ERROR }

    Long queueId
    String hostname
    String statusMessage
    String version
    RestartProcessState state

    void restartRequested() {
        state = RestartProcessState.REQUESTED_RESTART
        statusMessage = "Submitted restart request"
    }

    void restartInProgress() {
        state = RestartProcessState.RESTARTING
        statusMessage = "Agent is restarting"
    }

    void restartDone() {
        state = RestartProcessState.FINISHED
        statusMessage = "Agent restart successful"
        if (version) statusMessage += " (version " + version + ")"
    }

    void restartFailed(message) {
        state = RestartProcessState.ERROR
        statusMessage = message
    }

    void currentVersion(String version) {
        this.version = version
    }

    boolean finished() {
        return (state == RestartProcessState.FINISHED || state == RestartProcessState.ERROR)
    }

    String toString() {
        return "host: $hostname (queueId $queueId), state '$state', message: $statusMessage"
    }

}
