

AutodeployDialog = function(options){

	this.afterAjaxAction = function(){} // to overwrite

	this.getContents = function(){
		if ($('.queueContainer .queues li.active a').attr('queueId')){
			$.LoadingIndicator(true);
			$.ajax({url:options.url,data:{id:$('.queueContainer .queues li.active a').attr('queueId')},success:this.afterAjaxAction});
		}
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
		
	AutodeployDialog.apply(this,arguments);  // "ableitung"
	
	this.afterAjaxAction = function(data){
		$.LoadingIndicator(false);
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

$.DashboardOverviewDialog = function(options){
	var container = $('<div class="containerWrapper"/>').attr('title','Dashboard Overview');
	var dialog;
	options = $.extend(options,{
		close:function(){
			$(this).dialog("destroy").remove();
		},modal:true})

	AutodeployDialog.apply(this,arguments);  // "ableitung"

	this.afterAjaxAction = function(data){
		$.LoadingIndicator(false);
		container.append(data);
		dialog = container.dialog(options);
	}

	this.getContents();
	return container;
}

$.ChangesDialog = function(options){
	var container = $('<div class="containerWrapper"/>').attr('title','Autodeploy changelog');
	var dialog;
	options = $.extend(options,{
		close:function(){
			$(this).dialog("destroy").remove();
		},modal:true})

	AutodeployDialog.apply(this,arguments);  // "ableitung"

	this.afterAjaxAction = function(data){
		$.LoadingIndicator(false);
		container.append(data);
		dialog = container.dialog(options);
	}

	this.getContents();
    console.log("init");
	return container;
}


$.AppRevisionDialog = function(options){
	options = $.extend(options,{
		close:function(){
			$(this).dialog("destroy").remove();
		},modal:true})
		
	AutodeployDialog.apply(this,arguments);  // "ableitung"
	var container = $('<div class="containerWrapper entryDetails"/>').attr('title','Plan Management');
	var table = $('<table></table>').AppRevisonTable(null);	
	var dialog;
	
	this.afterAjaxAction = function(data){
		$.LoadingIndicator(false);
		container.append(table);
		dialog = container.dialog(options);
		table.create(data.apps);
	} 
	this.getContents();

	return container;
}


$.fn.NotificationArea = function() {
    var that = this;

    var currentActive = false;
    var currentMessage = null;

    this.process = function(data) {
        if (that.currentActive && typeof data == 'undefined') {
            this.clearNotification();
        } else {
            if (typeof data != 'undefined') {
                var message = data.message + " (from " + data.user + " on " + data.created +")";
                if (message != that.currentMessage) {
                    this.showNotification(message);
                }
            }
        }
    }

    this.clearNotification = function() {
        that.currentActive = false;
        that.currentMessage = null;
        $(this).empty();
        $(this).slideUp("slow");
        //$(this).hide();
    }

    this.showNotification = function(message) {
        that.currentActive = true;
        that.currentMessage = message;
        $(this).empty().append(message);
        $(this).slideDown("slow");
        $(this).show();
    }

    return this;
}

$.fn.QueueList = function(){
	var that = this;
	this.entryTable = $('.wrapper .queueEntries table').QueueEntryTable(this);
	this.detailsTable = $('.wrapper .entryDetails table').QueueEntryDetailsTable(this);
    this.notificationArea = $('.wrapper .notification').NotificationArea();
    this.autoPlay = $('.wrapper .fastDeploy input[name="autoPlay"]')
    this.updater = null;
	
	this.processData = function(data,newEntryTable,newDetailsTable){
        this.notificationArea.process(data.notification);
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
            if (newDetailsTable) {
                this.entryId = data.entryDetails.entryId;
            }
			this.entryTable.setActive(data.entryDetails.entryId);
		}
        if (data.autoPlay) {
            this.autoPlay.prop('checked', 'checked');
        } else {
            this.autoPlay.prop('checked', '');
        }
		this.timestamp = data.lastTimeStamp;
	}
	
	this.select = function(){
			$this=$(this);
			if ($this.hasClass('modal')){
				$.DeploymentPlanDialog({url:$this.attr('href'),height:"500",width:"1000"});
			}
			else if ($this.hasClass('modalDiv')){
				$.AppRevisionDialog({url:$this.attr('href'),height:"500",width:"1000"});
			}
            else if ($this.hasClass('dashboard')) {
                $.DashboardOverviewDialog({url:$this.attr('href'),height:"800",width:"1200"});
            }
			else {
	            $('.queueEntryHeader .queueText').html($this.text());
	            $.LoadingIndicator(true);
				$.ajax({url:$this.attr('href'),data:{},
					success:function(data){
			            $.LoadingIndicator(false);
						that.pollurl = $this.attr('href');
						$this.parent().parent().children().removeClass('active');
						$this.parent().addClass('active');
                        if (data.settings && data.settings.releaseMailByDefault) {
                            $('.wrapper input[name=releaseMail]').attr('checked', 'true');
                        } else {
                            $('.wrapper input[name=releaseMail]').removeAttr('checked');
                        }
						$.cookie('autodeploy_queueId',$this.attr('queueId'));
						that.processData(data,true,true);
						$('.wrapper .fastDeploy .sync form[name=syncEnv] select option').show();
						$('.wrapper .fastDeploy .sync form[name=syncEnv] select option[value='+$this.attr('queueId')+']').hide();
						that.startUpdater();
						
				},dataType:'json'});
			}
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
                      console.log("ERROR!");
		    	  },
		      type:'json'
		   },
		   function(data){
			   that.processData(data,false,false);
		});
	}
	
	this.init = function(){
		this.data('this',this);
		this.bind('resetTimer',function(){ 
			that.updater.restart()
			});
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

    $('.wrapper').find('span.changesdialog').unbind('click').bind('click', function() {
        $.ChangesDialog({url:$(this).attr('url'),height:"500",width:"1050"})
    });

	$('.wrapper .fastDeploy form[name=assignPlan]').bind('submit',function(){
			var that = this;
			$.ajax({url:$(this).attr('action'),
				data:{
					planId:$(this).find('[name=planId]').val(),
					queueId:$('.queueContainer li.active a').attr('queueId'),
					revision:$(this).find('[name=revision]').val(),
                    hostFilter:$(this).find('[name=hostFilter]').val(),
                    releaseMail:$('.wrapper input[name=releaseMail]:checked').val() ? true : false
				},
				success:function(data) {
					$.cookie('autodeploy_teamId',$(that).find('[name=teamId]').val());
					$.cookie('autodeploy_planId',$(that).find('[name=planId]').val());
					MessageProcessor(data);
				}});
			return false;
	})

    $('.wrapper .fastDeploy input[name="autoPlay"]').bind('click', function(event) {
        var queueId = $('.queueContainer li.active a').attr('queueId');
        var state = $(this).prop('checked');
        $.ajax({url:$(this).data('action'),
            data: {
                id: queueId,
                state: state || false
            },
            success: function(data) {
                MessageProcessor(data);
            },
            error: function(data) {
                MessageProcessor(data);
            }

        });


    });
	$('.wrapper .fastDeploy .sync form[name=syncEnv] select').bind('change',function(){ $(this).closest('form').submit()})
	$('.wrapper .fastDeploy .sync form[name=syncEnv]').bind('submit',function(){
		var that = this;
        var syncEle = $('.wrapper .fastDeploy .sync form[name=syncEnv] select');
        syncEle.attr('disabled', 'true');
		$.ajax({url:$(this).attr('action'),
			data:{
				id:$('.queueContainer li.active a').attr('queueId'),
				sourceId:$(this).find('select').val()
			},
			success:function(data) {
                syncEle.removeAttr('disabled');
				MessageProcessor(data);
			},
            error:function() {
                syncEle.removeAttr('disabled');
            }
		});

		$(this).find('select').val(0);
		return false;
	})
	
	
});
