package de.mobile.siteops

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class PropertyAssemblerController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    /*
      * ajax Functions
      */

    def ajaxList = {
        def data = []
		def result = [[:]]
        def instance = PropertyAssembler.findAll()
        if (instance) {
            data = instance.collect { [id: it.id, name: it.name, configAssemblerUrl: it.configAssemblerUrl] }
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
        def instance = PropertyAssembler.get(id)
		
		if (!instance) { // for new entries
			instance = [dateCreated:"",lastUpdated:"",name:"", configAssemblerUrl:"",id:0]
		}
		
        data = [saveUrl: g.createLink(action: 'ajaxSave', id: id)]
        data['values'] = [
                dateCreated: [value: instance.dateCreated, type: 'text', disabled: true],
                lastUpdated: [value: instance.lastUpdated, type: 'text', disabled: true],
                name: [value: instance.name, type: 'text', size: 30],
                configAssemblerUrl: [value: instance.configAssemblerUrl, type: 'text', size: 50]
        ]
        
        render data as JSON
    }
	
	//@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
	def ajaxSave = {
		def result = [:]
		def instance = PropertyAssembler.get(params.id)
		def values = [name:params.name, configAssemblerUrl:params.configAssemblerUrl]
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
			instance = new PropertyAssembler(values)
			if (instance.save(flush: true)) {
				def tableEntry = [id: instance.id, name: instance.name, configAssemblerUrl: instance.configAssemblerUrl]
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
		def instance = PropertyAssembler.get(params.id)
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

}
