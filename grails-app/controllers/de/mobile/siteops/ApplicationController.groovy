package de.mobile.siteops

import grails.plugins.springsecurity.Secured
import grails.converters.JSON
import de.mobile.siteops.Application.MarketPlace

class ApplicationController {

    def dataSource

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	/*
	* ajax Functions
	*/
	
   def ajaxList = {
	   def data = []
	   def application = Application.findAll()

	   if (application) {
		   data = application.collect { [id: it.id, filename: it.filename, modulename: it.modulename, pillar:it.pillar.name,description:it.description] }
	   }
	   data.each { TableUtils.addActions(it,g) }

       def config = [description: [width: '300px', searchable: true], id: [width: '30px', searchable: false]]
	   def result = [data: data, config: config, actions: [[title: 'Create', type: 'create', action: g.createLink(action: 'ajaxEdit', id: 0)]]]

	   render result as JSON
   }
   
   def ajaxEdit = {
	   def data = [[:]]

	   def applicationId = params.id as long
	   def application = Application.get(applicationId)
	   
	   if (!application) { // for new entries
		   application = [
			   pillar:null,filename:"",downloadName:"",startStopScript:"",propertiesPath:"",installDir:"",symlink:"",description:"",context:"",verificationJMXBean:"",verificationJMXAttribute:"",type:null,balancerType:null, modulename:"",artifactId:"",groupId:"",assembleProperties:false,instanceProperties:false,doProbe:false,startOnDeploy:false]
	   }
	   
	   def pillarList = Pillar.findAll().collect { [id: it.id, name: it.name,selected: (application.pillar && application.pillar.id == it.id)] }
	   def typeList = Application.ApplicationType.collect { [id: it.name(), name: it.name(),selected: (application.type && application.type.name() == it.name())] }
	   def balancerTypeList = Application.LoadBalancerType.collect { [id:it.name(), name: it.name(),selected: (application.balancerType && application.balancerType.name() == it.name())] }
       def marketPlaceList = MarketPlace.values().collect { [id: it.name(), name: it.name(), selected: (application.marketPlace && application.marketPlace == it) ]}

	   def hostClasses = application.hostclasses
	   def hostClassList = HostClass.findAll().collect{[id: it.id, name: it.name, selected: (hostClasses)?hostClasses.contains(it):false]}
   
	   if (!application.pillar) pillarList += [id: 0, name: '---',selected:true]
	   if (!application.type) typeList += [id: 0, name: '---',selected:true]
	   if (!application.balancerType) balancerTypeList += [id: 0, name: '---',selected:true]

	   data = [saveUrl: g.createLink(action: 'ajaxSave', id: applicationId)]
	   data['values'] = [
		   pillar:[value:pillarList, type:'select'],
		   filename:[value: application.filename, type: 'text', size: 30],
		   downloadName:[value: application.downloadName, type: 'text', size: 30],
		   startStopScript:[value: application.startStopScript, type: 'text'],
		   installDir:[value: application.installDir, type: 'text'],
           symlink:[value: application.symlink, type: 'text'],
		   propertiesPath:[value: application.propertiesPath, type: 'text'],
		   description:[value: application.description, type: 'textarea'],
		   context:[value: application.context, type: 'text'],
		   verificationJMXBean:[value: application.verificationJMXBean, type: 'text'],
		   verificationJMXAttribute:[value: application.verificationJMXAttribute, type: 'text'],
		   type:[value:typeList, type:'select'],
           marketPlace:[value: marketPlaceList, type:'select'],
		   balancerType:[value:balancerTypeList, type:'select'],
		   modulename:[value:application.modulename, type: 'text'],
		   artifactId:[value:application.artifactId, type: 'text'],
		   groupId:[value:application.groupId, type: 'text'],
		   assembleProperties:[value:application.assembleProperties, type: 'checkbox'],
		   instanceProperties:[value:application.instanceProperties, type: 'checkbox'],
		   doProbe:[value:application.doProbe, type: 'checkbox'],
		   startOnDeploy:[value:application.startOnDeploy, type: 'checkbox'],
		   hostClasses:[value:hostClassList,type:'list']
	   ]
	   
	   render data as JSON
   }
   
   //@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
   def ajaxSave = {
	   def result = [:]
	   def application = Application.get(params.id)
	   def values = ['pillar.id':params.pillar,
		   filename:params.filename,
           downloadName:params.downloadName,
		   startStopScript:params.startStopScript,
		   installDir:params.installDir,
		   symlink:params.symlink,
		   propertiesPath:params.propertiesPath,
		   description:params.description,
		   context:params.context,
		   verificationJMXBean:params.verificationJMXBean,
           verificationJMXAttribute:params.verificationJMXAttribute,
		   type:params.type,
           marketPlace: params.marketPlace,
		   balancerType:params.balancerType,
		   modulename:params.modulename,
		   artifactId:params.artifactId,
		   groupId:params.groupId,
		   assembleProperties:(params.assembleProperties)?true:false,
		   instanceProperties:(params.instanceProperties)?true:false,
		   doProbe:(params.doProbe)?true:false,
		   startOnDeploy:(params.startOnDeploy)?true:false
		]
	      
	   if (application) {
		   if (params.version) {
			   def version = params.version.toLong()
			   if (application.version > version) {

				   application.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'host.label', default: 'Host')] as Object[], "Another user has updated this Host while you were editing")
				   result = [MessageResult.errorMessage("Version too long")]
				   render result as JSON
				   return
			   }
		   }

		   application.properties = values
		   if (!application.hasErrors() && application.save(flush: true)) {
			   result = [MessageResult.successMessage("Entry successfully updated")]
		   }
		   else {
			   result = [message:MessageResult.addFieldErrors(application.id, application)]
		   }
	   }
	   else {
		   application = new Application(values)
		   if (application.save(flush: true)) {
			   def entry = [
				   	   id:application.id,
					   filename:application.filename,
					   pillar:application.pillar.name,
					   description:application.description
					]
			   result = [entry:  TableUtils.addActions(entry,g),
						 message: MessageResult.successMessage("New Entry successfully saved")]
		   }
		   else {
			   result = [message:MessageResult.addFieldErrors(application.id, application)]
		   }
	   }
	   
	   def hostClasses = HostClass.findAllByIdInList(getListFromSeparated(params.hostClasses))
	   if (hostClasses && !hostClasses.isEmpty()) {
		   handleHostClasses(hostClasses, application)
	   }
	   
	   render result as JSON
   }
   

   //@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
   def ajaxDelete = {
	   def instance = Application.get(params.id)
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
   
   
   private def handleHostClasses(hostClasses, Application app) {
	   hostClasses.each {
		   if (app.hostclasses == null || !app.hostclasses.contains(it)) {
			   app.addToHostclasses(it)
		   }
	   }
	   def removeApps = []
	   app.hostclasses.each { 
		   if (!hostClasses.contains(it)) removeHostClasses += it
	   }
	   removeApps.each {
		   app.removeFromHostclasses(it)
	   }
   }
   
   private def getListFromSeparated(separated,separator=",") {
	   def result = []
	   if (separated) {
		   if (separated.contains(separator)) {
			   separated.split(separator).each {
				   result += it as long
			   }
		   } else {
			   result += separated as long
		   }
	   }
	   return result
   }
   
   /*
	* ajax Functions End
	*/
    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 200, 200)
        [applicationInstanceList: Application.list(params), applicationInstanceTotal: Application.count()]
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def create = {
        def applicationInstance = new Application()
        applicationInstance.properties = params
        return [applicationInstance: applicationInstance]
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
    def save = {
        def applicationInstance = new Application(params)

        def selectedHostclassIds = params.getList('hostclasses').collect { it as Long }
        if (selectedHostclassIds) {
            def selectedHostclasses = HostClass.findAllByIdInList(selectedHostclassIds)
            if (selectedHostclasses && !selectedHostclasses.isEmpty()) {
                selectedHostclasses.each {
                    if (applicationInstance.hostclasses == null || !applicationInstance.hostclasses.contains(it)) {
                        applicationInstance.addToHostclasses(it)
                    }
                }
            }
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

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
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

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
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

            def removeHostsclasses = []
            applicationInstance.hostclasses.each { removeHostsclasses += it }
            removeHostsclasses.each {
                applicationInstance.removeFromHostclasses(it)
            }

            applicationInstance.properties = params
            def selectedHostclassIds = params.getList('hostclasses').collect { it as Long }
            if (selectedHostclassIds) {
                def selectedHostclasses = HostClass.findAllByIdInList(selectedHostclassIds)
                if (selectedHostclasses && !selectedHostclasses.isEmpty()) {
                    selectedHostclasses.each {
                        if (applicationInstance.hostclasses == null || !applicationInstance.hostclasses.contains(it)) {
                            applicationInstance.addToHostclasses(it)
                        }
                    }
                }
            }

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

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
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
