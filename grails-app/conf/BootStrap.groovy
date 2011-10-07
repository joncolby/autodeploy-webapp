import javax.servlet.http.HttpServletRequest
import de.mobile.siteops.SecRole
import de.mobile.siteops.SecUser
import de.mobile.siteops.SecUserSecRole

class BootStrap {

    def zookeeperHandlerService

    def springSecurityService

    def init = { servletContext ->

        log.info("Entering bootstrap init")
        zookeeperHandlerService.init()


        def userRole = SecRole.findByAuthority('ROLE_USER') ?: new SecRole(authority: 'ROLE_USER').save(failOnError: true)
        def adminRole = SecRole.findByAuthority('ROLE_ADMIN') ?: new SecRole(authority: 'ROLE_ADMIN').save(failOnError: true)
        def readProdRole = SecRole.findByAuthority('ROLE_PROD_READ') ?: new SecRole(authority: 'ROLE_PROD_READ').save(failOnError: true)
        def writeProdRole = SecRole.findByAuthority('ROLE_PROD_WRITE') ?: new SecRole(authority: 'ROLE_PROD_WRITE').save(failOnError: true)

        def adminUser = SecUser.findByUsername('admin') ?: new SecUser(
                username: 'admin',
                password: springSecurityService.encodePassword('test123'),
                enabled: true).save(failOnError: true)

        if (!adminUser.authorities.contains(adminRole)) {
            SecUserSecRole.create adminUser, adminRole
        }


        HttpServletRequest.metaClass.isXhr = {->
            'XMLHttpRequest' == delegate.getHeader('X-Requested-With')
        }

        log.info("Leaving bootstrap init")
    }
    def destroy = {
    }
}
