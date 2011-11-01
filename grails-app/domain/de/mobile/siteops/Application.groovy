package de.mobile.siteops

class Application {

    enum LoadBalancerType { NONE, MODJK, NETSCALER }

    enum ApplicationType { TANUKI_TOMCAT, TANUKI_DAEMON, STATIC_CONTENT, TARBALL, UNPACK_ONLY, TANUKI_JAR, JAR_DOWNLOAD }

    enum MarketPlace { FRANCE, GERMANY, ITALY, POLAND, ROMANIA }

	def dataSource

	String filename
    String downloadName
    String modulename
	String description
	ApplicationType type
    LoadBalancerType balancerType = LoadBalancerType.NONE
    MarketPlace marketPlace = MarketPlace.GERMANY   // default marketplace
    String startStopScript
	String releaseInfoJMXBean
	String releaseInfoJMXAttribute
	String context
	String installDir
	String artifactId
	String groupId
	Boolean startOnDeploy = true
	Boolean assembleProperties = true
	Boolean instanceProperties = true
	Boolean doProbe = true

	Date dateCreated
	Date lastUpdated

	Pillar pillar

	static belongsTo = HostClass

	static hasMany =  [hostclasses: HostClass]

	static mapping = { sort filename:"asc" }

	static constraints = {
		pillar(nullable:false)
		filename(blank:false,nullable:false)
		downloadName(blank:false,nullable:false)
        modulename(blank:false,nullable:false)
        startStopScript(blank:true,nullable: true)
		installDir(blank:false,nullable: true)
		artifactId(blank:false,nullable:true)
		groupId(blank:false,nullable:true)
		description(blank:false,nullable:true)
		context(blank:false,nullable:true)
		releaseInfoJMXBean(blank:false,nullable:true)
		releaseInfoJMXAttribute(blank:false,nullable:true)
		type(blank:false,nullable:false )
		marketPlace(blank:false,nullable:false )
	}

	String suffix() {
		def filename = this.filename
		return filename.substring(filename.indexOf(".") + 1, filename.length())
	}

	String toString() {
		return "$filename - $pillar"
	}
}
