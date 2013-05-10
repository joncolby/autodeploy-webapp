package de.mobile.siteops

import grails.plugins.springsecurity.Secured
import grails.converters.JSON

class PillarController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	
	/*
	 * ajax Functions
	 */
	
	def ajaxList = {
		def data = []
		def pillars = Pillar.findAll()
		
		if (pillars) {
			data = pillars.collect { [id: it.id, name: it.name] }
		}
		
		data.each{ entry->
			entry['actions'] = []
			entry['actions'] += [title: 'Edit', type: 'edit', action: g.createLink(action: 'edit', controller: 'deploymentAdmin', id: entry.id)]
			entry['actions'] += [title: 'Delete', type: 'remove', action: g.createLink(action: 'remove', controller: 'deploymentAdmin', id: entry.id)]
	  }
		render data as JSON
	}
	
	/*
	 * ajax Functions End
	 */
    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 200, 200)
        [pillarInstanceList: Pillar.list(params), pillarInstanceTotal: Pillar.count()]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def create = {
        def pillarInstance = new Pillar()
        pillarInstance.properties = params
        return [pillarInstance: pillarInstance]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def save = {

        def pillarInstance = new Pillar(params)

        if (pillarInstance.save(flush: true)) {

            flash.message = "${message(code: 'default.created.message', args: [message(code: 'pillar.label', default: 'Pillar'), pillarInstance.id])}"
            redirect(action: "show", id: pillarInstance.id)
        }
        else {
            render(view: "create", model: [pillarInstance: pillarInstance])
        }
    }

    def show = {
        def pillarInstance = Pillar.get(params.id)
        if (!pillarInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'pillar.label', default: 'Pillar'), params.id])}"
            redirect(action: "list")
        }
        else {
            [pillarInstance: pillarInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def edit = {
        def pillarInstance = Pillar.get(params.id)
        if (!pillarInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'pillar.label', default: 'Pillar'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [pillarInstance: pillarInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def update = {
        def pillarInstance = Pillar.get(params.id)
        if (pillarInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (pillarInstance.version > version) {
                    
                    pillarInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'pillar.label', default: 'Pillar')] as Object[], "Another user has updated this Pillar while you were editing")
                    render(view: "edit", model: [pillarInstance: pillarInstance])
                    return
                }
            }


            pillarInstance.properties = params

            if (!pillarInstance.hasErrors() && pillarInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'pillar.label', default: 'Pillar'), pillarInstance.id])}"
                redirect(action: "show", id: pillarInstance.id)
            }
            else {
                render(view: "edit", model: [pillarInstance: pillarInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'pillar.label', default: 'Pillar'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def delete = {
        def pillarInstance = Pillar.get(params.id)
        if (pillarInstance) {

              if (pillarInstance.applications) {
                flash.warning = "${message(code: 'pillar.has.applications', args: [ pillarInstance?.name])}"
                redirect(action: "show", id: pillarInstance.id)
                return
              }


            try {
                pillarInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'pillar.label', default: 'Pillar'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'pillar.label', default: 'Pillar'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'pillar.label', default: 'Pillar'), params.id])}"
            redirect(action: "list")
        }
    }


}
