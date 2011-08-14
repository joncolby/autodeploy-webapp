package de.mobile.siteops

class DeploymentQueue {

    Environment environment
    Boolean frozen = false
    Date dateCreated
    Date lastUpdated
 
    static hasMany = [entries:DeploymentQueueEntry]

    static constraints = {
        environment(nullable:false, unique:true)
        entries(nullable:true)
    }

	static mapping = {
		environment sort:'name', order: 'asc'
	}
	
    String toString() {
        return "${environment} Deployment Queue"
    }
}
