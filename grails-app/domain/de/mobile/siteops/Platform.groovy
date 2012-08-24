package de.mobile.siteops

class Platform {

    String name
    String description

    Date dateCreated
    Date lastUpdated

    static constraints = {
        name(unique: true, nullable:false,blank:false)
        description(nullable:true,blank:true)
    }

    String toString() {
        return name
    }
}
