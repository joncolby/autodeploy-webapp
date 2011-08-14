package de.mobile.siteops

import grails.plugins.springsecurity.Secured

class ApplicationController {

    def dataSource

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [applicationInstanceList: Application.list(params), applicationInstanceTotal: Application.count()]
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def create = {
        def applicationInstance = new Application()
        applicationInstance.properties = params
        return [applicationInstance: applicationInstance]
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def save = {
        def applicationInstance = new Application(params)

        def selectedHostclassIds = params.getList('hostclasses').collect { it as Long }
        if (selectedHostclassIds) {
            def selectedHostclasses = HostClass.findAllByIdInList(selectedHostclassIds)
            if (selectedHostclasses && !selectedHostclasses.isEmpty()) {
                selectedHostclasses.each {
                    if (applicationInstance.hostclasses == null || !applicationInstance.hostclasses.contains(it)) {
                        applicationInstance.addToHostclasses(it)
                    }
                }
            }
        }

        if (applicationInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'application.label', default: 'Application'), applicationInstance.id])}"
            redirect(action: "show", id: applicationInstance.id)
        }
        else {
            render(view: "create", model: [applicationInstance: applicationInstance])
        }
    }

    def show = {
        def applicationInstance = Application.get(params.id)
        if (!applicationInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'application.label', default: 'Application'), params.id])}"
            redirect(action: "list")
        }
        else {
            [applicationInstance: applicationInstance]
        }
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def edit = {
        def applicationInstance = Application.get(params.id)
        if (!applicationInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'application.label', default: 'Application'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [applicationInstance: applicationInstance]
        }
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def update = {
        def applicationInstance = Application.get(params.id)
        if (applicationInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (applicationInstance.version > version) {

                    applicationInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'application.label', default: 'Application')] as Object[], "Another user has updated this Application while you were editing")
                    render(view: "edit", model: [applicationInstance: applicationInstance])
                    return
                }
            }

            def removeHostsclasses = []
            applicationInstance.hostclasses.each { removeHostsclasses += it }
            removeHostsclasses.each {
                applicationInstance.removeFromHostclasses(it)
            }

            applicationInstance.properties = params
            def selectedHostclassIds = params.getList('hostclasses').collect { it as Long }
            if (selectedHostclassIds) {
                def selectedHostclasses = HostClass.findAllByIdInList(selectedHostclassIds)
                if (selectedHostclasses && !selectedHostclasses.isEmpty()) {
                    selectedHostclasses.each {
                        if (applicationInstance.hostclasses == null || !applicationInstance.hostclasses.contains(it)) {
                            applicationInstance.addToHostclasses(it)
                        }
                    }
                }
            }

            if (!applicationInstance.hasErrors() && applicationInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'application.label', default: 'Application'), applicationInstance.id])}"
                redirect(action: "show", id: applicationInstance.id)
            }
            else {
                render(view: "edit", model: [applicationInstance: applicationInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'application.label', default: 'Application'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def delete = {
        def applicationInstance = Application.get(params.id)
        if (applicationInstance) {
            try {
                applicationInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'application.label', default: 'Application'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'application.label', default: 'Application'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'application.label', default: 'Application'), params.id])}"
            redirect(action: "list")
        }
    }

}
