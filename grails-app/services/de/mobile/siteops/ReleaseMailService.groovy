package de.mobile.siteops

class ReleaseMailService {

    static transactional = false

    def releaseMail(queueEntryId) {
        def queueEntry = DeploymentQueueEntry.load(queueEntryId)

        def model = [:]

        model.team = queueEntry.executionPlan.team.fullName
        model.planName = queueEntry.executionPlan.name
        model.contribution = queueEntry.executionPlan.contribution
        model.ticket = queueEntry.executionPlan.ticket
        model.databaseChanges = queueEntry.executionPlan.databaseChanges
        model.applications = []
        model.multipleRevs = false
        model.creator = queueEntry.creator
        def lastRev = null
        queueEntry.executionPlan.applicationVersions.each { ApplicationVersion app ->
            if (lastRev && lastRev != app.revision) {
                model.multipleRevs = true
            }
            model.applications += [ name: app.application.modulename + " (" + app.application.pillar.name + ")", revision: app.revision]
            lastRev = app.revision
        }
        model.revision = model.multipleRevs ? "(multiple, see applications)" : lastRev

        sendMail {
            from "autodeploy@team.mobile.de"
            to "DL-eBay-TXL-mobile-Technology@ebay.com"
            subject "QA-Approval for $model.team Contribution $model.contribution"
            body( view: "/mail/releaseMail", model: model)
        }
    }

}
