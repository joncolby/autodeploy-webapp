

$.fn.DeploymentPlanDetails = function(options){
	this.appsList = this.find('.selectList.applications').SelectableList();
	
	this.enableFields = function(){
		this.find('p').trigger('enable');
		this.appsList.enableList();
	}	
	
	this.disableFields = function(){
		this.find('p').trigger('disable');
		this.appsList.disableList();
	}
	
	this.getValues = function(){
		var values = {};
		this.find('input[type=text],textarea').not('input[name="revision"]').each(function(){
			values[$(this).attr('name')] = $(this).val();
		})
		this.find('input[type=checkbox]').each(function(){
			values[$(this).attr('name')] = ($(this).filter(':checked').length==1)?true:'';
		});
		values['apps'] = this.appsList.getSelected();
		values['teamId'] = $('.container .left .teams').SelectableList().getSelected();
		return values;
	}
	
	this.makeEditAction = function(e){
		e.data.enableFields();
		e.data.find('[name=cancel][type=submit]').show().unbind('click').bind('click',e.data,e.data.cancelAction);
		$(this).val('Save').unbind('click').bind('click',e.data,e.data.saveAction);
		$('.container .revisionBox').hide();
	}
	
	this.saveSuccessAction = function(e){
		this.disableFields();
		this.find('[name=edit][type=submit]').val('Edit').unbind('click').bind('click',this,this.makeEditAction);
		this.find('[name=cancel][type=submit]').hide();
		$('.container .revisionBox').show();
		var selectedPlan = $('.selectList.deploymentPlans .active');
		var planName = this.find('[name=name] span').html();

		$('.fastDeploy [name=teamId]').change();
		$('.selectList.teams .active').click();
	}
	

	this.saveAction = function(e){
			$.ajax({url:$(this).attr('url'),
				data:e.data.getValues(),
				success:function(data){
					FieldMessageProcessor(data,e.data);
				}
			});
	}	
	
	this.cancelAction = function(e){
		e.data.find('p[name]').InputError().reset();
		$('.selectList.deploymentPlans li.active').click();
	}
	
	this.setFields = function(data){
		this.appsList.build({data:data.apps,multiSelect:true,disabled:true});

		this.find('[name=contribution]').TextArea().set(data.contribution);
		this.find('[name=ticket]').InputField().set(data.ticket);
		this.find('[name=name]').InputField().set(data.name);
		this.find('[name=created]').InputField(false).set(data.created);
		this.find('[name=modified]').InputField(false).set(data.modified);
		this.find('[name=requiresPropertyChanges]').Checkbox().set(data.requiresPropertyChanges);
		this.find('[name=requiresDatabaseChanges]').Checkbox().set(data.requiresDatabaseChanges);
		this.find('[name=edit][type=submit]').val('Edit').attr('url',data.url).unbind('click').bind('click',this,this.makeEditAction);
		this.find('[name=cancel][type=submit]').hide();
		return this;
	}
	return this;
}


$.fn.TeamList = function(options){
	$.fn.SelectableList.apply(this,arguments);  // "ableitung
	this.buildAction = function(){
		var team = $.cookie('autodeploy_teamId');
		if (team != null) this.find('[itemId='+team+']').click();
	}
	this.successAction = function(data){
			$('.container .revisionBox input').attr('disabled', 'true');
			$('.container .right').hide();
			$.cookie('autodeploy_teamId',this.getSelected());
			$('.selectList.deploymentPlans').DeploymentPlanList({
			data:data
		}).build({});
	}
	return this;
}

$.fn.DeploymentPlanList = function(options){

	$.fn.SelectableList.apply(this,arguments);  // "ableitung

	this.buildAction = function(){
		var plan = $.cookie('autodeploy_planId');
		if (plan != null) this.find('[itemId='+plan+']').click();
	}
	this.clickAction = function(e){
		$('.container .revisionBox input').removeAttr('disabled');
		$('.container .revisionBox input[type=submit]').unbind('click').bind('click',e.data,function(e){
			$.ajax({url:$(this).attr('url'),
					data:{
						queueId:$('.queueContainer .queues li.active a').attr('queueId'), 
						planId:e.data.getSelected(), 
						revision:$('.container .revisionBox input[type=text]').val()
					},success:function(){
						$('.ui-dialog-titlebar-close span').click();
					}
			});
		});
	}
	
	this.newAction = function (data){
		data.contribution ="";
		data.created ="";
		data.modified ="";
		data.name ="";
		data.requiresDatabaseChanges =false;
		data.requiresPropertyChanges =false;
		data.ticket ="";
		var DPD = $('.container .right').DeploymentPlanDetails();
		DPD.setFields(data).css('display','').enableFields();
		
		$('.container .right').find('[name=cancel][type=submit]').hide();
		$('.container .right').find('[name=edit][type=submit]').val('Save').unbind('click').bind('click',DPD,DPD.saveAction);
		
		$('.container .revisionBox input[type=text]').val("");
        $('.container .revisionBox').hide();
	}
	
	this.successAction = function(data){
		$('.container .right').DeploymentPlanDetails().setFields(data).css('display','');
		$('.container .revisionBox input[type=text]').val("");
        $('.container .revisionBox').show();
		$.cookie('autodeploy_planId',this.getSelected());
	}
	return this;
}
