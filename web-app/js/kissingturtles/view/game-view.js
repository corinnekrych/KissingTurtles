


var kissingturtles = kissingturtles || {};
kissingturtles.view = kissingturtles.view || {};

kissingturtles.view.gameview = function (model, elements) {

    var that = grails.mobile.mvc.view(model, elements);
    
    // Register events

    
    that.model.listedDependentItems.attach(function (data) {
        if (data.relationType === 'many-to-one') {
            renderDependentList(data.dependentName, data.items);
        }
        if (data.relationType === 'one-to-many') {
            renderMultiChoiceDependentList(data.dependentName, data.items);
        }
    });
    
    that.model.listedItems.attach(function (data) {
        renderList();
    });

    that.model.createdItem.attach(function (data, event) {
        if (data.item.errors) {
            $.each(data.item.errors, function(index, error) {
            $('#input-game-' + error.field).validationEngine('showPrompt',error.message, 'fail');
            });
            event.stopPropagation();
            event.preventDefault();
        } else if (data.item.message) {
            showGeneralMessage(data, event);
        } else {
            renderElement(data.item);
            $('#list-games').listview('refresh');
    	    
		}
    });

    that.model.updatedItem.attach(function (data, event) {
        if (data.item.errors) {
            $.each(data.item.errors, function(index, error) {
                $('#input-game-' + error.field).validationEngine('showPrompt',error.message, 'fail');
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
            $('#game' + data.item.id + '-in-list').parents('li').remove();
            
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
        var id = sessionStorage.getItem("UserId");
        if (id) {
            that.listButtonClicked.notify();
        } else {
            $.mobile.changePage($("#section-show-user"));
        }
    });

    that.elements.save.live("click tap", function (event) {
        $("#form-update-game").validationEngine('hide');
        if($("#form-update-game").validationEngine('validate')) {
            var obj = grails.mobile.helper.toObject($("#form-update-game").find("input, select"));
            var newElement = {
                game: JSON.stringify(obj)
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
        that.deleteButtonClicked.notify({ id: $('#input-game-id').val() }, event);
    });

    // Detect online/offline from browser
    addEventListener('offline', function(e) {
        that.offlineEvent.notify();
    });

    addEventListener('online', function(e) {
        that.onlineEvent.notify();
    });

    that.elements.add.live('pageshow', function (e) {
        $('#form-update-game').validationEngine('hide');
        $("#form-update-game").validationEngine();

        //that.editButtonClicked.notify();

        var id = sessionStorage.getItem("showgameId");

        if (id) {
            sessionStorage.removeItem("showgameId");
            showElement(id);
        } else {
            createElement();
        }
    });

    var createElement = function () {
        resetForm("form-update-game");
        
        $("#delete-game").hide();
    };

    var showElement = function (id) {
        resetForm("form-update-game");
        var element = that.model.items[id];
 
        $('select[data-gorm-relation="many-to-one"][name="user1"]').val(element.user1.id);
        $('select[data-gorm-relation="many-to-one"][name="user1"]').selectmenu('refresh');
 
        $('select[data-gorm-relation="many-to-one"][name="user2"]').val(element.user2.id);
        $('select[data-gorm-relation="many-to-one"][name="user2"]').selectmenu('refresh');
 
 
        $.each(element, function (name, value) {
            var input = $('#input-game-' + name);
            if (input.attr('type') == 'date') {
                input.scroller('setDate', (value === "") ? "" : new Date(value), true);
            } else {
                input.val(value);
            }
        });
        
        $("#delete-game").show();
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
        
        $('#list-games').empty();
        var key, items = model.getItems();
        for (key in items) {
            renderElement(items[key]);
        }
        $('#list-games').listview('refresh');
        
    };


    
    var refreshSelectDropDown = function (select, newOptions) {
        var options = null;
        if(select.prop) {
            options = select.prop('options');
        }
        else {
            options = select.attr('options');
        }
        $('option', select).remove();

        $.each(newOptions, function(val, text) {
            options[options.length] = new Option(text, val);
        });
        select.val(options[0]);
        select.selectmenu('refresh');
    };

     var renderDependentList = function (dependentName, items) {

        var manyToOneSelectForDependent = $('select[data-gorm-relation="many-to-one"][name=' + dependentName + ']');
        var options = {};
        $.each(items, function() {
            var key = this.id;
            var value = this[Object.keys(this)[2]];;
            options[key] = value;
            });

        refreshSelectDropDown(manyToOneSelectForDependent, options);
    };


    var refreshMultiChoices = function (oneToMany, dependentName, newOptions) {
        oneToMany.empty();
        $.each(newOptions, function(key, val) {
            oneToMany.append($('<input type="checkbox" data-gorm-relation="one-to-many" name="'+ dependentName +'" id="checkbox-'+ dependentName +'-' + key + '"/><label for="checkbox-'+ dependentName +'-'+key+'">'+val+'</label>'));
        });
    };

    var renderMultiChoiceDependentList = function (dependentName, items) {
        var oneToMany = $('#multichoice-' + dependentName);
        var options = {};
        $.each(items, function() {
            var key = this.id;
            var value = this[Object.keys(this)[2]];
            options[key] = value;
        });

        refreshMultiChoices(oneToMany, dependentName, options);
    };


    var renderElement = function (element) {
        if (element.offlineAction !== 'DELETED') {
            var a = $('<a>').attr({ href: '#section-show-game'});
            a.attr({id : 'game' + element.id + '-in-list'});
            a.attr({onClick : 'sessionStorage.showgameId=' + element.id});
            a.attr({'data-transition': 'fade' });
            a.text(getText(element));
            if (element.offlineStatus === "NOT-SYNC") {
                $("#list-games").append($('<li data-theme="e">').append(a));
            } else {
                $("#list-games").append($('<li>').append(a));
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