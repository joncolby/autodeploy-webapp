package de.mobile.siteops

import grails.plugins.springsecurity.Secured

class ApplicationController extends ControllerBase {

    def dataSource

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [applicationInstanceList: Application.list(params), applicationInstanceTotal: Application.count()]
    }

    // needed anymore ???
    def listNotAssigned = {
        def sql = new groovy.sql.Sql(dataSource)
        def applications = []
        sql.eachRow("""
                select a.id from application a left join host_class_applications h on  h.application_id = a.id where h.host_class_id is null
              """, { row ->
                  applications << Application.get(row.id)
              } )
        params.max = applications.size()
        render(view: "list", model:[applicationInstanceList: applications, applicationInstanceTotal: applications.size()])
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def create = {
        def applicationInstance = new Application()
        applicationInstance.properties = params
        return [applicationInstance: applicationInstance]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def save = {
        def applicationInstance = new Application(params)


            def hostClasses = getChosenHostClasses()

            def currentHostClasses = applicationInstance.hostclasses

            currentHostClasses.each { HostClass hc ->
              hc.removeFromApplications(applicationInstance)
            }

            hostClasses.each { HostClass hc ->
              hc.addToApplications(applicationInstance)
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

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
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

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
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

            def hostClasses = getChosenHostClasses()

            def currentHostClasses = applicationInstance.hostclasses

            currentHostClasses.each { HostClass hc ->
              hc.removeFromApplications(applicationInstance)
            }

            hostClasses.each { HostClass hc ->
              hc.addToApplications(applicationInstance)
            }

            applicationInstance.properties = params
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

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
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
