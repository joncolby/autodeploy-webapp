package de.mobile.siteops

import grails.plugins.springsecurity.Secured
import grails.converters.JSON

class HostController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    /*
      * ajax Functions
      */

    def ajaxList = {
        def data = []
		def result = [[:]]
        def hosts = Host.findAll()
        if (hosts) {
            data = hosts.collect { [id: it.id, name: it.name,className:(it.className)?it.className.name:"",environment:(it.environment)?it.environment.name:""] }
        }

        data.each { TableUtils.addActions(it,g) }
		
		result= [data: data]
		result['actions'] = [[title: 'Create', type: 'create', action: g.createLink(action: 'ajaxEdit', id: 0)]]
		
        render result as JSON
    }

    //@Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def ajaxEdit = {
        def data = [[:]]

        def hostId = params.id as long
        def host = Host.get(hostId)
		
		if (!host) { // for new entries
			host = [dateCreated:"",lastUpdated:"",environment:null,className:null,name:"",id:0]
		}
		
        def hostClassList = HostClass.findAll().collect { [id: it.id, name: it.name,selected: (host.className && host.className.id == it.id)] }
        def environmentList = Environment.findAll().collect { [id: it.id, name: it.name,selected:(host.environment && host.environment.id == it.id)]}
		
		if (!host.className) hostClassList += [id: 0, name: '---',selected:true]
		if (!host.environment) environmentList += [id: 0, name: '---',selected:true]

        data = [saveUrl: g.createLink(action: 'ajaxSave', id: hostId)]
        data['values'] = [
                dateCreated: [value: host.dateCreated, type: 'text', disabled: true],
                lastUpdated: [value: host.lastUpdated, type: 'text', disabled: true],
                environment: [value: environmentList, type: 'select'],
                hostClass:[value:hostClassList,type:'select'],
                name: [value: host.name, type: 'text']
        ]
        
        render data as JSON
    }
	
	//@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
	def ajaxSave = {
		def result = [:]
		def hostInstance = Host.get(params.id)
		def values = [name:params.name,'className.id':params.hostClass,'environment.id':params.environment]
		if (hostInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (hostInstance.version > version) {

					hostInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'host.label', default: 'Host')] as Object[], "Another user has updated this Host while you were editing")
					result = [MessageResult.errorMessage("Version too long")]
					render result as JSON
					return
				}
			}

			hostInstance.properties = values
			if (!hostInstance.hasErrors() && hostInstance.save(flush: true)) {
				result = [MessageResult.successMessage("Entry successfully updated")]
			}
			else {
	            result = [message:MessageResult.addFieldErrors(hostInstance.id, hostInstance)]
			}
		}
		else {
			hostInstance = new Host(values)
			if (hostInstance.save(flush: true)) {
				def entry = [id: hostInstance.id, name: hostInstance.name,className:(hostInstance.className)?hostInstance.className.name:"", environment: hostInstance.environment.name]
				result = [entry:  TableUtils.addActions(entry,g), 
					      message: MessageResult.successMessage("New Entry successfully saved")]
			}
			else {
				result = [message:MessageResult.addFieldErrors(hostInstance.id, hostInstance)]
			}	
		}
		
		render result as JSON
	}
	
	//@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
	def ajaxDelete = {
		def hostInstance = Host.get(params.id)
		if (hostInstance) {
			try {
				hostInstance.delete(flush: true)
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
        params.max = Math.min(params.max ? params.int('max') : 25, 25)
        params.sort = 'name'
        [hostInstanceList: Host.list(params), hostInstanceTotal: Host.count()]
    }

    def listByEnv = {

        def environmentInstance = Environment.get(params?.id)

        params.max = Math.min(params.max ? params.int('max') : 25, 25)
        params.sort = 'name'
        render(view: "list", model: [hostInstanceList: Host.findAllByEnvironment(environmentInstance, params), hostInstanceTotal: Host.findAllByEnvironment(environmentInstance).size(), environmentInstance: environmentInstance])
    }

    def search = {

        params.max = Math.min(params.max ? params.int('max') : 25, 25)
        params.sort = 'name'

        //def hosts =  Host.findAllByName(params?.hostname, params)


        def hosts = Host.createCriteria().list(max: params?.max, sort: params?.sort, offset: params?.offset) {
            like("name", "${params?.hostname}%")
            projections {
                rowCount()
            }
        }

        def hostInstanceTotal = hosts.totalCount

        render(view: "list", model: [hostInstanceList: hosts, hostInstanceTotal: hostInstanceTotal, hostname: params?.hostname])
    }


    def searchByEnv = {

        def environmentInstance = Environment.get(params?.environment)

        params.max = Math.min(params.max ? params.int('max') : 25, 25)
        params.sort = 'name'

        //  def hosts =  Host.findAllByEnvironmentAndNameLike(environmentInstance, "${params?.host}%", params)
        //  def hostInstanceTotal = Host.findAllByEnvironmentAndNameLike(environmentInstance, "${params?.host}%").size()

        def hosts = Host.createCriteria().list(max: params?.max, sort: params?.sort, offset: params?.offset) {

            and {
                eq("environment", environmentInstance)
                like("name", "${params?.host}%")
            }
            projections {
                rowCount()
            }
        }

        def hostInstanceTotal = hosts.totalCount

        render(view: "deploy_host", model: [hostInstanceList: hosts, hostInstanceTotal: hostInstanceTotal, deploymentQueueEntryInstance: deploymentQueueEntryInstance, deploymentQueueEntryId: params?.deploymentQueueEntryId, host: params?.host])
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def create = {
        def hostInstance = new Host()
        hostInstance.properties = params
        return [hostInstance: hostInstance]
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def save = {
        def hostInstance = new Host(params)
        if (hostInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'host.label', default: 'Host'), hostInstance.id])}"
            redirect(action: "show", id: hostInstance.id)
        }
        else {
            render(view: "create", model: [hostInstance: hostInstance])
        }
    }

    def show = {
        def hostInstance = Host.get(params.id)
        if (!hostInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'host.label', default: 'Host'), params.id])}"
            redirect(action: "list")
        }
        else {
            [hostInstance: hostInstance]
        }
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def edit = {
        def hostInstance = Host.get(params.id)
        if (!hostInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'host.label', default: 'Host'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [hostInstance: hostInstance]
        }
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def update = {
        def hostInstance = Host.get(params.id)
        if (hostInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (hostInstance.version > version) {

                    hostInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'host.label', default: 'Host')] as Object[], "Another user has updated this Host while you were editing")
                    render(view: "edit", model: [hostInstance: hostInstance])
                    return
                }
            }

            /*
             // host has many applications but Application owns the relationship (belongsTo)
             def currentApplications = hostInstance.applications.collect { it }

            // remove all apps from this host
            Iterator c = currentApplications.iterator()

            while (c.hasNext())
                  c.next().removeFromHosts(hostInstance).save(flush:true)


            def chosenApps = getChosenApps()

            Iterator i = chosenApps.iterator()

            // add the chosen apps to the application side first
            while (i.hasNext())
                  i.next().addToHosts(hostInstance).save(flush:true)

            // no extra binding needed
            //params.applications = chosenApps
            */

            hostInstance.properties = params
            if (!hostInstance.hasErrors() && hostInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'host.label', default: 'Host'), hostInstance.id])}"
                redirect(action: "show", id: hostInstance.id)
            }
            else {
                render(view: "edit", model: [hostInstance: hostInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'host.label', default: 'Host'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def delete = {
        def hostInstance = Host.get(params.id)
        if (hostInstance) {
            try {
                hostInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'host.label', default: 'Host'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'host.label', default: 'Host'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'host.label', default: 'Host'), params.id])}"
            redirect(action: "list")
        }
    }

    def getChosenApps() {

        def applicationList = params.applications

        if (applicationList instanceof String) {
            applicationList = []
            applicationList << params.applications
        }

        def applicationChosen = applicationList.collect {Application.get(it)}

        return applicationChosen
    }
}
