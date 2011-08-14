package de.mobile.siteops

class Instance {

    String name
    Host host

    static hasMany = [applications:Application]

    static constraints = {
         name(unique: ['name','host'],nullable:false,blank:false)
         host(nullable:false)
         applications(nullable:true)
    }

    String toString() {
        return name
    }

}
