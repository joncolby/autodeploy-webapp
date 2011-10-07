package de.mobile.siteops

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class UserAdminController {

    def springSecurityService

    def index = {
        def model = [users: SecUser.findAll().collect { [name: it.username, roles: it.authorities.collect { role -> role.authority }, entry: it]} ]

        println model.users

        [model: model]
    }

    def create = {
        def username = params.username
        def password = params.password
        def roleStr = params.roles

        if (!username) {
            render "<h1>No username parameter defined</h1>"
            return
        }

        if (!password) {
            render "<h1>No password parameter defined</h1>"
            return
        }

        if (!roleStr) {
            render "<h1>No roles parameter defined</h1>"
            return
        }

        def roles = roleStr.split(",").collect { it.toLowerCase() }
        def allRoles = SecRole.findAll()

        def secUser = SecUser.findByUsername(username)
        if (!secUser) {
            secUser = new SecUser(username: username, password: springSecurityService.encodePassword(password), enabled: true)
            if (!secUser.save()) {
                render "<h1>Could not save user, maybe already exists?</h1>"
                return
            }

            allRoles.each {
                if (roles.contains(it.authority.toLowerCase())) {
                    if (!secUser.authorities.contains(it)) {
                        SecUserSecRole.create(secUser, it)
                    }
                }
            }
        } else {
            render "<h1>User already exists, use modify to change user</h1>"
            return
        }

        render "<h1>User $username created</h1>"
    }

    def modify = {
        def username = params.username
        def password = params.password
        def roleStr = params.roles
        def roles = roleStr ? roleStr.split(",").collect { it.toLowerCase() } : []
        def allRoles = SecRole.findAll()

        if (!username) {
            render "<h1>No username parameter defined</h1>"
            return
        }

        def secUser = SecUser.findByUsername(username)
        if (!secUser) {
            render "<h1>Could not find user $username</h1>"
            return
        }

        def updated = []

        if (password) {
            secUser.password = springSecurityService.encodePassword(password)
            secUser.save()
            updated += "PASSWORD"
        }

        if (roles) {
            SecUserSecRole.removeAll(secUser)
            allRoles.each {
                if (roles.contains(it.authority.toLowerCase())) {
                    if (!secUser.authorities.contains(it)) {
                        SecUserSecRole.create(secUser, it)
                    }
                }
            }
            updated += "ROLES"
        }
        render "<h1>Updated $username (" + updated.join(",") + ")</h1>"
    }

    def remove = {
        def model = [:]
        def user = SecUser.findById(params.id)
        if (user.username != 'admin') {
            SecUserSecRole.removeAll(user)
            user.delete()
            model.message = "Deleted user $user.username"
        } else {
            model.message = "Could not delete admin user"
        }
        [model: model]
    }
}
