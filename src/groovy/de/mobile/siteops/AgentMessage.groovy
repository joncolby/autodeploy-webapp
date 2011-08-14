package de.mobile.siteops

class AgentMessage {

    def id
	def status
	def date
	def identifier
	def message 
	def timestamp = new Date().time
	
	boolean isError() {
		return status == 'SCRIPT_ERROR' || status == 'AGENT_ERROR' || status == 'DEPLOYMENT_ERROR'
	}
	
	boolean newerThan(timestamp) {
		return !timestamp || timestamp < this.timestamp
	}
	
	String toString() {
		return "$date - $status - $identifier - $message"
	}
}
