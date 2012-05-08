package de.mobile.siteops

class ExecutionPlan {

    public static enum PlanType { NORMAL, ROLLBACK, REDEPLOY, RETRY, SYNC }

    String name
    String contribution
    String ticket
    PlanType planType = PlanType.NORMAL
    Team team
    Repository repository
    Boolean databaseChanges = false
    String user

    Date dateCreated
    Date lastUpdated

    boolean forceDeploy = false

    static belongsTo = [ DeploymentQueueEntry ]

    static hasMany = [applicationVersions: ApplicationVersion]

    static transients = ['outputName']

    static constraints = {
        repository(nullable: true)
        user(blank:true,nullable: true)
    }
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

    static ExecutionPlan copyFrom(ExecutionPlan source) {
        return new ExecutionPlan(name: source.name, contribution: source.contribution, ticket: source.ticket ? source.ticket : "", databaseChanges: source.databaseChanges, team: source.team, planType: source.planType, applicationVersions: [], user: source.user)
    }

    String toString() {
        return "$name - $planType - ($team)"
    }
}
