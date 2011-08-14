package de.mobile.siteops

import groovy.time.*

class TimeUtils {

	private static def formatTimeCategory(def tc) {
		def list = []
		if (tc.hours != 0) list << "$tc.hours hr"
		if (tc.minutes != 0) list << "$tc.minutes min"
		if (tc.seconds != 0) list << "$tc.seconds sec"

		def result = list.join(' ')
		return result ? result : "0 sec"
	}
	
	static def formatDuration(def duration) {
		def start = new Date()
		def end = new Date(start.time + duration)
		def tc = TimeCategory.minus(end, start)

		return formatTimeCategory(tc)
	}
	
	static def durationBetween(def start, def end) {
		return formatTimeCategory(TimeCategory.minus(end, start))
	}
	
}

