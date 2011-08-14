package de.mobile.siteops

class Repository {

    String name
    String baseUrl
    String type = "NEXUS"

    Date dateCreated
    Date lastUpdated

    static constraints = {
      name(blank:false, nullable:false, validator: { val, obj -> if ( val =~ /\s/ ) return 'default.invalid.whitespace.message' })
      baseUrl(blank:true,nullable:true)
      type(inList:["MOUNTED_FILESYSTEM", "ARCHIVA", "NEXUS" ], nullable:false )
    }

    String toString() {
        return name
    }
}
