package de.mobile.siteops

class Host {

    String name
    Date dateCreated
    Date lastUpdated
    Environment environment
    HostClass className

    static constraints = {
        name(unique: ['name', 'environment'], nullable: false, blank: false)
        className(nullable: true)
        environment(nullable: false)
    }

    String toString() {
        return name
    }

    String envAndHost() {
        return environment?.name + "-" + name
    }

}
