AbstractTable = function() { // abstract class

    this.appendEntry = function(entry) {
        this.entries[entry.id] = entry;
        this.container.append(entry.addClass('appended'));
        return entry;
    }

    this.prependEntry = function(entry) {
        this.entries[entry.id] = entry;
        this.container.prepend(entry.addClass('appended'));
        return entry;
    }

    this.getEntry = function(id) {
        return (typeof this.entries[id] == 'object') ? this.entries[id] : null;
    }

    this.create = function(data) {
        this.data = data;
        this.container.children('.appended').remove();
        for (var x = 0; x < data.length; x++) {
            this.appendEntry(this.getNewEntry(data[x]));
        }
    }

    this.update = function(data) {
        var entry = null;
        for (var x = 0; x < data.length; x++) {
            entry = this.getEntry(data[x][this.idString]);
            if (entry == null)
                this.prependEntry(this.getNewEntry(data[x]));
            else
                entry.update(data[x]);
        }
    }
}


MessageProcessor = function(data, item) {
    $.gritter.add({
        title: data.title,
        text: data.message,
        sticky: (data.type == 'error' || data.type == 'warning'),
        class_name:data.type
    });
}

$.fn.RestartStatusTable = function(statusHandler) {
    var that = this;
    this.statusHandler = statusHandler;
    this.pollUrl = $('.wrapper .restartStatus .statusTable').attr('pollUrl');
    return this;
}

$.fn.RestartStatusInformation = function(statusHandler) {
    this.statusHandler = statusHandler;
    this.restartForm = $('.wrapper .statusInformation form[name=restartAgents]');
    this.restartAction = this.restartForm.find('input[type=submit]');
    this.statusLabel = $('.wrapper .statusInformation .statusValue')
    this.running = false;

    this.init = function() {
        this.restartForm.bind('submit', function() {
            var that = this;
            $.ajax({url:$(this).attr('action'),
                data: { id: $('#selectEnvId').val() },
                dataType: 'json',
                ifModified: true,
                type: 'POST',
                success:function(result) {
                    if (result.info) {
                        MessageProcessor(result.info);
                    }
                    if (result.data) {
                        that.statusHandler.update(result.data);
                    }
                }});
            return false;
        })
    }

    this.update = function(data) {
        this.running = false;
        this.id = data.queueId;
        this.restartAction.removeAttr('disabled');

        if (data.status == 'IN_PROGRESS') {
            this.restartAction.attr('disabled', 'disable');
            this.running = true;
        }

        this.statusLabel.text(data.status);
    }

    this.init();
    return this;
}

$.fn.RestartStatusHandler = function() {
    var that = this;
    this.updater = null;
    this.pollurl = this.attr('pollUrl');
    this.statusInfos = $('.wrapper .statusInformation').RestartStatusInformation(this);
    this.detailTable =  $('.wrapper .restartStatus .statusTable table').RestartStatusTable(this)


    this.stopUpdater = function() {
        if (this.updater != null) {
            this.updater.stop();
        }
    }

    this.startUpdater = function() {
        this.stopUpdater();
        this.updater = $.PeriodicalUpdater(this.detailTable.pollUrl, {
                data: function() {
                    return { id:that.id }
                },
                error:function() {

                },
                type:'json'
            },
            function(data) {
                that.update(data);
            });
    }

    this.update = function(data) {
        this.id = data.queueId;
        if (data.status) {
            this.statusInfos.update(data);
        }
        if (this.statusInfos.running) {
            this.startUpdater();
        } else {
            this.stopUpdater();
        }
    }

    return this;
}

$(function() {
    var statusHandler = $('.wrapper .restartStatus').RestartStatusHandler();
    $('.wrapper .chooseEnv form[name=restartEnv] select').bind('change', function() {
        $(this).closest('form').submit()
    })
    $('.wrapper .chooseEnv form[name=restartEnv]').bind('submit', function() {
        $.ajax({url:$(this).attr('action'),
            data:{
                id:$(this).find('select').val()
            },
            success:function(data) {
                statusHandler.update(data);
            }
        });

        return false;
    })

});