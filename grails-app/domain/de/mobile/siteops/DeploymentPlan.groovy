package de.mobile.siteops

import java.text.SimpleDateFormat;
import java.util.Date;

class DeploymentPlan {

    String name
    String contribution
    String ticket
  
    Boolean requiresDatabaseChanges = false
    Boolean requiresPropertyChanges = false

    Date dateCreated
    Date lastUpdated

    static hasMany = [ applications:Application ]

    static belongsTo = [team:Team]

    static constraints = {
        contribution(blank:false,nullable:false,maxSize:1000)
        ticket(blank:false,nullable:false)
        team(nullable: false)
        applications(nullable:false)
        name(nullable:false,blank:false, unique: true)
    }

    String toString() {
        return name
    }

    static mapping = {
        sort "name"
    }
}
