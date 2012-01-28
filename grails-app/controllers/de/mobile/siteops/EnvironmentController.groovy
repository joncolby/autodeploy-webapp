package de.mobile.siteops

import grails.plugins.springsecurity.Secured
import grails.converters.JSON
import de.mobile.siteops.Environment.DeployErrorType

class EnvironmentController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    /*
      * ajax Functions
      */

    def ajaxList = {
        def data = []
		def result = [[:]]
        def instance = Environment.findAll()
        if (instance) {
            data = instance.collect { [id: it.id, name: it.name, repository: (it.repository ? it.repository.name : ""), propertyAssembler: (it.propertyAssembler ? it.propertyAssembler.name : ""), secured: (it.secured ? 'Yes' : 'No')] }
        }

        data.each { TableUtils.addActions(it,g) }

		result= [data: data]
		result['actions'] = [[title: 'Create', type: 'create', action: g.createLink(action: 'ajaxEdit', id: 0)]]

        render result as JSON
    }

    //@Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def ajaxEdit = {
        def data = [[:]]

        def id = params.id as long
        Environment instance = Environment.get(id)

		if (!instance) { // for new entries
			instance = [dateCreated:"",lastUpdated:"",name:"", useHostClassConcurrency: false, secured: false, releaseMailByDefault: false, deployErrorType: null, repository: null, propertyAssembler: null,id:0]
		}

        def deployErrorTypes = DeployErrorType.values().collect { [id: it.name(), name: it.name(), selected: (instance.deployErrorType && instance.deployErrorType == it) ]}
        def repositories = Repository.findAll().collect { [id: it.id, name: it.name,selected:(instance.repository && instance.repository.id == it.id)]}
        def propertyAssemblers = PropertyAssembler.findAll().collect { [id: it.id, name: it.name,selected:(instance.propertyAssembler && instance.propertyAssembler.id == it.id)]}

        if (!instance.repository) repositories += [id: 0, name: '---',selected:true]
        if (!instance.propertyAssembler) propertyAssemblers += [id: 0, name: '---',selected:true]

        data = [saveUrl: g.createLink(action: 'ajaxSave', id: id)]
        data['values'] = [
                dateCreated: [value: instance.dateCreated, type: 'text', disabled: true],
                lastUpdated: [value: instance.lastUpdated, type: 'text', disabled: true],
                name: [value: instance.name, type: 'text', size: 30],
                useHostClassConcurrency: [value: instance.useHostClassConcurrency, type: 'checkbox'],
                secured: [value: instance.secured, type: 'checkbox'],
                releaseMailByDefault: [value: instance.releaseMailByDefault, type: 'checkbox'],
                deployErrorType: [value: deployErrorTypes, type: 'select'],
                repository: [value: repositories, type: 'select'],
                propertyAssembler: [value: propertyAssemblers, type: 'select'],
        ]

        render data as JSON
    }

	//@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
	def ajaxSave = {
		def result = [:]
		Environment instance = Environment.get(params.id)
		def values = [
                name:params.name,
                useHostClassConcurrency: params.useHostClassConcurrency ? true : false,
                secured: params.secured ? true : false,
                releaseMailByDefault: params.releaseMailByDefault ? true : false,
                deployErrorType: params.deployErrorType,
                'repository.id': params.repository,
                'propertyAssembler.id': params.propertyAssembler
        ]
		if (instance) {
			if (params.version) {
				def version = params.version.toLong()
				if (instance.version > version) {

					instance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'host.label', default: 'Host')] as Object[], "Another user has updated this Host while you were editing")
					result = [MessageResult.errorMessage("Version too long")]
					render result as JSON
					return
				}
			}
			instance.properties = values
			if (!instance.hasErrors() && instance.save(flush: true)) {
				result = [MessageResult.successMessage("Entry successfully updated")]
			}
			else {
	            result = [message:MessageResult.addFieldErrors(instance.id, instance)]
			}
		}
		else {
            println values
			instance = new Environment(values)
			if (instance.save(flush: true)) {
                if (!DeploymentQueue.findByEnvironment(instance)) {
                  new DeploymentQueue(environment: instance).save(flush:true)
                }

				def tableEntry = [id: instance.id, name: instance.name, useHostClassConcurrency: instance.useHostClassConcurrency, secured: instance.secured, releaseMailByDefault: instance.releaseMailByDefault, deployErrorType: instance.deployErrorType, repository: instance.repository, propertyAssembler: instance.propertyAssembler]
				result = [tableEntry:  TableUtils.addActions(tableEntry,g),
					      message: MessageResult.successMessage("New Entry successfully saved")]
			}
			else {
				result = [message:MessageResult.addFieldErrors(instance.id, instance)]
			}
		}

		render result as JSON
	}

	//@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
	def ajaxDelete = {
		Environment instance = Environment.get(params.id)
		if (instance) {
			try {
				instance.delete(flush: true)
				render MessageResult.successMessage("Entry successfully deleted") as JSON
			}
			catch (org.springframework.dao.DataIntegrityViolationException e) {
				render MessageResult.errorMessage("Entry could not be deleted") as JSON
			}
		}
		else {
			render MessageResult.successMessage("No such entry") as JSON
		}
	}

	/*
	 * ajax Functions End
	 */
    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [environmentInstanceList: Environment.list(params), environmentInstanceTotal: Environment.count()]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def create = {
        def environmentInstance = new Environment()
        environmentInstance.properties = params
        return [environmentInstance: environmentInstance]
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def save = {
        def environmentInstance = new Environment(params)
        if (environmentInstance.save(flush: true)) {

            DeploymentQueue.findByEnvironment(environmentInstance) ?: new DeploymentQueue(environment: environmentInstance).save(flush:true)

            flash.message = "${message(code: 'default.created.message', args: [message(code: 'environment.label', default: 'Environment'), environmentInstance.id])}"
            redirect(action: "show", id: environmentInstance.id)
        }
        else {
            render(view: "create", model: [environmentInstance: environmentInstance])
        }
    }

    def show = {
        def environmentInstance = Environment.get(params.id)
        if (!environmentInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'environment.label', default: 'Environment'), params.id])}"
            redirect(action: "list")
        }
        else {
            [environmentInstance: environmentInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def edit = {
        def environmentInstance = Environment.get(params.id)
        if (!environmentInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'environment.label', default: 'Environment'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [environmentInstance: environmentInstance]
        }
    }

    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def update = {
        def environmentInstance = Environment.get(params.id)
        if (environmentInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (environmentInstance.version > version) {
                    
                    environmentInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'environment.label', default: 'Environment')] as Object[], "Another user has updated this Environment while you were editing")
                    render(view: "edit", model: [environmentInstance: environmentInstance])
                    return
                }
            }
            environmentInstance.properties = params
            if (!environmentInstance.hasErrors() && environmentInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'environment.label', default: 'Environment'), environmentInstance.id])}"
                redirect(action: "show", id: environmentInstance.id)
            }
            else {
                render(view: "edit", model: [environmentInstance: environmentInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'environment.label', default: 'Environment'), params.id])}"
            redirect(action: "list")
        }
    }
    @Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def delete = {
        def environmentInstance = Environment.get(params.id)
        if (environmentInstance) {
            try {
                environmentInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'environment.label', default: 'Environment'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'environment.label', default: 'Environment'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'environment.label', default: 'Environment'), params.id])}"
            redirect(action: "list")
        }
    }
}
