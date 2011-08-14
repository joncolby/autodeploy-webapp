package de.mobile.siteops

import grails.plugins.springsecurity.Secured

class HostClassController extends ControllerBase {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }
  
    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.sort = "name"
        [hostClassInstanceList: HostClass.list(params), hostClassInstanceTotal: HostClass.count()]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def create = {
        def hostClassInstance = new HostClass()
        hostClassInstance.properties = params
        return [hostClassInstance: hostClassInstance]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def save = {

        params.applications = getChosenApps()

        def hostClassInstance = new HostClass(params)
        if (hostClassInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'hostClass.label', default: 'HostClass'), hostClassInstance.id])}"
            redirect(action: "show", id: hostClassInstance.id)
        }
        else {
            render(view: "create", model: [hostClassInstance: hostClassInstance])
        }
    }

    def show = {
        def hostClassInstance = HostClass.get(params.id)
        if (!hostClassInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'hostClass.label', default: 'HostClass'), params.id])}"
            redirect(action: "list")
        }
        else {
            [hostClassInstance: hostClassInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def edit = {
        def hostClassInstance = HostClass.get(params.id)
        if (!hostClassInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'hostClass.label', default: 'HostClass'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [hostClassInstance: hostClassInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def update = {
        def hostClassInstance = HostClass.get(params.id)
        if (hostClassInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (hostClassInstance.version > version) {
                    
                    hostClassInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'hostClass.label', default: 'HostClass')] as Object[], "Another user has updated this HostClass while you were editing")
                    render(view: "edit", model: [hostClassInstance: hostClassInstance])
                    return
                }
            }

            params.applications = getChosenApps()

            hostClassInstance.properties = params
            if (!hostClassInstance.hasErrors() && hostClassInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'hostClass.label', default: 'HostClass'), hostClassInstance.id])}"
                redirect(action: "show", id: hostClassInstance.id)
            }
            else {
                render(view: "edit", model: [hostClassInstance: hostClassInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'hostClass.label', default: 'HostClass'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])  
    def delete = {
        def hostClassInstance = HostClass.get(params.id)
        if (hostClassInstance) {
            try {

                // cascade delete of hosts belonging to the hostclass ...
                def hostsInClass = Host.findAllByClassName(hostClassInstance)
                hostsInClass.each { Host h ->  h.delete() }

                hostClassInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'hostClass.label', default: 'HostClass'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'hostClass.label', default: 'HostClass'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'hostClass.label', default: 'HostClass'), params.id])}"
            redirect(action: "list")
        }
    }



}
