
$.fn.StatusField = function (){
	this.progress = $('<div class="progress"/>').progressbar({ value: 0 });
	this.text = $('<div/>');
	this.container = $('<div class="container"/>').append(this.progress).append(this.text);
	this.append(this.container);
	this.addClass('status');
	
	this.set = function(data){
		if (data.colorState == 'working'){
			this.progress.show();

			this.text.html(data.duration);
			if (data.duration == "unknown"){
				this.progress.addClass('animated');
				this.progress.progressbar("option", "value", 100);
			}
			else{
				this.progress.progressbar("option", "value", data.progress);
			}
		}
		else {
			this.progress.hide();
			this.text.html(data.status);
			if (typeof data.duration != 'undefined' &&  data.duration != 'unknown')
				this.text.append(' <small>(' + data.duration + ')</small>');
		}

		this.text.attr('class',data.colorState);
		return this;
	}
	return this;
}

$.fn.ActionsField = function(row){
	var that = this;
	var settings = {
			icons:{
				'edit':'pencil',
				'remove':'close',
				'cancel':'stop',
				'deploy-all':'play',
				'retry':'circle-triangle-e',
				'rollback':'seek-first'
			}
	}
	function createItem(data){
		item = $('<a class="ui-icon action_'+data.type+'" href="'+data.action+'" title="' +data.title+'"></a>');
		if(typeof settings.icons[data.type] == 'undefined') item.addClass('ui-icon-help');
		else item.addClass('ui-icon-'+settings.icons[data.type]);
		
		item.bind('click',row,row.doAction).button().tooltip({showURL: false, fade: 300, extraClass:"wide"});
		that.append(item);
	}
	this.set = function(data){
		var selector = [];
		for (var x=0;x<data.actions.length;x++){
			selector.push( ".action_"+ data.actions[x].type);
			if (this.find(".action_"+ data.actions[x].type).length  == 0)
				createItem(data.actions[x]);
		}
		this.find('a.ui-icon:not('+selector.join(',')+')').remove();
		return this;
	}
	return this;
}

