package de.mobile.siteops

class ApplicationVersion {

    Application application
    String revision

    static belongsTo = [ ExecutionPlan ]

    static mapping = {
        version false
    }

    static constraints = {
    }

    String toString() {
        return application?.filename + " (pillar " + application?.pillar +") - rev: " + revision
    }

}
