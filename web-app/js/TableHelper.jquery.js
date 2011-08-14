(function($){
	// TableHelper methods
	var methods = {
	appendTr : function( data ) { 
		var tr = $("<tr id='" + data[0] +"'></tr>");
		for(var x=1;x<data.length;x++){
			if (typeof data[x] == "object")
				tr.append("<td colspan='" + data[x][0] + "'>" + data[x][1] + "</td>");
			else
				tr.append("<td>" + data[x] + "</td>");
		}
		this.append(tr);
	},
	appendTbody: function(data ) {  
		var tbody = $("<tbody id='" + data[0][0] +"'></tbody>");
		for(var x=0;x<data.length;x++){
			tbody.TableHelper('appendTr',data[x]);
		}
		this.append(tbody);
	}
  };

  $.fn.TableHelper = function( method ) {
    
    // Method calling logic
    if ( methods[method] ) {
      return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
    } else if ( typeof method === 'object' || ! method ) {
      return methods.init.apply( this, arguments );
    } else {
      $.error( 'Method ' +  method + ' does not exist on jQuery.TableHelper' );
    }    
  
  };
})( jQuery );
	