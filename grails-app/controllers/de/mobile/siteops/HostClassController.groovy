package de.mobile.siteops

import grails.plugins.springsecurity.Secured
import grails.converters.JSON

class HostClassController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	/*
	* ajax Functions
	*/
   
   def ajaxList = {
	   def data = []
       def result = [[:]]
	   def hostClass = HostClass.findAll()

	   if (hostClass) {
		   data = hostClass.collect { [id: it.id, name: it.name] }
	   }
	   
	   data.each{ entry->
		   entry['actions'] = []
		   entry['actions'] += [title: 'Edit', type: 'edit', action: g.createLink(action: 'edit', controller: 'deploymentAdmin', id: entry.id)]
		   entry['actions'] += [title: 'Delete', type: 'remove', action: g.createLink(action: 'remove', controller: 'deploymentAdmin', id: entry.id)]
	   }

       result = [data: data]
       result['actions'] = [[title: 'Create', type: 'create', action: g.createLink(action: 'ajaxEdit', id: 0)]]

	   render result as JSON
   }
   
   /*
	* ajax Functions End
	*/
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

        def hostclassInstance = new HostClass(params)

        def selectedAppIds = params.getList('applications').collect { it as Long }
        if (selectedAppIds) {
            def selectedApps = Application.findAllByIdInList(selectedAppIds)
            if (selectedApps && !selectedApps.isEmpty()) {
                selectedApps.each {
                    if (hostclassInstance.applications == null || !hostclassInstance.applications.contains(it)) {
                        hostclassInstance.addToApplications(it)
                    }
                }
            }
        }

        if (hostclassInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'hostClass.label', default: 'HostClass'), hostclassInstance.id])}"
            redirect(action: "show", id: hostclassInstance.id)
        }
        else {
            render(view: "create", model: [hostClassInstance: hostclassInstance])
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
        def hostclassInstance = HostClass.get(params.id)
        if (hostclassInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (hostclassInstance.version > version) {
                    
                    hostclassInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'hostClass.label', default: 'HostClass')] as Object[], "Another user has updated this HostClass while you were editing")
                    render(view: "edit", model: [hostClassInstance: hostclassInstance])
                    return
                }
            }

            def removeApplications = []
            hostclassInstance.applications.each { removeApplications += it }
            removeApplications.each {
                hostclassInstance.removeFromApplications(it)
            }

            hostclassInstance.properties = params
            def selectedAppIds = params.getList('applications').collect { it as Long }
            if (selectedAppIds) {
                def selectedApps = Application.findAllByIdInList(selectedAppIds)
                if (selectedApps && !selectedApps.isEmpty()) {
                    selectedApps.each {
                        if (hostclassInstance.applications == null || !hostclassInstance.applications.contains(it)) {
                            hostclassInstance.addToApplications(it)
                        }
                    }
                }
            }

            if (!hostclassInstance.hasErrors() && hostclassInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'hostClass.label', default: 'HostClass'), hostclassInstance.id])}"
                redirect(action: "show", id: hostclassInstance.id)
            }
            else {
                render(view: "edit", model: [hostClassInstance: hostclassInstance])
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
