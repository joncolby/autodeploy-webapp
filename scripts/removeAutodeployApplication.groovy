import groovy.sql.Sql

// cleanup autodeploy junk in database

String dataSource = "jdbc:mysql://infradbmaster.mobile.rz:3306/autodeploy_mobile?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8"
String dataSourceUser = "autodeploy"
String dataSourcePassword = "autodeploy"


def sql = Sql.newInstance(dataSource, dataSourceUser, dataSourcePassword, "com.mysql.jdbc.Driver")

if (!this.args) {
    println "missing application id argument!"
    System.exit(1)
}
def appId = this.args[0]


println "app id " + appId
    

sql.execute("delete from deployment_plan_application where application_id = ${appId}")
sql.execute("delete from execution_plan_application_version where application_version_id in (select id from application_version where application_id = ${appId})")
sql.execute("delete from application_version where application_id = ${appId}")
sql.execute("delete from application where id = ${appId}")
sql.execute("delete from host_class_applications where application_id = ${appId}")

println "done removing application with id ${appId}"
