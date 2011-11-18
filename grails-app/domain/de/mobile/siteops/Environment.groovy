package de.mobile.siteops

class Environment {

    public static enum DeployErrorType { SKIP_HOSTCLASS, ABORT_DEPLOYMENT, IGNORE }

    String name
    Date dateCreated
    Date lastUpdated

    Boolean useHostClassConcurrency = false
    DeployErrorType deployErrorType = DeployErrorType.SKIP_HOSTCLASS

    Repository repository
    PropertyAssembler propertyAssembler

    static constraints = {
      name(blank:false,nullable:false,unique:true, validator: { val, obj -> if ( val =~ /\s/ ) return 'default.invalid.whitespace.message' })
      repository(nullable:true)
    }

	static mapping = {
		sort "name"
	}
	
    String toString() {
        return name
    }
}
