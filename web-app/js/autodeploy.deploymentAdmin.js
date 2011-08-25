$.fn.EntryRow = function(){
	this.id = null;
	this.values = null;
	var that = this;
	this.status = null;
	this.actions = null;
		
	AbstractRow.apply(this,arguments);  // "ableitung"
	

	this.doAction = function(){
		var $this = $(this);
		if ($(this).hasClass('action_remove')) {
			$.ajax({url:$(this).attr('href'),success:function(data){
				MessageProcessor(data);
				$this.closest('tr').remove();
			}});
		}
		else
			$.TableEntryEditDialog({item:this,height:"500",width:"1000"});
		return false;
	}
	
	
	this.create = function(data){
		this.values = data;
		
		for (var title in data){
			if (title != 'actions')
				this.appendEntry(data[title]);
		}
		
		this.actions = $('<td></td>').ActionsContainer(this).set(data);
		this.append(this.actions);
		//this.id = data.entryId;
		//this.attr('entryId',data.entryId);
		
		return this;
	}
	
	return this;
}

$.fn.EntryTable = function(){
	var that = this;
	var head = this.find('thead tr');
	this.entries = {};
	this.idString = "entryId";
	AbstractTable.apply(this,arguments);  // "ableitung"
	
	this.getNewEntry = function(data){ return $('<tr></tr>').EntryRow().create(data) }
	
	this.doAction = function(){
		$.TableEntryEditDialog({item:this,height:"500",width:"1000"});
		return false;
	}
	
	/*
	 * @override
	 * */
	this.create = function(result){
		var current = null;
		this.data = result.data;
		this.container.children('.appended').remove();
		head.empty();
		for (var title in result.data[0]){
			current = $('<td>'+title+'</td>');
			head.append(current);
			if (title != 'actions')
				$(current).append($('<input/>').bind('keyup',that,that.searchAction));
			else
				$(current).append($('<span/>').ActionsContainer(this).set(result));
		}
		for(var x=0;x<result.data.length;x++){
			this.appendEntry(this.getNewEntry(result.data[x]));
		}
	}
	
	this.bind('appendEntry',function(event,entry){
		that.appendEntry(that.getNewEntry(entry));
	})
	
	this.container = $('<tbody></tbody>');
	this.append(this.container);
	return this;
};


$.TableEntryEditDialog = function(options){

	var that = this;
	options = $.extend(options,{
		close:function(){
			$(this).dialog("destroy").remove();
		},modal:true});
	var container = $('<form/>');
	var wrapper = $('<div class="containerWrapper tableEntryDetails"/>').attr('title','Edit Entry').append(container);
	var dialog;
		
	this.afterSaveAction = function(data){
		MessageProcessor(data.message);
		if (typeof data.entry != 'undefined'){
			$(options.item).closest('table').trigger('appendEntry',[data.entry]);
		}
		dialog.dialog("destroy").remove();
	}
	
	this.afterLoadAction = function(data){
		var fieldContainer = null;
		var action = null;

		container.attr('action', data.saveUrl);
		for(field in data.values){
			switch (data.values[field].type){
				case 'text':
					fieldContainer = $('<p/>');
					fieldContainer.InputField().create(field,data.values[field].value);
				break;
				case 'select':
					fieldContainer = $('<p/>');
					fieldContainer.SelectField().create(field,data.values[field].value);
			}
			if (data.values[field].disabled == true) fieldContainer.trigger('disable');
			container.append(fieldContainer);
		}
		fieldContainer = $('<p class="actions"></p>');
		action = $('<input type="submit" name="edit" value="Save">');
		action.bind('click',function(){
			var form = $(this).closest('form');
			$.ajax({url:form.attr('action'),data:form.serialize(),success:that.afterSaveAction,dataType:'json'});
			return false;
		});
		fieldContainer.append(action.button());

		action = $('<input type="submit" name="cancel" value="Cancel">');
		action.bind('click',function(){
			dialog.dialog("destroy").remove();
			return false;
		});
		fieldContainer.append(action.button());
		container.append(fieldContainer);
		dialog = wrapper.dialog(options);
	} 
	
	$.ajax({url:$(options.item).attr('href'),data:{},
		success:function(data){
			if (typeof data.error != 'undefined') MessageProcessor(data.message, options.item);
			that.afterLoadAction(data);
	},dataType:'json'});
	return wrapper;
}

$.fn.MenuList = function(){
	var that = this;
	this.entryTable = $('.wrapper .tableEntries table').EntryTable();
	
	this.select = function(){
			$this=$(this);
			if ($this.hasClass('modal')){
				// if to delete
				return false;
			}
            $('.queueEntryHeader .queueText').html($this.text());
			$.ajax({url:$this.attr('href'),data:{},
				success:function(data){
					$this.parent().parent().children().removeClass('active');
					$this.parent().addClass('active');
					that.entryTable.create(data)
					
			},dataType:'json'});
			return false;
	}	
	
	
	this.init = function(){
		this.find('li a').unbind('click').bind('click',this.select);
        var entryElement = this.find('li a').first();
        entryElement.click();
	}
	
	this.init();
}

$(function(){
	$('.wrapper ul.queues').MenuList();

});