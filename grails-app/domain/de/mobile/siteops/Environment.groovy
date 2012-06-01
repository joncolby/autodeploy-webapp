package de.mobile.siteops

class Environment {

    public static enum DeployErrorType { SKIP_HOSTCLASS, ABORT_DEPLOYMENT, IGNORE }

    String name
    Date dateCreated
    Date lastUpdated

    Boolean useHostClassConcurrency = false
    Boolean secured = false
    Boolean releaseMailByDefault = false
    Boolean autoPlayEnabled = false

    DeployErrorType deployErrorType = DeployErrorType.SKIP_HOSTCLASS

    Repository repository
    PropertyAssembler propertyAssembler

    static constraints = {
      name(blank:false,nullable:false,unique:true, validator: { val, obj -> if ( val =~ /\s/ ) return 'default.invalid.whitespace.message' })
      repository(nullable:true)
      propertyAssembler(nullable: false)
    }

	static mapping = {
		sort "name"
	}
	
    String toString() {
        return name
    }
}
