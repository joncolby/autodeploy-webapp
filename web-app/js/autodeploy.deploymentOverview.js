MessageProcessor = function(data,item){
	$.gritter.add({
		title: data.title,
		text: data.message,
		sticky: (data.type == 'error' || data.type == 'warning'),
		class_name:data.type
	});
	if ($(item).hasClass('action_remove')) {
		$(item).closest('tr').next().find('td:nth-child(2) a').click();
		$(item).closest('tr').remove();
	}
	else{
		$('.wrapper ul.queues').data('this').updater.restart();
	}
}

FieldMessageProcessor = function(data,container){
	if (data.type != 'error')
		container.saveSuccessAction();
	else{
		container.find('p[name]').InputError().reset();
		for (var i=0; i<data.fields.length; i++){
			container.find('p[name='+data.fields[i].field+']').InputError().showError(data.fields[0].message);
		}
	}
}


QueueDialog = function(options){

	this.afterAjaxAction = function(){} // to overwrite

	this.getContents = function(){
		if ($('.queueContainer .queues li.active a').attr('queueId'))
			$.ajax({url:options.url,data:{id:$('.queueContainer .queues li.active a').attr('queueId')},success:this.afterAjaxAction});
		else {
			container.attr('title','Error').append('Please select a queue first!').dialog();
		}
	}
}

$.DeploymentPlanDialog = function(options){

	var container = $('<div class="containerWrapper"/>').attr('title','Plan management');
	var dialog;
	options = $.extend(options,{
		close:function(){
			$(this).dialog("destroy").remove();
		},modal:true})
		
	QueueDialog.apply(this,arguments);  // "ableitung"
	
	this.afterAjaxAction = function(data){
		container.append(data);
		dialog = container.dialog(options);
		container.find('.right input[type=submit]').button();
		container.find('.selectList').hide();
		container.find('.right').hide();
		var teams = $('.selectList.teams').TeamList().build();
	} 
	
	this.getContents();
	return container;
}


$.AppRevisionDialog = function(options){
	options = $.extend(options,{
		close:function(){
			$(this).dialog("destroy").remove();
		},modal:true})
		
	QueueDialog.apply(this,arguments);  // "ableitung"
	var container = $('<div class="containerWrapper entryDetails"/>').attr('title','Plan Management');
	var table = $('<table></table>');
	var appBody = $('<tbody class="appView"></tbody>').AppRevisonBody(null);	
	var dialog;
	
	this.afterAjaxAction = function(data){
		table.append(appBody);
		container.append(table);
		dialog = container.dialog(options);
		appBody.create(data.apps);
	} 
	this.getContents();

	return container;
}


$.fn.QueueList = function(){
	var that = this;
	this.entryTable = $('.wrapper .queueEntries table').QueueEntryTable(this);
	this.detailsTable = $('.wrapper .entryDetails table').QueueEntryDetailsTable(this);
    this.updater = null;
	
	this.processData = function(data,newEntryTable,newDetailsTable){
		if (data.queueEntries != null){
			if (newEntryTable)
				this.entryTable.create(data.queueEntries);
			else
				this.entryTable.update(data.queueEntries);
		}
		if (data.entryDetails != null){
            if (newDetailsTable)
                this.detailsTable.create(data.entryDetails.content);
            else {
                this.detailsTable.update(data.entryDetails.content);
            }
			this.entryId = data.entryDetails.entryId;
			this.entryTable.setActive(data.entryDetails.entryId);
		}
		this.timestamp = data.lastTimeStamp;
	}
	
	this.select = function(){
			$this=$(this);
			if ($this.hasClass('modal')){
				$.DeploymentPlanDialog({url:$this.attr('href'),height:"500",width:"1000"});
				return false;
			}
			else if ($this.hasClass('modalDiv')){
				$.AppRevisionDialog({url:$this.attr('href'),height:"500",width:"1000"});
				return false;
			}
            $('.queueEntryHeader .queueText').html($this.text());
			$.ajax({url:$this.attr('href'),data:{},
				success:function(data){
					that.pollurl = $this.attr('href');
					$this.parent().parent().children().removeClass('active');
					$this.parent().addClass('active');
					$.cookie('autodeploy_queueId',$this.attr('queueId'));
					that.processData(data,true,true);
					$('.wrapper .fastDeploy form[name=syncEnv] select option').show();
					$('.wrapper .fastDeploy form[name=syncEnv] select option[value='+$this.attr('queueId')+']').hide();
					that.startUpdater();
					
			},dataType:'json'});
			return false;
	}	
	
	$('.wrapper .entryDetails .ui-tabs li a').bind('click',this,function(e){
		$(this).closest('ul').find('li').removeClass('ui-state-active').removeClass('ui-tabs-selected');
		$(this).closest('li').addClass('ui-state-active').addClass('ui-tabs-selected');
		$.cookie('autodeploy_view',$(this).attr('href').replace('#',''));
		e.data.detailsTable.appView = ($(this).attr('href').replace('#','') == 'appView' );
		e.data.entryTable.getActive().find('td:nth-child(2) a').click();
		return false;
	})
	
	this.startUpdater = function(){
        if (this.updater != null) {
            this.updater.stop();
        }
		this.updater = $.PeriodicalUpdater(this.pollurl, {
		      data: function() {
		    	  return { timestamp:that.timestamp, entryId: that.entryId }
		    	  },
		    	  error:function(){

		    	  },
		      type:'json'
		   },
		   function(data){
			   that.processData(data,false,false);
		});
	}
	
	this.init = function(){
		this.data('this',this);
		var queue = $.cookie('autodeploy_queueId');
		this.find('li a').unbind('click').bind('click',this.select);
        var queueElement = queue != null ? this.find('li a[queueId='+queue+']') : this.find('li a').first();
        queueElement.click();
	}
	
	this.init();
}

$(function(){
	$('.wrapper ul.queues').QueueList();
	$('.wrapper .fastDeploy input[type=submit]').button();
	$('.wrapper .fastDeploy select[name=teamId]').bind('change',function(){
		var $this = $(this);
		$.ajax({url:$(this).find(':selected').attr('url'),
			success:function(data){
				$('.wrapper .fastDeploy select[name=planId]').empty();
				for (var i=0; i < data.length; i++){
					$('.wrapper .fastDeploy select[name=planId]').append('<option value="'+data[i].id+'">'+data[i].name+'</option>')
				}
			}
		})
	});

	$('.wrapper .fastDeploy form[name=assignPlan]').bind('submit',function(){
			var that = this;
			$.ajax({url:$(this).attr('action'),
				data:{
					planId:$(this).find('[name=planId]').val(),
					queueId:$('.queueContainer li.active a').attr('queueId'),
					revision:$(this).find('[name=revision]').val()
				},
				success:function(data) {
					$.cookie('autodeploy_teamId',$(that).find('[name=teamId]').val());
					$.cookie('autodeploy_planId',$(that).find('[name=planId]').val());
					MessageProcessor(data);
				}});
			return false;
	})
	$('.wrapper .fastDeploy form[name=syncEnv] select').bind('change',function(){ $(this).closest('form').submit()})
	$('.wrapper .fastDeploy form[name=syncEnv]').bind('submit',function(){
		var that = this;
		$.ajax({url:$(this).attr('action'),
			data:{
				id:$('.queueContainer li.active a').attr('queueId'),
				sourceId:$(this).find('select').val()
			},
			success:function(data) {
				MessageProcessor(data);
			}
		});

		$(this).find('select').val(0);
		return false;
	})
	
	
});