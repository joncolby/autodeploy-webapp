

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
				this.entries = {};
				this.empty(); // if a message id comes again, this means, that it is a redeploy and all messages should be removed
				this.prependEntry(this.getNewEntry(data[x]));
			}
		}
	}
	
	this.container = this;
	return this;
}

$.fn.AppRevisonTable = function(queueList){
	var that = this;
	this.entries = {};
	this.idString = "id";
	
	AbstractTable.apply(this,arguments);  // "ableitung"
	
	this.getNewEntry = function(data,x){ return $('<tr></tr>').DetailsAppRow(queueList).create(data) }
		
	
	this.create = function(data){
		this.data = data;
		this.empty();
		for(var x=0;x<data.length;x++){
			this.appendEntry(this.getNewEntry(data[x]));
		}
		this.append('<thead class="appView"><tr><th>name</th><th>pillar</th><th>revision</th><th>type</th><th>context</th><th></th></tr></thead>');

		this.append(this.container);
		this.find('tr th:not(:nth-child(6))').each(function(){
			var str = $(this).text();
			 $(this).empty().append($.SearchField(str,that));
		});
	}
	
	this.container = $('<tbody/>');
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
