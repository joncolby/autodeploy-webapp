package de.mobile.siteops

class Application {

    enum LoadBalancerType { NONE, MODJK, NETSCALER }

    enum ApplicationType { TANUKI_TOMCAT, TANUKI_DAEMON, STATIC_CONTENT, UNPACK_AND_SYMLINK, TANUKI_JAR, UNPACK_ONLY, COPY_AND_SYMLINK }

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
    String propertiesPath
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
		propertiesPath(blank:false,nullable: true)
		artifactId(blank:false,nullable:true)
		groupId(blank:false,nullable:true)
		description(blank:false,nullable:true)
		context(blank:false,nullable:true)
		releaseInfoJMXBean(blank:false,nullable:true)
		releaseInfoJMXAttribute(blank:false,nullable:true)
		type(nullable:false )
		marketPlace(nullable:false )
	}

	String suffix() {
		def filename = this.filename
		return filename.substring(filename.indexOf(".") + 1, filename.length())
	}

	String toString() {
		return "$filename - $pillar"
	}
}
