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
        def hosts = Host.findAll()

        if (hosts) {
            data = hosts.collect { [id: it.id, name: it.name] }
        }

        data.each { entry ->
            entry['actions'] = []
            entry['actions'] += [title: 'Edit', type: 'edit', action: g.createLink(action: 'ajaxEdit', controller: 'host', id: entry.id)]
            entry['actions'] += [title: 'Delete', type: 'remove', action: g.createLink(action: 'ajaxRemove', controller: 'host', id: entry.id)]
        }
        render data as JSON
    }

    //@Secured(['ROLE_ADMIN','IS_AUTHENTICATED_REMEMBERED'])
    def ajaxEdit = {
        def data = [[:]]

        def hostId = params.id as long
        def host = Host.get(hostId)

        if (host) {

            def hostClassList = HostClass.findAll().collect { [id: it.id, name: it.name] }
            def environmentList = Environment.findAll().collect { [id: it.id, name: it]}

            data = [saveUrl: g.createLink(action: 'ajaxSave', controller: 'host', id: hostId), hostClass: hostClassList, environment: environmentList]
            data['values'] = [
                    dateCreated: [value: host.dateCreated, type: 'text', disabled: true],
                    lastUpdated: [value: host.lastUpdated, type: 'text', disabled: true],
                    environment: [value: host.environment.id, type: 'select'],
                    //	hostClass:[value:host.className.id,type:'select']
                    name: [value: host.name, type: 'text']
            ]
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
