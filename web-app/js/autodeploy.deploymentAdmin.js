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
			$.TableEntryEditDialog({item:this,height:"600",width:"1000"});
		return false;
	}
	
	
	this.create = function(data){
		
		//var starttime = (new Date()).getTime();
		this.values = data;
		
		for (var title in data){
			if (title != 'actions')
				this.appendEntry(data[title]);
		}
		
		that.actions = $('<td></td>').ActionsContainer(that).set(data);
		that.append(that.actions);

		//console.log('table complete in '+((new Date()).getTime()-starttime) + 'ms');
		//this.id = data.entryId;
		//this.attr('entryId',data.entryId);
		
		return this;
	}
	
	return this;
}

$.fn.EntryTable = function(){
	var that = this;
	var head = this.find('thead tr');
	this.appendTimeout = null;
	
	this.entries = {};
	this.idString = "entryId";
	AbstractTable.apply(this,arguments);  // "ableitung"
	
	this.getNewEntry = function(data){ return $('<tr></tr>').EntryRow().create(data) }
	
	this.doAction = function(){
		$.TableEntryEditDialog({item:this,height:"600",width:"1000"});
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
			(function(title){
	            var columnConfig = null
	            if (typeof result.config != 'undefined' && result.config[title]) {
	                columnConfig = result.config[title]
	            }
				current = $('<td>'+title+'</td>');
	            if (columnConfig && columnConfig.width) {
	                current.css('width', columnConfig.width);
	            }
				head.append(current);
				if (title != 'actions'){
					if (!columnConfig || (columnConfig && (typeof columnConfig.searchable == 'undefined' || columnConfig.searchable))) {
						$(current).empty().append($.SearchField(title,that));
					}
				}
				else
					$(current).append($('<span/>').ActionsContainer(this).set(result));
			})(title)
		}

		$.AppendController($.extend([],result.data),that);
//		for(var x=0;x<result.data.length;x++){
//			var starttime =(new Date()).getTime();
//			this.appendEntry(this.getNewEntry(result.data[x]));
//			console.log('row complete in '+((new Date()).getTime()-starttime) + 'ms');
//		}
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
	var saveResponse;
		
	container.saveSuccessAction = function(){
		if (typeof saveResponse.entry != 'undefined'){
			$(options.item).closest('table').trigger('appendEntry',[saveResponse.entry]);
		}
		dialog.dialog("destroy").remove()
	}
	
	this.afterSaveAction = function(data){
		saveResponse = data;
		FieldMessageProcessor(data.message,container);
	}
	
	this.afterLoadAction = function(data){
		var fieldContainer = null;
		var action = null;
		var column = null;
		var listContainer;
		
		container.attr('action', data.saveUrl);
		column = $('<div class="col"/>');
		container.append(column);
		for(field in data.values){
			switch (data.values[field].type){
			case 'text':
				fieldContainer = $('<p/>');
				fieldContainer.InputField().create(field, data.values[field]);
			break;
			case 'textarea':
				fieldContainer = $('<p/>');
				fieldContainer.TextArea().create(field,data.values[field].value);
			break;
			case 'checkbox':
				fieldContainer = $('<p/>');
				fieldContainer.Checkbox().create(field,data.values[field].value);
			break;
			case 'list':
				listContainer = $('<p></p>');
				listContainer.SelectableList({data:data.values[field].value,multiSelect:true}).create(field).build();
			break;
			case 'select':
				fieldContainer = $('<p/>');
				fieldContainer.SelectField().create(field,data.values[field].value);
			}
			if (data.values[field].disabled == true) fieldContainer.trigger('disable');
			column.append(fieldContainer);
			if (column.children().length == 10){
				column = $('<div class="col"/>');
				container.append(column);
			}
		}
		fieldContainer = $('<p class="actions"></p>');
		action = $('<input type="submit" name="edit" value="Save">');
		action.bind('click',function(){
			var form = $(this).closest('form');
			var values = form.serialize();
			form.find('.selectList').trigger('updateField');
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
		column.append(fieldContainer);

		column = $('<div class="col"/>');
		container.append(column);
		column.append(listContainer);
		
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
            $.LoadingIndicator(true);
			$.ajax({url:$this.attr('href'),data:{},
				success:function(data){
					var starttime = (new Date()).getTime();
					console.log('data complete');
					$.LoadingIndicator(false);
					$this.parent().parent().children().removeClass('active');
					$this.parent().addClass('active');
					that.entryTable.create(data);
					console.log('table complete in '+((new Date()).getTime()-starttime) + 'ms');
					
					
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