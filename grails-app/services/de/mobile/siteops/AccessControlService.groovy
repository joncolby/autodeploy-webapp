package de.mobile.siteops

class AccessControlService {

    def springSecurityService
    SecRole[] allRoles


    static transactional = false

    AccessControlService() {
        allRoles = SecRole.findAll()
    }

    boolean hasWriteAccessForQueue(DeploymentQueue queue) {
        if (queue.environment.name == 'Production') {
            if (hasRole("ROLE_ADMIN")) {
                return true
            } else {
                false
            }
        } else {
            true
        }
    }

    boolean hasRole(role) {
        def auth = springSecurityService.authentication
        return auth.authorities.collect { it.authority.toLowerCase() }.contains(role.toLowerCase())
    }
}
