
import groovy.sql.Sql

/**
 * clean autodeploy hosts that no longer exist in autoconf
 */

boolean DEBUG = false
String autodeploySchemaName = args[0]

String autoConfDatabase = "jdbc:mysql://infradbmaster.mobile.rz:3306/inventory?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8"
String autoConfDatabaseUser = "mobile"
String autoConfDatabasePassword = ""

String autoDeployDatabase = "jdbc:mysql://infradbmaster.mobile.rz:3306/${autodeploySchemaName}?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8"
String autoDeployDatabaseUser = "autodeploy"
String autoDeployDatabasePassword = "autodeploy"


def productionEnvironments =  ["Production","AutoActProduction","EMP-Production","MPDD-Production","OpenCmsProduction"]

def invSql = Sql.newInstance(autoConfDatabase, autoConfDatabaseUser, autoConfDatabasePassword, "com.mysql.jdbc.Driver")

def autoDeploySql = Sql.newInstance(autoDeployDatabase, autoDeployDatabaseUser, autoDeployDatabasePassword, "com.mysql.jdbc.Driver")

def autoDeployHosts = []
def autoDeployEnvIds = []
def autoDeployClasses = []
int autoDeployProdEnvId

def autoConfPlatforms = []
def autoDeployPlatforms = []

def inactiveHostClass = autoDeploySql.firstRow('select * from host_class where name= "INACTIVE"')

Date now = new Date()

def log = { message ->
    if (DEBUG) {
        println "[" + new Date() + "] DEBUG: " + message.trim() + ""
    }
}


productionEnvironments.each { environment ->

        try {
        autoDeployProdEnvId = autoDeploySql.firstRow("select id from environment where name = ${environment}").id
        } catch(Exception e) {

        }

        if (autoDeployProdEnvId) {
	   // dont use this since hosts could be added by hand first, and later appear in autoconf.  Then they would be added twice.	
            //autoDeploySql.eachRow("select name from host where environment_id = ${autoDeployProdEnvId} and autoconf_managed = true",{  result ->
            autoDeploySql.eachRow("select name from host where environment_id = ${autoDeployProdEnvId}",{  result ->
                autoDeployHosts << result.name
            })
        }
}

def overrideHostClass = autoDeploySql.firstRow('select * from host_class where name= "AUTOCONF_OVERRIDE"')

def overrideId = overrideHostClass.id

autoDeployHosts.each { host ->
  achost = invSql.firstRow("select server_id from view_assignment_hostdetails where hostname = ${host}")
  
  def class_name_id = autoDeploySql.firstRow('select class_name_id from host where name = ?', host).class_name_id
  def override = class_name_id == overrideId ? true : false
  
   if (!achost) {
        if (!override) {
	   		println "HOST ${host} DOES NOT EXIST IN AUTOCONF.  DELETING IN AUTODEPLOY"
	   		server_id = autoDeploySql.firstRow('select id from host where name = ?',host).id
	   		//println "server id of ${host} = ${server_id}" 	
	   		autoDeploySql.execute("delete from deployed_host where host_id = ${server_id}")
	   		autoDeploySql.execute("delete from host where id = ${server_id} and name = ${host}")   	
   		}
   		else {
   		 	println "HOST ${host} IS IN THE HOST CLASS AUTOCONF_OVERRIDE. NOT REMOVING!"
   		}	   			
   } 

 }