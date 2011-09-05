MessageProcessor = function(data, item) {
    $.gritter.add({
        title: data.title,
        text: data.message,
        sticky: (data.type == 'error' || data.type == 'warning'),
        class_name:data.type
    });
}

$.fn.RestartStatusTableEntry = function(data) {

    this.init = function(data) {
        var cssClass = 'default';
        if (data.state == 'FINISHED') {
            cssClass = 'success';
        } else if (data.state == 'ERROR') {
            cssClass = 'error';
        } else if (data.state == 'RESTARTING' || data.state == 'REQUESTED_RESTART') {
            cssClass = 'working';
        }
        this.appendEntry(data.hostname);
        var entry = this.appendEntry(data.state);
        entry.addClass(cssClass);
        this.appendEntry(data.statusMessage);
    }

    this.appendEntry = function(data) {
        var entry = $('<td></td>');
        entry.append(data)
        this.append(entry);
        return entry;
    }

    this.init(data);

    return this;
}

$.fn.RestartStatusTable = function(statusHandler) {
    var that = this;
    this.statusHandler = statusHandler;
    this.pollUrl = $('.wrapper .restartStatus .statusTable').attr('pollUrl');

    this.update = function(data) {
        this.clearTable();
        if (data.messages.length > 0) {
            for (var i=0; i < data.messages.length; i++) {
                var entry = $('<tr/>').RestartStatusTableEntry(data.messages[i]);
                this.append(entry);
            }
        } else {
            this.append($('<tr/>').append('<td colspan="3" class="important">No agents restarted since last application start</td>'));
        }
    }

    this.createNewEntry = function(data) {
    }

    this.clearTable = function() {
        this.find('tbody').find('tr').remove();
    }

    return this;
}

$.fn.RestartStatusInformation = function(statusHandler) {
    var that = this;
    this.statusHandler = statusHandler;
    this.restartForm = $('.wrapper .statusInformation form[name=restartAgents]');
    this.restartAction = this.restartForm.find('input[type=submit]');
    this.statusLabel = $('.wrapper .statusInformation .statusValue')
    this.running = false;

    this.init = function() {
        this.restartForm.bind('submit', function() {
            that.restartAction.attr('disabled', 'disable');
            that.statusLabel.text("Please wait....");
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
    this.detailTable = $('.wrapper .restartStatus .statusTable table').RestartStatusTable(this)


    this.stopUpdater = function() {
        if (this.updater != null) {
            this.updater.stop();
            this.updater = null;
        }
    }

    this.startUpdater = function() {
        this.stopUpdater();
        this.updater = $.PeriodicalUpdater(this.detailTable.pollUrl, {
                data: function() {
                    return { id:that.id }
                },
                maxTimeout: 2000,
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
        if (data.messages) {
            this.detailTable.update(data)
        }
        if (this.statusInfos.running) {
            if (this.updater == null) {
                this.startUpdater();
            }
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