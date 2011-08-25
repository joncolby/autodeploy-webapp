
$.fn.QueueEntryRow = function(queueList){
	this.id = null;
	this.values = null;
	var that = this;
	this.status = null;
	this.actions = null;
		
	AbstractRow.apply(this,arguments);  // "ableitung"
	
	this.doAction = function(event){
		var item = this;
		var data = {
				entryId: that.id,
				timestamp: queueList.timestamp,
				fulldetails:true
		}
		
		$.ajax({url:$(this).attr('href'),data:data,success:function(data){
			MessageProcessor(data, item);
			if ($(item).hasClass('action_remove')) {
				$(item).closest('tr').next().find('td:nth-child(2) a').click();
				$(item).closest('tr').remove();
			}
		},dataType:'json'});
		return false;
	};
	
	this.fetchDetails = function(){
		var data = {
				entryId: that.id,
				timestamp: queueList.timestamp,
				fulldetails:true
		}
		
		$.ajax({url:queueList.pollurl,data:data,success:function(data){
			queueList.processData(data,false,true);
		},dataType:'json'});
		return false;
	}
	
	this.update = function(data){
		this.values = data;
		this.status.set(data);
		this.actions.set(data);
	}	

	this.create = function(data){
		var td = null;
		var item = null;

		this.values = data;
		this.id = data.entryId;
		this.attr('entryId',data.entryId);
		this.appendEntry(data.team);
		item = $('<a href="#">'+data.name+'</a>');
		item.bind('click',this,this.fetchDetails)
		this.appendEntry(item);
		this.appendEntry(data.revision);
		this.status = $('<td/>').StatusContainer().set(data);
		this.append(this.status);
		this.appendEntry(data.created);
		this.actions = $('<td></td>').ActionsContainer(this).set(data);
		this.append(this.actions);

		return this;
	}
	
	return this;
}

$.fn.QueueEntryDetailsHostRow = function(queueList){

	var that = this;
	this.hosts = {};
	
	AbstractRow.apply(this,arguments);  // "ableitung"
	
	this.update = function(data){
		for(var x=0;x<data.hosts.length;x++){
			this.hosts[data.hosts[x].hostId].update(data.hosts[x]);
		}
	}
	
	this.create = function(data){
		this.values = data;
		this.id = data.hostclassId;
		//var tr = $('<tr class="ui-widget-header"></tr>'); // old orange
		var tr = $('<tr class="ui-state-hover"></tr>');
		var ul = null;
		var td = null;
		var item = null;
		var div = null;

		tr.append('<td>'+data.hostclassName+'</td>');
		td = $('<td colspan="2" />');
		for(var x=0;x<data.applications.length;x++){
			div = $('<div class="application"/>');
			td.append(div);
			item = $('<span revision="'+data.applications[x].revision+'" application="'+data.applications[x].name+'" title="'+data.applications[x].name+' (revision '+data.applications[x].revision+')" class="'+data.applications[x].type+'"></span>');
			item.tooltip({showURL: false, fade: 300, top: -10, left: 15, bodyHandler: function() {
                console.log(this);
                return $(this).attr('application') + '&nbsp;<span class="important tooltip">' + $(this).attr('revision') + '</span>';
            }});
			div.append(item);
			div.append(data.applications[x].name + '<span class="important small">(' + data.applications[x].revision + ')</span>');
		}
		tr.append(td);
		this.append(tr);
		
		for(var x=0;x<data.hosts.length;x++){
			tr = $('<tr></tr>').DetailsHostRow();
			tr.create(data.hosts[x]);
			this.append(tr);
			this.hosts[data.hosts[x].hostId] = tr;
		}
		return this;
	}

	return this;
	
}

$.fn.QueueEntryDetailsAppRow = function(queueList){

	var that = this;
	this.apps = {};
	
	AbstractRow.apply(this,arguments);  // "ableitung"
	
	this.update = function(data){
		for(var x=0;x<data.hosts.length;x++){
			this.hosts[data.hosts[x].hostId].update(data.hosts[x]);
		}
	}
	
	this.create = function(data){
		this.values = data;
		this.id = data.hostclassId;
		var tr = $('<tr class="ui-widget-header"></tr>');
		var ul = null;
		var td = null;
		var item = null;
		var div = null;

		tr.append('<td>'+data.hostclassName+'</td>');
		td = $('<td colspan="5" />');
		for(var x=0;x<data.hosts.length;x++){
			div = $('<div class="host"/>');
			td.append(div);
			item = $('<span title="'+data.hosts[x].name+'"></span>');
			item.tooltip({showURL: false, fade: 300, top: -10, left: 15});
			div.append(item);
			div.append(data.hosts[x].name);
		}
		tr.append(td);
		this.append(tr);

		this.append('<tr><th>Name</th><th>Pillar</th><th>Revision</th><th>Type</th><th>Context</th><th></th></tr>');
		
		for(var x=0;x<data.applications.length;x++){
			tr = $('<tr></tr>').DetailsAppRow();
			tr.create(data.applications[x]);
			this.append(tr);
			this.apps[data.applications[x].name] = tr;
		}
		return this;
	}

	return this;
	
}

$.fn.DetailsHostRow = function(queueList){
	var that = this;
	this.messages = null;
    this.values = null;
	this.status = null;
	
	AbstractRow.apply(this,arguments);  // "ableitung"
	this.update = function(data){
		this.values = data;
		this.messages.update(data.messages);
		this.status.set(data);
		return this;
	}
	
	this.plusAction = function(){
		var $this = $(this);
		if ($this.hasClass('ui-icon-plus')){
			$this.removeClass('ui-icon-plus').addClass('ui-icon-minus');
			$this.parent().addClass('active');
		}
		else{
			$this.addClass('ui-icon-plus').removeClass('ui-icon-minus');
			$this.parent().removeClass('active');
		}
	}
	
	this.create = function(data){
		this.values = data;
		this.append('<td class="col1">'+data.name+'</td>');

		this.status = $('<td col="2"/>').StatusContainer().set(data);
		this.append(this.status);
		
		this.messages = $('<ul></ul>').DetailsMessagesTable();
		this.messages.create(data.messages);
		
		var td = $('<td class="col3"></td>');
		var plus = $('<span class="ui-icon ui-icon-plus"><span>');
		plus.button().bind('click',this.plusAction);
		td.append(plus,this.messages);
		this.append(td);
		return this;
	}

	return this;
}

$.fn.DetailsAppRow = function(queueList){
	var that = this;
	this.messages = null;
    this.values = null;
	this.status = null;
	
	AbstractRow.apply(this,arguments);  // "ableitung"
	this.update = function(data){
		this.values = data;
		this.messages.update(data.messages);
		this.status.set(data);
		return this;
	}
	
	this.doAction = function(event){
		var item = this;
		$.ajax({url:$(this).attr('href'),data:{},success:function(data){
			MessageProcessor(data);
		},dataType:'json'});
		return false;
	};	
	this.create = function(data){
		this.values = data;
		this.append('<td>'+data.name+'</td>');
		this.append('<td>'+data.pillar+'</td>');
		var revision = "";
		if (data.revision != null) revision = '<span class="success">' + data.revision + '</span>';
		else if(data.existsInEnv) revision = '<span class="error">not deployed</span>';
		else revision = 'N/A';
		
		this.append('<td>'+revision+'</td>');
		this.append('<td>'+data.type+'</td>');
		
		var context = "";
		if (data.context != null) context = data.context;
		this.append('<td>'+context+'</td>');

		this.actions = $('<td></td>').ActionsContainer(this).set(data);
		this.append(this.actions);
		return this;
	}

	return this;
}

$.fn.EntryMessageRow = function(queueList){
	var that = this;

	AbstractRow.apply(this,arguments);  // "ableitung"
	this.update = function(data){
		// there are no messages to update
		return this;
	}
	
	this.create = function(data){
		this.values = data;
		this.id = data.id;
		this.append(this.values.message);
		this.addClass(this.values.type);
		
		return this;
	}

	return this;
}
