package de.mobile.siteops

class ExecutionPlan {

    public static enum PlanType { NORMAL, ROLLBACK, REDEPLOY, RETRY, SYNC }

    String name
    String contribution
    String ticket
    PlanType planType = PlanType.NORMAL
    Team team

    Date dateCreated
    Date lastUpdated

    boolean forceDeploy = false

    static belongsTo = [ DeploymentQueueEntry ]

    static hasMany = [applicationVersions: ApplicationVersion]

    static transients = ['outputName']

    static mapping = {
        version false
    }

    String outputName() {
        def result = ""
        if (planType == PlanType.RETRY) {
            result = "Retry of " + name
        } else if (planType == PlanType.ROLLBACK) {
            result = "Rollback of " + name
        } else if (planType == PlanType.REDEPLOY) {
            result = "Redeploy of " + name
        } else {
            result = name
        }
        return result
    }

    String toString() {
        return "$name - $planType - ($team)"
    }
}
