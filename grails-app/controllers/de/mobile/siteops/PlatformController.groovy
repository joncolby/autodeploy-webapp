package de.mobile.siteops

import grails.plugins.springsecurity.Secured
import grails.converters.JSON

class PlatformController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	/*
	 * ajax Functions
	 */

	def ajaxList = {
		def data = []
		def platform = Platform.findAll()

		if (platform) {
			data = platform.collect { [id: it.id, name: it.name] }
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
        [platformInstanceList: Platform.list(params), platformInstanceTotal: Platform.count()]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def create = {
        def platformInstance = new Platform()
        platformInstance.properties = params
        return [platformInstance: platformInstance]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def save = {
        def platformInstance = new Platform(params)
        if (platformInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'platform.label', default: 'Platform'), platformInstance.id])}"
            redirect(action: "show", id: platformInstance.id)
        }
        else {
            render(view: "create", model: [platformInstance: platformInstance])
        }
    }

    def show = {
        def platformInstance = Platform.get(params.id)
        if (!platformInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])}"
            redirect(action: "list")
        }
        else {
            [platformInstance: platformInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def edit = {
        def platformInstance = Platform.get(params.id)
        if (!platformInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [platformInstance: platformInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def update = {
        def platformInstance = Platform.get(params.id)
        if (platformInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (platformInstance.version > version) {
                    
                    platformInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'platform.label', default: 'Platform')] as Object[], "Another user has updated this Platform while you were editing")
                    render(view: "edit", model: [platformInstance: platformInstance])
                    return
                }
            }
            platformInstance.properties = params
            if (!platformInstance.hasErrors() && platformInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'platform.label', default: 'Platform'), platformInstance.id])}"
                redirect(action: "show", id: platformInstance.id)
            }
            else {
                render(view: "edit", model: [platformInstance: platformInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def delete = {
        def platformInstance = Platform.get(params.id)
        if (platformInstance) {
            try {
                platformInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])}"
            redirect(action: "list")
        }
    }
}
