package autodeploy

import de.mobile.siteops.*

class CustomTagLib {

    def autoPlayService

    def autoPlayWarning = { attrs, body ->
      if (! attrs?.queueId) return
      def deploymentQueue = DeploymentQueue.get(attrs.queueId)
      if (!deploymentQueue) return
      if (autoPlayService.isEnabled(deploymentQueue))
      out << '<div style="color: #cc0000; font-weight:bold; text-shadow: 1px 1px #fff;font-size:14px" align="center">AUTOPLAY IS ON</div>'
    }

    // Checkbox list that can be used as a more user-friendly alternative to
    // a multi-select list box
  
    def checkBoxList = {attrs, body ->

    def from = attrs.from
    def form = attrs.form
    def value = attrs.value
    def cname = attrs.name
    def isChecked, ht, wd, style, html
    def applications
    // sets the style to override height and/or width if either of them is specified,
    // else the default from the CSS is taken

    style = "style='"

    if(attrs.height)
      style += "height:${attrs.height};"

    if(attrs.width)
      style += "width:${attrs.width};"

    if (style.length() == "style='".length())
      style = ""
    else
      style += "'" // closing single quote

      html = "<input type=\"button\" onclick=\"SetAllCheckBoxes('" + form + "', '" + cname + "', true);\" value=\"Select All\">"
      html += "&nbsp;&nbsp;&nbsp;<input type=\"button\" onclick=\"SetAllCheckBoxes('" + form + "', '" + cname + "', false);\" value=\"Select None\">"

    //html = "<ul class='CheckBoxList' " + style + ">"
    html += "<ul class='CheckBoxList' " + style + ">"
 
    out << html

    from.each { obj ->

          // if we wanted to select the checkbox using a click anywhere on the label (also hover effect)
          // but grails does not recognize index suffix in the name as an array: // cname = "${attrs.name}[${idx++}]"
          // and put this inside the li: <label for='$cname'>...</label>

                  isChecked = (value?.contains(obj."${attrs.optionKey}"))? true: false

                  out << "<li><label for=\"" + cname + obj."${attrs.optionKey}" + "\">" << checkBox(name:cname, value:obj."${attrs.optionKey}", checked: isChecked, id: cname + obj."${attrs.optionKey}") << "${obj} " << "</label></li>"


    }


    out << "</ul>"

}

  /*
  def toggleAddToQueue = {  attrs, body ->

    def deploymentPlanInstance =  attrs['plan']
    def deploymentQueue = attrs['queue']

    if ( deploymentQueue.find { it.plans.contains(deploymentPlanInstance) }  )
       //out << "<div class='message'>This plan is in the $deploymentQueue</div>"
       out << "<div class='message'>" + link(action: 'show', controller: 'deploymentQueue', id: deploymentQueue.id) { "This plan is in the <span style='text-decoration : underline;'>$deploymentQueue</span>" } + "</div>"
    else
      out << "<div class='menuButton'>" + link(class: 'create', action: 'addToDeploymentQueue',params: [environment: deploymentQueue?.environment?.id,id: deploymentPlanInstance?.id ]) { "Add to ${deploymentQueue}" }  + "</div>"
  }
  */

    def toggleAddToQueue = {  attrs, body ->

      def deploymentPlanInstance =  attrs['plan']
      def deploymentQueue = attrs['queue']

      def entries = deploymentQueue.collect { it.entries }
      
      if ( entries.find { it.plan.contains(deploymentPlanInstance) }  ) {
        out << "<div class='message'>" + link(action: 'show', controller: 'deploymentQueue', id: deploymentQueue.id) { "This plan is in the <span style='text-decoration : underline;'>$deploymentQueue</span>" } + "</div>"
        out << "<div class='menuButton'>" + link(class: 'create', action: 'create', controller: 'deploymentQueueEntry', params: [environment: deploymentQueue?.environment?.id,id: deploymentPlanInstance?.id ]) { "Add to ${deploymentQueue}" }  + "</div>"
      } else {
        //out << "<div class='menuButton'>" + link(class: 'create', action: 'addToDeploymentQueue',params: [environment: deploymentQueue?.environment?.id,id: deploymentPlanInstance?.id ]) { "Add to ${deploymentQueue}" }  + "</div>"
        out << "<div class='menuButton'>" + link(class: 'create', action: 'create', controller: 'deploymentQueueEntry', params: [environment: deploymentQueue?.environment?.id,id: deploymentPlanInstance?.id ]) { "Add to ${deploymentQueue}" }  + "</div>"
      }

    }


    def concatList = { attrs, body ->

      def from = attrs.from
      out << from.join(',')

    }


    def prioritySelect = { attrs, body ->

      def hostClassInstance = HostClass.get(attrs['id'])
      def props = [:]
      props.name = "priority"
      props.from = ['FIRST','NORMAL','LAST']
      props.keys = ['0','1','2']
      props.noSelection = ['-1':'-SELECT PRIORITY-']
      if (hostClassInstance)
        props.value = Integer.toString(hostClassInstance?.priority)
      out << g.select(props)
    }

  	def teaser = { attrs ->

	  def limit = (attrs.limit ? attrs.limit.toInteger() : 30 )
	  def content = (attrs.content ? attrs.content : "" )
        
	  def tokens = content.split(/\s+/)

	  def max = ( limit > tokens.size() ) ? tokens.size() :  limit

        (0..<max).each { out << tokens[it] + " "}

        if (tokens.size() > 1 )
          out << " ... "

	}

}