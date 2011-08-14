package de.mobile.siteops

class HostClass {

    String name
    String description

    int concurrency = 1

    int priority = 0

    Date dateCreated
    Date lastUpdated

    static hasMany = [applications : Application]

    static constraints = {
        priority(validator: { val, obj -> if ( val < 0 || val > 2 ) return 'default.priority.chosen.message' } )
        concurrency(min:1)
        name(blank:false,nullable:false,unique:true, validator: { val, obj -> if ( val =~ /\s/ ) return 'default.invalid.whitespace.message' } )
        description(blank:true,nullable:true)
        applications(nullable:true)
    }

    static namedQueries = {
            havingApplication {
              applicationId ->
                applications {
                  idEq(applicationId)
                }
          }
    }

    String toString() {
        return name
    }
}
