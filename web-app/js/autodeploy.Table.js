AbstractTable = function(){ // abstract class
	this.data('this',this);
	
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
}


$.fn.DetailsMessagesTable = function(queueList){
	var that = this;
	this.entries = {};
	this.idString = "id";
	
	AbstractTable.apply(this,arguments);  // "ableitung"
	
	this.getNewEntry = function(data,x){ return $('<li></li>').EntryMessageRow(queueList).create(data) }
	
	/*
	 * @override
	 * */
	this.update = function(data){
		var entry = null;
		for(var x=0;x<data.length;x++){
			entry = this.getEntry(data[x][this.idString]);
			if (entry == null)
				this.prependEntry(this.getNewEntry(data[x]));
			else{
				this.empty(); // if a message id comes again, this means, that it is a redeploy and all messages should be removed
				this.prependEntry(this.getNewEntry(data[x]));
			}
		}
	}
	
	this.container = this;
	return this;
}

$.fn.AppRevisonBody = function(queueList){
	var that = this;
	this.entries = {};
	this.idString = "id";
	
	AbstractTable.apply(this,arguments);  // "ableitung"
	
	this.getNewEntry = function(data,x){ return $('<tr></tr>').DetailsAppRow(queueList).create(data) }
	
	this.searchAction = function(){
		var $this = $(this);
		var search = {};
		
		that.find('tr:first-child th input').each(function(index){
			search[index+1] = $(this).val();
		})
		
		var trs = $this.closest('table').find('tr:not(:first-child)');
		trs.each(function(){
			var text = '';
			var show = true;
			for (x in search){
				text = $(this).find('td:nth-child('+x+')').text();
				if (search[x] != "" && text.indexOf(search[x]) < 0) show = false;
			}

			if (show) $(this).show();
			else $(this).hide();			
		})
	}
		
	this.append('<tr><th>Name</th><th>Pillar</th><th>Revision</th><th>Type</th><th>Context</th><th></th></tr>');
	
	this.find('tr th:not(:nth-child(6))').each(function(){
		 $(this).append($('<input/>').bind('keyup',that,that.searchAction));
	});
	this.container = this;
	return this;
}

$.fn.QueueEntryTable = function(queueList){
	var that = this;
	this.entries = {};
	this.idString = "entryId";
	
	AbstractTable.apply(this,arguments);  // "ableitung"
	
	this.getNewEntry = function(data){ return $('<tr></tr>').QueueEntryRow(queueList).create(data) }

	this.setActive = function(id){
		for (x in this.entries){
		  if (this.entries[x].id != id) this.entries[x].removeClass('active');
		  else this.entries[x].addClass('active');
		}
	}
	
	this.getActive = function(){
		for (x in this.entries){
		  if (this.entries[x].hasClass('active'))
			  return this.entries[x];
		}
		return 0;
	}
	
	this.container = $('<tbody></tbody>');
	this.append(this.container);
	return this;
};

$.fn.QueueEntryDetailsTable = function(queueList){
	var that = this;
	this.entries = {};
	this.idString = "hostclassId";

	AbstractTable.apply(this,arguments); // ableitung
	
	this.appView = ($.cookie('autodeploy_view')=='appView');
	
	this.getNewEntry = function(data){ 
		if (!this.appView){
			return $('<tbody class="hostView"></tbody>').QueueEntryDetailsHostRow(queueList).create(data);
		}
		else{
			return $('<tbody class="appView"></tbody>').QueueEntryDetailsAppRow(queueList).create(data);
		}
	}
	
	this.container = this;
	return this;
};
