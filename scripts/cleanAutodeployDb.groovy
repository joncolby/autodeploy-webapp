import groovy.sql.Sql

// cleanup autodeploy junk in database

String dataSource = ""
String dataSourceUser = ""
String dataSourcePassword = ""


def sql = Sql.newInstance(dataSource, dataSourceUser, dataSourcePassword, "com.mysql.jdbc.Driver")

def queueEntries = []

def queues = []

sql.eachRow("select id from deployment_queue",{
    queues << it.id
})

// delete any queue entries and deployed hosts older than x months (3 months)
queues.each { queue ->
    println "getting queue entries for queue: ${queue}"
    sql.eachRow("select id from deployment_queue_entry where queue_id = ${queue} and MONTH(date_created) < MONTH(now()) - 3",{
        queueEntries << it.id
    })

}

queueEntries.each { entry ->
    println "entry -> " + entry

    sql.eachRow("select id from deployed_host where entry_id = ${entry}",{
        def deployedHostId = it.id
        println "deleting deploy_host: " + deployedHostId
        sql.execute("delete from deployed_host where id = ${deployedHostId}")
    })

    println "deleting queue_entry -> " + entry

    sql.execute("delete from deployment_queue_entry where id = ${entry}")

}

println "cleaning up application versions table ..."

def appIds = []

sql.eachRow("select application_id from application_version group by application_id",{
   println "processing application id ${it.application_id}"
   appIds << it.application_id
})

def keepVersionsCount = 50
appIds.each { app ->

    def versions = []
    sql.eachRow("select id from application_version where application_id = ${app}",{
        versions << it.id
    })

    if (versions.size() > keepVersionsCount) {
        versions.sort().take(versions.size() - keepVersionsCount).each { version ->
             println "deleting version ${version}"
             sql.execute("delete from execution_plan_application_version where application_version_id = ${version}")
             sql.execute("delete from application_version where id = ${version}")
        }
    }
}
