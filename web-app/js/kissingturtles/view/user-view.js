


var kissingturtles = kissingturtles || {};
kissingturtles.view = kissingturtles.view || {};

kissingturtles.view.userview = function (model, elements) {

    var that = grails.mobile.mvc.view(model, elements);
    
    // Register events

    
    that.model.listedItems.attach(function (data) {
        renderList();
    });

    that.model.createdItem.attach(function (data, event) {
        if (data.item.errors) {
            $.each(data.item.errors, function(index, error) {
            $('#input-user-' + error.field).validationEngine('showPrompt',error.message, 'fail');
            });
            event.stopPropagation();
            event.preventDefault();
        } else if (data.item.message) {
            showGeneralMessage(data, event);
        } else {
            event.stopPropagation();
            event.preventDefault();
            sessionStorage.setItem("UserId", data.item.id);
            sessionStorage.setItem("UserName", data.item.name);
            $.mobile.changePage($("#section-list-games"));
        }
    });

    that.model.updatedItem.attach(function (data, event) {
        if (data.item.errors) {
            $.each(data.item.errors, function(index, error) {
                $('#input-user-' + error.field).validationEngine('showPrompt',error.message, 'fail');
            });
            event.stopPropagation();
            event.preventDefault();
        } else if (data.item.message) {
            showGeneralMessage(data, event);
        } else {
            renderList();
        }
    });

    that.model.deletedItem.attach(function (data, event) {
        if (data.item.message) {
            showGeneralMessage(data, event);
        } else {
            $('#user' + data.item.id + '-in-list').parents('li').remove();
            
        }
    });

    var showGeneralMessage = function(data, event) {
        $.mobile.showPageLoadingMsg( $.mobile.pageLoadErrorMessageTheme, data.item.message, true );
        setTimeout( $.mobile.hidePageLoadingMsg, 3000 );
        event.stopPropagation();
        event.preventDefault();
    };

    // user interface actions
    
    that.elements.list.live('pageinit', function (e) {
        that.listButtonClicked.notify();
    });

    that.elements.save.live("click tap", function (event) {
        $("#form-update-user").validationEngine('hide');
        if($("#form-update-user").validationEngine('validate')) {
            var obj = grails.mobile.helper.toObject($("#form-update-user").find("input, select"));
            var newElement = {
                user: JSON.stringify(obj)
            };
            if (obj.id === "") {
                that.createButtonClicked.notify(newElement, event);
            } else {
                that.updateButtonClicked.notify(newElement, event);
            }
        } else {
            event.stopPropagation();
            event.preventDefault();
        }
    });

    that.elements.remove.live("click tap", function (event) {
        that.deleteButtonClicked.notify({ id: $('#input-user-id').val() }, event);
    });

    // Detect online/offline from browser
    addEventListener('offline', function(e) {
        that.offlineEvent.notify();
    });

    addEventListener('online', function(e) {
        that.onlineEvent.notify();
    });

    that.elements.add.live('pageshow', function (e) {
        $('#form-update-user').validationEngine('hide');
        $("#form-update-user").validationEngine();

        that.editButtonClicked.notify();

        var id = sessionStorage.getItem("UserId");

        if (id) {
            showElement(id);
        } else {
            createElement();
        }
    });

    var createElement = function () {
        resetForm("form-update-user");
        
        $("#delete-user").hide();
    };

    var showElement = function (id) {
        resetForm("form-update-user");
        var element = that.model.items[id];
 
 
        $.each(element, function (name, value) {
            var input = $('#input-user-' + name);
            if (input.attr('type') == 'date') {
                input.scroller('setDate', (value === "") ? "" : new Date(value), true);
            } else {
                input.val(value);
            }
        });
        
        $("#delete-user").show();
    };

    var resetForm = function (form) {
        $('input[type="date"]').each(function() {
            $(this).scroller('destroy');
            $(this).scroller({
                preset: 'date',
                theme: 'default',
                display: 'modal',
                mode: 'scroller',
                dateOrder: 'mmD ddyy'
            });
        });
        var div = $("#" + form);
        div.find('input:text, input:hidden, input[type="number"], input:file, input:password').val('');
        div.find('input:radio, input:checkbox').removeAttr('checked').removeAttr('selected');//.checkboxradio('refresh');
    };
    

    var renderList = function () {
        
        $('#list-users').empty();
        var key, items = model.getItems();
        for (key in items) {
            renderElement(items[key]);
        }
        $('#list-users').listview('refresh');
        
    };


    

    var renderElement = function (element) {
        if (element.offlineAction !== 'DELETED') {
            var a = $('<a>').attr({ href: '#section-show-user'});
            a.attr({id : 'user' + element.id + '-in-list'});
            a.attr({onClick : 'sessionStorage.showuserId=' + element.id});
            a.attr({'data-transition': 'fade' });
            a.text(getText(element));
            if (element.offlineStatus === "NOT-SYNC") {
                $("#list-users").append($('<li data-theme="e">').append(a));
            } else {
                $("#list-users").append($('<li>').append(a));
            }
            
        }
    };

    var getText = function (data) {
        var textDisplay = '';
        $.each(data, function (name, value) {
            if (name !== 'class' && name !== 'id' && name !== 'offlineAction' && name !== 'offlineStatus' && name !== 'status' && name !== 'version') {
                if (typeof value !== 'object') {   // do not display relation in list view
                    textDisplay += value + " - ";
                }
            }
        });
        return textDisplay.substring(0, textDisplay.length - 2);
    };

    return that;
};