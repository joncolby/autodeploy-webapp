MessageProcessor = function(data,item){
	$.gritter.add({
		title: data.title,
		text: data.message,
		sticky: (data.type == 'error' || data.type == 'warning'),
		class_name:data.type
	});
}

$.fn.StatusTable = function(id) {
    var that = this;
    this.id = id;
    this.updater = null;
    this.pollurl = this.attr('pollUrl');

    this.startUpdater = function(){
        if (this.updater != null) {
            this.updater.stop();
        }
        this.updater = $.PeriodicalUpdater(this.pollurl, {
              data: function() {
                  return { id:that.id }
                  },
                  error:function(){

                  },
              type:'json'
           },
           function(data){
               alert(data);
        });
    }

    this.setId = function(id) {
        that.queueId = id;
    }

    this.hello = function(text) {
        alert(text);
    }

    this.init = function(){
        //alert(that.id);
        //that.startUpdater();
    }

    this.init();

}

$(function(){
    $('.wrapper .statusTable').StatusTable();
	$('.wrapper .chooseEnv form[name=restartEnv] select').bind('change',function(){ $(this).closest('form').submit()})
	$('.wrapper .chooseEnv form[name=restartEnv]').bind('submit',function(){
		var that = this;
		$.ajax({url:$(this).attr('action'),
			data:{
				id:$(this).find('select').val()
			},
			success:function(data) {
                console.log(this);
                console.log(that);
                console.log($('#selectEnvId').val());
                console.log($(that).find('select').val());
                $('.wrapper .statusTable').hello('Test');

				MessageProcessor(data);
			}
		});

		$(this).find('select').val(0);
		return false;
	})

});