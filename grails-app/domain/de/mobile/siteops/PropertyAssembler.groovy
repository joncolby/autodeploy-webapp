package de.mobile.siteops

class PropertyAssembler {

    String name
    String configAssemblerUrl

    Date dateCreated
    Date lastUpdated

    static constraints = {
        name(blank:false, nullable:false)
        configAssemblerUrl(blank:true,nullable:true)
    }

    String toString() {
        return "$name ($configAssemblerUrl)"
    }
}
