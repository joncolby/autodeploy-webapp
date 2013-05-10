package de.mobile.siteops

import grails.plugins.springsecurity.Secured
import grails.converters.JSON

class RepositoryController {

	
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	/*
	* ajax Functions
	*/
   
   def ajaxList = {
	   def data = []
	   def repositories = Repository.findAll()
	   
	   if (repositories) {
		   data = repositories.collect { [id: it.id, name: it.name] }
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
        [repositoryInstanceList: Repository.list(params), repositoryInstanceTotal: Repository.count()]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def create = {
        def repositoryInstance = new Repository()
        repositoryInstance.properties = params
        return [repositoryInstance: repositoryInstance]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def save = {
        def repositoryInstance = new Repository(params)
        if (repositoryInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'repository.label', default: 'Repository'), repositoryInstance.id])}"
            redirect(action: "show", id: repositoryInstance.id)
        }
        else {
            render(view: "create", model: [repositoryInstance: repositoryInstance])
        }
    }

    def show = {
        def repositoryInstance = Repository.get(params.id)
        if (!repositoryInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'repository.label', default: 'Repository'), params.id])}"
            redirect(action: "list")
        }
        else {
            [repositoryInstance: repositoryInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def edit = {
        def repositoryInstance = Repository.get(params.id)
        if (!repositoryInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'repository.label', default: 'Repository'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [repositoryInstance: repositoryInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def update = {
        def repositoryInstance = Repository.get(params.id)
        if (repositoryInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (repositoryInstance.version > version) {
                    
                    repositoryInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'repository.label', default: 'Repository')] as Object[], "Another user has updated this Repository while you were editing")
                    render(view: "edit", model: [repositoryInstance: repositoryInstance])
                    return
                }
            }
            repositoryInstance.properties = params
            if (!repositoryInstance.hasErrors() && repositoryInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'repository.label', default: 'Repository'), repositoryInstance.id])}"
                redirect(action: "show", id: repositoryInstance.id)
            }
            else {
                render(view: "edit", model: [repositoryInstance: repositoryInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'repository.label', default: 'Repository'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def delete = {
        def repositoryInstance = Repository.get(params.id)
        if (repositoryInstance) {
            try {
                repositoryInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'repository.label', default: 'Repository'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'repository.label', default: 'Repository'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'repository.label', default: 'Repository'), params.id])}"
            redirect(action: "list")
        }
    }
}
