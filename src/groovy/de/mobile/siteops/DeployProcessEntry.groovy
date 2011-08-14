package de.mobile.siteops

import grails.converters.JSON

import static HostStateType.*
import de.mobile.siteops.Environment.DeployErrorType
import org.codehaus.groovy.grails.web.json.JSONObject

class DeployProcessEntry {

	DeploymentQueueService queueService
	
	DeploymentQueueEntry queueEntry
	Host host 
	def hostname
	def hostid
	def hostclass
	Environment environment
	String deploymentPlan
	HostStateType state = QUEUED
    DeployErrorType deployErrorType
    boolean useHostClassConcurrency
	int avgDuration
	long startTime
	long endTime
	long timestamp
	int priority
	def messages = []

    def updateSelf() {
        timestamp = new Date().time
    }

	void changeState(HostStateType state) {
		if (state == IN_PROGRESS) {
			startTime = new Date().time
		} else if ([ERROR, DEPLOYED, CANCELLED, ABORTED].contains(state)) {
			endTime = new Date().time
		}
        updateSelf()
		this.state = state
	}

    void addDeploymentMessage(status, message) {
        def id = messages.size() > 0 ? messages[-1].id + 1 : 1
		messages += new AgentMessage(id: id, status: status, date: new Date().format("yyyy-MM-dd HH:mm:ss"), message: message, identifier: "none")
        updateSelf()
    }

	void addMessage(String message) {
		def msg = JSON.parse(message)
		if (!msg) return
        def id = messages.size() > 0 ? messages[-1].id + 1 : 1
		messages += new AgentMessage(id: id, status: msg.status, date: msg.date, message: msg.message, identifier: msg.identifier)
        updateSelf()
	}

	void addMessages(String json) {
		if (!json) return
		def result = JSON.parse(json)
        for (String messageStr:result) {
            def msg = new AgentMessage(messageStr)
            msg.timestamp = timestamp
			messages += msg
        }
        updateSelf()
	}
	
	int processTime() {
		return startTime > 0 && startTime < endTime ? endTime - startTime : 0
	}
	
	int progress() {
		if (startTime > 0 && avgDuration > 0 && state == IN_PROGRESS) {
			def duration = new Date().time - startTime
			def progress = (duration * 100 / avgDuration) as int
			return progress <= 100 ? progress : 100
		}
		return 0;
	}
	
	String duration() {
        if (avgDuration == 0) return "unknown"
		if (startTime > 0 && state == IN_PROGRESS) {
			def duration = new Date().time - startTime
			return duration <= avgDuration ? TimeUtils.formatDuration(avgDuration - duration) + " left" : "overtime " + TimeUtils.formatDuration(duration - avgDuration) 
		} else {
			return processTime() > 0 ? TimeUtils.formatDuration(processTime()) : TimeUtils.formatDuration(avgDuration)
		}
	}

	String formatAvgDuration() {
		return TimeUtils.formatDuration(avgDuration)
	}

	HostStateType getState() {
		def msgError = messages.find { it instanceof AgentMessage && it.isError() }
		if (msgError && state != CANCELLED && state != ABORTED) {
			return ERROR
		} else {
			return state
		}
	}

    def getApplications() {
        def result = []
        if (hostclass && hostclass.apps) {
            hostclass.apps.sort { a,b -> a.name.compareTo(b.name) }.each { result += it }
        }

        return result
    }

	boolean newerThan(timestamp) {
		return !timestamp || state == IN_PROGRESS || timestamp < this.timestamp
	}
		
	String messagesAsJSON() {
		return messages.collect { [id: it.id, status: it.status, date: it.date, identifier: it.identifier, message: it.message] } as JSON
	}
	
	String fullString() {
		return "hostname: $hostname, env: $environment.name, hostclass: $hostclass, state: $state, startTime: $startTime, endTime: $endTime, avgDuration: $avgDuration, useHostClassConcurrency: $useHostClassConcurrency, deployErrorType: $deployErrorType, priority: $priority, messages: $messages"
	}

    String toString() {
        return "hostname: $hostname, state: $state, useHostClassConcurrency: $useHostClassConcurrency, priority: $priority"
    }

}
