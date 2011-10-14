$.LoadingIndicator = function(loading){
	var indicator = $('.loadingIndicator');
	var body = $('body');
	var w = $(window);
	
	function addIndicator(){
		var pb = $('<div class="ajaxLoader"/>');
		indicator = $('<div class="loadingIndicator"/>').append(pb);
		body.append(indicator);
	}
	
	if (indicator.length == 0) addIndicator();
	
	if (loading) {
		indicator.css({height:body.height(),width:body.width()});
		indicator.find('.ajaxLoader').css({'margin-top':(w.scrollTop()+(w.height()/2)-50)+'px'});
		indicator.show();
	}
	else indicator.hide();
}

AbstractRow = function(){	
	this.data('this',this);
	this.appendEntry = function(data){
		var entry = $('<td></td>');
		entry.append(data)
		this.append(entry);
	}
}

MessageProcessor = function(data,item){
	$.gritter.add({
		title: data.title,
		text: data.message,
		sticky: (data.type == 'error' || data.type == 'warning'),
		class_name:data.type
	});
	$('.wrapper ul.queues').trigger('resetTimer');
	
}

FieldMessageProcessor = function(data,container){
	if (typeof data == 'undefined' || data.type != 'error')
		container.saveSuccessAction();
	else{
		container.find('p[name]').trigger('reset');
		for (var i=0; i<data.fields.length; i++){
			container.find('p[name='+data.fields[i].field+']').trigger('error',data.fields[i].message);
		}
	}
}



AbstractTable = function(){ // abstract class
	var that = this;
	this.data('this',this);

	var searchTimeout = null;
	
	this.appendEntry = function(entry){
		this.entries[entry.id] = entry;
		this.container.append(entry.addClass('appended'));
		return entry;
	}
	
	this.prependEntry = function(entry){
		this.entries[entry.id] = entry;
		this.container.prepend(entry.addClass('appended'));
		return entry;
	}
	
	this.getEntry = function(id){
		return (typeof this.entries[id] == 'object') ? this.entries[id] : null;
	}
	
	this.create = function(data){
		this.data = data;
		this.container.children('.appended').remove();
		for(var x=0;x<data.length;x++){
			this.appendEntry(this.getNewEntry(data[x]));
		}
	}
	
	this.update = function(data){
		var entry = null;
		for(var x=0;x<data.length;x++){
			entry = this.getEntry(data[x][this.idString]);
			if (entry == null)
				this.prependEntry(this.getNewEntry(data[x]));
			else
				entry.update(data[x]);
		}
	}
	
	this.searchAction = function(){
		clearTimeout(searchTimeout);
		searchTimeout = setTimeout(function(){
			console.log('search begin');
			var starttime = (new Date()).getTime();
			var $this = $(that);
			var search = {};
			var show = true;
			
			that.find('tr:first-child th input,tr:first-child td input').each(function(index){
				if ($(this).hasClass('greyed')){ search[$(this).attr('title')] = ""}
				else { search[$(this).attr('title')] = $(this).val(); }
			})
			
			var data = $.extend([],that.data);
			for(var i=data.length-1;i>=0;i--){
				show = true;
				for (x in search){
					searchString = new RegExp(".*"+search[x]+".*","i");
					if (search[x] != "" && !searchString.test(data[i][x])) show = false;
				}
				if (!show) data.splice(i,1);
			}
			$.AppendController(data,that);
			console.log('search complete in '+((new Date()).getTime()-starttime) + 'ms');
		},1000);
	}	
//	this.searchAction = function(){
//		clearTimeout(searchTimeout);
//		searchTimeout = setTimeout(function(){
//			console.log('search begin');
//			var starttime = (new Date()).getTime();
//			var $this = $(that);
//			var search = {};
//			that.find('tr:first-child th input,tr:first-child td input').each(function(index){
//				if ($(this).hasClass('greyed')){ search[index] = ""}
//				else { search[index] = $(this).val(); }
//			})
//			
//			var trs = $this.closest('table').find('tbody tr');
//			
//			trs.each(function(){
//				var field = null;
//				var show = true;
//				var searchString = "";
//				var fields = $(this).find('td');
//				for (x in search){
//					field = $(fields[x]); 
//					searchString = new RegExp(".*"+search[x]+".*","i");
//					if (field.length==1 && search[x] != "" && field.text() != "" && !searchString.test(field.text())) show = false;
//				}
//	
//				if (show) $(this).show();
//				else $(this).hide();	
//
//			});
//			console.log('search complete in '+((new Date()).getTime()-starttime) + 'ms');
//			
//		},1000);
//	}
}

$.AppendController = function(data,table){
	var that = this;
	var div = table.closest('div');
	
	this.id = Math.floor(Math.random()*10000000);
	
	table.find('tbody').empty();
	function appendEntryRecursive(data,id){
		if (data.length == 0 || (that.id != id)) {div.find('.loader').remove();return;}
		table.appendEntry(table.getNewEntry(data.shift()));
		setTimeout(function(){
			if (that.id == id) appendEntryRecursive(data,id);	
		},0);
	}
	
	function addLoading(){
		div.css('position','relative');
		if (div.find('.loader').length == 0){
			var loader = $('<img class="loader" src="../images/ajax-loader-big.gif"/>').css({
				position:'absolute',
				bottom:'0',
				left:'0'
			});
			div.append(loader);
			div.bind('scroll',function(){
				loader.css('bottom',-$(this).scrollTop());
			})
		}
	}
	
	appendEntryRecursive(data,this.id);
	addLoading();
}

$.SearchField = function(title,table){
	var item = $('<input value="'+title+'" title="'+title+'" class="greyed" style="color:#AAA"/>')
	.bind('keyup',table.searchAction)
	.bind('focus',function(){
		$(this).css('color','').removeClass('greyed');
		if($(this).val()==title) {
			$(this).val('');
		}
	}).bind('focusout',function(){
		if($(this).val()==title || $(this).val()=='') {
			$(this).val(title);
			$(this).css('color','#AAA').addClass('greyed');
		}
	});
	return item;
}

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
	var that = this;
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
	
	this.create = function(name){
		var head;
		this.attr('name',name);
		this.addClass('selectList');
		head = $('<h3>'+name+'</h3>');
		head.append('<a class="none" href="#">select none</a>');
		head.append('<a class="all" href="#">select all</a>');
		this.append(head);
		this.append('<ul></ul>');
		return this;
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
	this.bind('updateField',function(){ 
		that.find('input[name='+that.attr('name')+']').remove();
		that.append('<input type="hidden" name="'+that.attr('name')+'" value="'+that.getSelected()+'"/>')
	});
	
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

	this.unbind('error').bind('error',this,function(event,msg){event.data.showError(msg)});
	this.unbind('reset').bind('reset',this,function(event){event.data.reset()});
	this.unbind('enable').bind('enable',this,function(event){event.data.enableAction()});
	this.unbind('disable').bind('disable',this,function(event){event.data.disableAction()});
	return this;
}

$.fn.TextArea = function(){
	
	this.enableAction = function(a){
		var field = this.find('span');
		field.html("<textarea name ='"+this.attr('name')+"'>"+field.html()+"</textarea>");
	}

	this.create = function(name,value){
		var val = value;
		if (val == null) val = "";
		this.attr('name',name);
		this.append('<label>'+name+'</label>');
		this.append('<span><textarea type="text" name="'+name+'">val</textarea></span>');
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
	this.create = function(name, data){
		var val = data.value;
		if (val == null) val = "";
		this.attr('name',name);
		this.append('<label>'+name+'</label>');
		this.append('<span><input type="text" name="'+name+'" value="'+val+'"></span>');
        var input = this.find('input')
        if (data.size != null) {
            input.attr('size', data.size)
        }
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
    console.log(this);
	return this;
}

$.fn.SelectField = function(writeable){
	if (typeof writeable == 'undefined') writeable = true;
	this.enableAction = function(){
		alert('not needed yet, so not implemented');
	}
	this.create = function(name,list){
		var current;
		var select;
		var inputContainer = $('<span></span>');
		this.attr('name',name);
		this.append('<label>'+name+'</label>');
		this.append(inputContainer);
		select = $('<select name="'+name+'"/>');

		for (var i=0;i<list.length;i++){
			current = $('<option value="'+list[i].id+'">'+list[i].name+'</option>');
			if (list[i].selected) current.attr('selected','selected');
			select.append(current);
		}
		inputContainer.append(select);
	}
	
	this.disableAction = function(){
		alert('not needed yet, so not implemented');
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
	
	this.create = function(name,value){
		this.attr('name',name);
		this.append('<label>'+name+'</label>');
		this.append('<span><input type="checkbox" name="'+name+'" '+((value)?'checked="checked"':'')+'></span>');
	}
	
	
	this.set = function(value){
		if (value) this.find('span').html("true");
		else this.find('span').html("false")
	}
	return this;
}



$.fn.StatusContainer = function (){
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

$.fn.ActionsContainer = function(row){
	var that = this;
	var settings = {
			icons:{
				'edit':'pencil',
				'remove':'close',
				'cancel':'stop',
				'create':'plus',
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
			if (this.find(".action_"+ data.actions[x].type).length  == 0) {
                createItem(data.actions[x]);
            }
		}
		this.find('a.ui-icon:not('+selector.join(',')+')').remove();
		return this;
	}
	return this;
}

