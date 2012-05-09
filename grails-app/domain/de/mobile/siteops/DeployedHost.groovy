package de.mobile.siteops

import java.util.Date;

class DeployedHost {

	DeploymentQueueEntry entry
	Host host
	Environment environment
	Integer duration
	Integer priority = 0
	HostStateType state
	String message

    static belongsTo = [ DeploymentQueueEntry ]

	static constraints = {
		host(nullable:false,unique:['entry', 'host'])
		environment(nullable: false)
		entry(nullable:false)
		state(nullable:false)
		priority(nullable:false)
		message(nullable:true,blank:true)
	}

    static mapping = {
        version false
    }

	static namedQueries = {
		avgDurationByHosts { hosts ->
			'in'("host", hosts)
			and {
				eq("state", HostStateType.DEPLOYED)
			}
			projections {
				property("host")
				avg("duration")
				groupProperty("host")
			}
		}
	}

	String toString() {
		return "id: $id, host: ${host.name}"
	}
}
