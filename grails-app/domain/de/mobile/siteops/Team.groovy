package de.mobile.siteops

class Team {

    String shortName
    String fullName
    String description

    static hasMany = [plans:DeploymentPlan]

    Date dateCreated
    Date lastUpdated

    static constraints = {
        shortName(blank:false,nullable:false, validator: { val, obj -> if ( val =~ /\s/ ) return 'default.invalid.whitespace.message' })
        fullName(blank:false,nullable:false)
        description(blank:false,nullable:true)
    }

      String toString() {
        return fullName + " (" + shortName + ")"
    }
}
