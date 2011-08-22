
$.fn.SelectableList = function(options){
	this.settings = {
			headline: "",
			name:"",
			data:null,
			multiSelect:false,
			disabled:false,
			onclick:null,
			success: null
	}
	this.settings = $.extend(this.settings,options);
	var ul = null;
	
	this.getSelected = function(){
		var selected = this.find('.active');
		var ids = [];
		selected.each(function(){
			ids.push($(this).attr('itemId'));
		});
		return ids.join(',');
	}
	
	this.enableList = function(){
		this.settings.disabled=false;
		this.find('.all,.none').show();
		this.find('li').show().each(function(){
			if ($(this).hasClass('wasActive')) $(this).removeClass('wasActive').addClass('active');
		}).unbind('click').bind('click',this,this.click);
	}
	
	this.disableList = function(){
		this.settings.disabled=true;
		this.find('.all,.none').hide();
		this.find('li').each(function(){
			if ($(this).hasClass('active')) $(this).removeClass('active').addClass('wasActive');
			else $(this).unbind('click').hide();
		});
	}

	this.clickAction = function(e){} // to override
	this.successAction = function(data){} // to override
	this.buildAction = function(){} // to override
	this.newAction = function(){} // to override
	
	this.click = function(e){
		if (!e.data.settings.multiSelect) e.data.find('li').removeClass('active');
		$(this).toggleClass('active');
		e.data.clickAction(e);
		var successFunction = e.data.successAction;
		if ($(this).attr('itemId')=='0') successFunction = e.data.newAction;
		
		if (typeof $(this).attr('url') != 'undefined' && $(this).attr('url') != ""){
			$.ajax({
				url:$(this).attr('url'),
				context: e.data,
				success: successFunction
			})
		}
	}
	this.selectAll = function(e){
		if (!e.data.settings.disabled)
		e.data.find('li').addClass('active');
		return false;
	}
	this.selectNone = function(e){
		if (!e.data.settings.disabled)
		e.data.find('li').removeClass('active');
		return false;
	}
	
	this.build = function(options){
		this.settings = $.extend(this.settings,options);
		ul = this.find('ul');
		if (this.settings.data != null){
			ul.empty();
			var item = null;
			var url ="";
			for (x in this.settings.data){
					url = (typeof this.settings.data[x].url != "undefined") ? "url='"+this.settings.data[x].url+"'": "";
					item = $("<li itemId='"+this.settings.data[x].id+"' "+url+">"+this.settings.data[x].name+"</li>");
					if (this.settings.data[x].selected) item.addClass('active');
					ul.append(item);
			}
		}
		if (this.settings.disabled){
			this.disableList();
		}
		else
			this.find('li').unbind('click').bind('click',this,this.click);
		if (ul.find('li').length == 0) this.hide();
		else this.css('display','');
		this.find('.all').unbind('click').bind('click',this,this.selectAll)
		this.find('.none').unbind('click').bind('click',this,this.selectNone)
		this.buildAction();
		return this;
	}
	
	return this;
}

$.fn.InputError = function(){

	this.data('this',this);
	this.showError = function(msg){
		this.find('label').css('color','red');
		this.append('<span class="error">'+msg+'</span>');
		return this;
	}
	this.reset = function(){
		this.find('.error').remove();
		this.find('label').css('color','');
		return this;
	}


	this.unbind('enable').bind('enable',this,function(event){event.data.enableAction()});
	this.unbind('disable').bind('disable',this,function(event){event.data.disableAction()});
	return this;
}

$.fn.TextArea = function(){

	
	this.enableAction = function(a){
		var field = this.find('span');
		field.html("<textarea name ='"+this.attr('name')+"'>"+field.html()+"</textarea>");
	}
	
	this.disableAction = function(a){
		var field = this.find('span');
		var textarea = field.find('textarea');
		if (textarea.length == 1) field.html(textarea.val());
	}

	$.fn.InputError.apply(this,arguments);
	
	this.set = function(value){
		this.find('span').html(value);
	}
	return this;
}

$.fn.InputField = function(writeable){
	if (typeof writeable == 'undefined') writeable = true;
	this.enableAction = function(){
		var field = this.find('span');
		if (writeable)
			field.html("<input type='text' name ='"+this.attr('name')+"' value='"+field.html()+"'/>");
	}
	
	this.disableAction = function(){
		var field = this.find('span');
		var input = field.find('input');
		if (writeable && input.length == 1) field.html(input.val());
	}

	$.fn.InputError.apply(this,arguments);
	this.set = function(value){
		this.find('span').html(value);
	}
	return this;
}

$.fn.Checkbox = function(){

	this.enableAction = function(){
		var field = this.find('span');
		var selected = (field.html()=='true')? "checked='checked'" : "";
		field.html("<input type='checkbox' name ='"+this.attr('name')+"' "+selected+"/> enabled");
	}

	$.fn.InputError.apply(this,arguments);
	this.disableAction = function(){
		var field = this.find('span');
		var input = field.find('input');
		if (input.filter(':checked').length==1) field.html("true");
		else field.html("false");
	}
	
	this.set = function(value){
		if (value) this.find('span').html("true");
		else this.find('span').html("false")
	}
	return this;
}

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
