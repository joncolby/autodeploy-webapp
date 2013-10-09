package de.mobile.siteops

class ArchiveJob {
    //def timeout = 5000l // execute job once in 5 seconds

    def archiveService

    static triggers = {
       //cron name: 'poller', startDelay: 10000, cronExpression: "0 0/60 * * * ?"
       simple name:'initArchiveJob', startDelay:10000, repeatCount: 0
       cron name: 'monthlyArchiveJob', startDelay: 10000, cronExpression: "0 0 5 1 * ?"
    }

    def execute() {
        // execute task
        def calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        archiveService.archive(calendar)

    }
}
