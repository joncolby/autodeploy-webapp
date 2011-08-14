package de.mobile.siteops

import grails.plugins.springsecurity.Secured

class InstanceController extends ControllerBase {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [instanceInstanceList: Instance.list(params), instanceInstanceTotal: Instance.count()]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def create = {
        def instanceInstance = new Instance()
        instanceInstance.properties = params

        def hostInstance = Host.get(params.id)
        return [instanceInstance: instanceInstance, hostInstance: hostInstance]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def save = {

        params.applications = getChosenApps()
      
        def instanceInstance = new Instance(params)
        if (instanceInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'instance.label', default: 'Instance'), instanceInstance.id])}"
            //redirect(action: "show", id: instanceInstance.id)
            redirect(action:"show",controller: "host", id: instanceInstance?.host?.id)
        }
        else {
            render(view: "create", model: [id: instanceInstance?.host?.id,instanceInstance: instanceInstance])
        }
    }

    def show = {
        def instanceInstance = Instance.get(params.id)
        if (!instanceInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])}"
            redirect(action: "list")
        }
        else {
            [instanceInstance: instanceInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def edit = {
        def instanceInstance = Instance.get(params.id)
        if (!instanceInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [instanceInstance: instanceInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def update = {
        def instanceInstance = Instance.get(params.id)
        if (instanceInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (instanceInstance.version > version) {
                    
                    instanceInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'instance.label', default: 'Instance')] as Object[], "Another user has updated this Instance while you were editing")
                    render(view: "edit", model: [instanceInstance: instanceInstance])
                    return
                }
            }
            params.applications = getChosenApps()
            instanceInstance.properties = params
            if (!instanceInstance.hasErrors() && instanceInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'instance.label', default: 'Instance'), instanceInstance.id])}"
                redirect(action: "show", id: instanceInstance.id)
            }
            else {
                render(view: "edit", model: [instanceInstance: instanceInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def delete = {
        def instanceInstance = Instance.get(params.id)
        if (instanceInstance) {
            try {
                instanceInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])}"
                //redirect(action: "list")
                redirect(action:"show", controller: "host", id: instanceInstance?.host?.id)

            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])}"
            redirect(action: "list")
        }
    }
}
