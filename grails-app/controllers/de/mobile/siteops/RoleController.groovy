package de.mobile.siteops

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class RoleController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [roleInstanceList: SecRole.list(params), roleInstanceTotal: SecRole.count()]
    }

    def create = {
        def roleInstance = new SecRole()
        roleInstance.properties = params
        return [roleInstance: roleInstance]
    }

    def save = {
        def roleInstance = new SecRole(params)
        if (roleInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'role.label', default: 'Role'), roleInstance.id])}"
            redirect(action: "show", id: roleInstance.id)
        }
        else {
            render(view: "create", model: [roleInstance: roleInstance])
        }
    }

    def show = {
        def roleInstance = SecRole.get(params.id)
        if (!roleInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), params.id])}"
            redirect(action: "list")
        }
        else {
            [roleInstance: roleInstance]
        }
    }

    def edit = {
        def roleInstance = SecRole.get(params.id)
        if (!roleInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [roleInstance: roleInstance]
        }
    }

    def update = {
        def roleInstance = SecRole.get(params.id)
        if (roleInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (roleInstance.version > version) {
                    
                    roleInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'role.label', default: 'Role')] as Object[], "Another user has updated this Role while you were editing")
                    render(view: "edit", model: [roleInstance: roleInstance])
                    return
                }
            }
            roleInstance.properties = params
            if (!roleInstance.hasErrors() && roleInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'role.label', default: 'Role'), roleInstance.id])}"
                redirect(action: "show", id: roleInstance.id)
            }
            else {
                render(view: "edit", model: [roleInstance: roleInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def roleInstance = SecRole.get(params.id)
        if (roleInstance) {
            try {
                roleInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'role.label', default: 'Role'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'role.label', default: 'Role'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), params.id])}"
            redirect(action: "list")
        }
    }
}
