package de.mobile.siteops

class Pillar {

    String name
    String description

    Date dateCreated
    Date lastUpdated

    static hasMany = [applications : Application]

    static constraints = {
        name(blank:false, nullable:false, unique:true, validator: { val, obj -> if ( val =~ /\s/ ) return 'default.invalid.whitespace.message' })
        description(blank:true,nullable:true)
        applications(nullable:true)
    }

    String toString() {
        return name
    }
}
