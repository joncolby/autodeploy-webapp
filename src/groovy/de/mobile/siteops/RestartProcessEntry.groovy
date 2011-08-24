package de.mobile.siteops

class RestartProcessEntry {

    enum RestartProcessState { IDLE, REQUESTED_RESTART, RESTARTING, FINISHED, ERROR }

    Long queueId
    String hostname
    String statusMessage
    RestartProcessState state

    void restartRequested() {
        state = RestartProcessState.REQUESTED_RESTART
        statusMessage = "Submitted restart request"
    }

    void restartInProgress() {
        state = RestartProcessState.RESTARTING
        statusMessage = "Agent ist restartung"
    }

    void restartDone() {
        state = RestartProcessState.FINISHED
        statusMessage = "Agent restart completed successful"
    }

    void restartFailed(message) {
        state = RestartProcessState.ERROR
        statusMessage = message
    }

    boolean finished() {
        return (state == RestartProcessState.FINISHED || state == RestartProcessState.ERROR)
    }

    String toString() {
        return "host: $hostname (queueId $queueId), state '$state', message: $statusMessage"
    }

}
