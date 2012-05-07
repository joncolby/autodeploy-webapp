package de.mobile.siteops

class AccessControlService {

    def springSecurityService
    SecRole[] allRoles


    static transactional = false

    AccessControlService() {
        allRoles = SecRole.findAll()
    }

    boolean hasWriteAccessForQueue(DeploymentQueue queue) {
        if (queue.environment.secured) {
            if (hasRole("ROLE_ADMIN")) {
                return true
            } else {
                false
            }
        } else {
            true
        }
    }

    String getCurrentUser() {
        def auth = springSecurityService.authentication
        if (!auth) return "(none)"
        def user = auth.name
        if (!user) return "(none)"
        return user
    }

    boolean hasRole(role) {
        def auth = springSecurityService.authentication
        return auth.authorities.collect { it.authority.toLowerCase() }.contains(role.toLowerCase())
    }

    boolean isLoggedIn() {
       return springSecurityService.isLoggedIn()
    }
}
