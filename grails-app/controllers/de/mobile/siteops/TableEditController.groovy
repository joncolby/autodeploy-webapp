package de.mobile.siteops
import grails.converters.JSON

class TableEditController {

    def index = { }
	
	def ajaxList = {
		def data = []
		def result = [[:]]
		def entries = properties.baseClass.findAll()
		if (entries) {
			data = entries.collect { [id: it.id, name: it.name,className:(it.className)?it.className.name:""] }
		}

		data.each { TableUtils.addActions(it) }
		
		result= [data: data]
		result['actions'] = [[title: 'Create', type: 'create', action: g.createLink(action: 'ajaxEdit', id: 0)]]
		
		render result as JSON
	}

	//@Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
	def ajaxEdit = {
		def data = [[:]]

		def entryId = params.id as long
		def entry = properties.baseClass.get(entryId)
		
		if (!entry) { // for new entries
			entry = [dateCreated:"",lastUpdated:"",environment:null,className:null,name:"",id:0]
		}
		
		def entryClassList = hostClass.findAll().collect { [id: it.id, name: it.name,selected: (entry.className && entry.className.id == it.id)] }
		def environmentList = Environment.findAll().collect { [id: it.id, name: it.name,selected:(entry.environment && entry.environment.id == it.id)]}
		
		if (!entry.className) entryClassList += [id: 0, name: '---',selected:true]
		if (!entry.environment) environmentList += [id: 0, name: '---',selected:true]

		data = [saveUrl: g.createLink(action: 'ajaxSave', controller: 'entry', id: entryId)]
		data['values'] = [
				dateCreated: [value: entry.dateCreated, type: 'text', disabled: true],
				lastUpdated: [value: entry.lastUpdated, type: 'text', disabled: true],
				environment: [value: environmentList, type: 'select'],
				entryClass:[value:entryClassList,type:'select'],
				name: [value: entry.name, type: 'text']
		]
		
		render data as JSON
	}
	
	//@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
	def ajaxSave = {
		def result = [:]
		def entryInstance = entry.get(params.id)
		def values = [name:params.name,'entryClass.id':params.entryClass,'environment.id':params.environment]
		
		if (entryInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (entryInstance.version > version) {

					entryInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'entry.label', default: 'entry')] as Object[], "Another user has updated this entry while you were editing")
					result = [MessageResult.errorMessage("Version too long")]
					render result as JSON
					return
				}
			}

			entryInstance.properties = values
			if (!entryInstance.hasErrors() && entryInstance.save(flush: true)) {
				result = [MessageResult.successMessage("entry successfully updated")]
			}
			else {
				result = [message:MessageResult.addFieldErrors(entryInstance.id, entryInstance)]
			}
		}
		else {
			entryInstance = properties.newInstance(values)
			if (entryInstance.save(flush: true)) {
				def entry = [id: entryInstance.id, name: entryInstance.name,entryInstance:(entryInstance.className)?entryInstance.className.name:""]
				result = [entry:  TableUtils.addActions(entry),
						  message: MessageResult.successMessage("New entry successfully saved")]
			}
			else {
				result = [message:MessageResult.addFieldErrors(entryInstance.id, entryInstance)]
			}
		}
		
		render result as JSON
	}
	
	//@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
	def ajaxDelete = {
		def entryInstance = entry.get(params.id)
		if (entryInstance) {
			try {
				entryInstance.delete(flush: true)
				render MessageResult.successMessage("entry successfully deleted") as JSON
			}
			catch (org.springframework.dao.DataIntegrityViolationException e) {
				render MessageResult.errorMessage("entry could not be deleted") as JSON
			}
		}
		else {
			render MessageResult.successMessage("No such entry") as JSON
		}
	}
	
	def addActions(def data) {
		data['actions'] = []
			data['actions'] += [title: 'Edit', type: 'edit', action: g.createLink(action: 'ajaxEdit', id: data.id)]
			data['actions'] += [title: 'Delete', type: 'remove', action: g.createLink(action: 'ajaxDelete', id: data.id)]

		return data
	}
	
}
