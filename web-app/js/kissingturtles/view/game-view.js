


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
            var confAsString = data.item.mazeDefinition;
            sessionStorage.setItem("showgameId", data.item.id);
            var conf = JSON.parse(confAsString);
            ktMaze(document.getElementById('canvas'), conf['configuration'], function () {
                console.log('done');
            });

//            that.randomEmilyX=Math.floor(Math.random()*15);
//            that.randomEmilyY=Math.floor(Math.random()*15);
//            ktMaze(document.getElementById('canvas'), {
//                images: {
//                    franklin: 'turtle.png',
//                    emily: 'turtle.png',
//                    tree1: 'tree.png'
//                },
//                //winningAnimation: { x: that.randomEmilyX, y: that.randomEmilyY },
//                steps: [{
//                    franklin: { x: 0, y: 0, direction: '+x' },
//                    emily: { x: that.randomEmilyX, y: that.randomEmilyY, direction: '-y' },
//                    tree1: { x: 14, y: 14 }
//                },
//                {
//                    franklin: { x: 0, y: 0, direction: '+x' },
//                    emily: { x: that.randomEmilyX, y: that.randomEmilyY, direction: '-y' },
//                    tree1: { x: 14, y: 14 }
//                }],
//                grid: 15,
//                stepDuration: 1000
//            }, function () {
//                console.log('done');
//            })
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
        var id = sessionStorage.getItem("KissingTurtles.UserId");
        if (id) {
            that.listButtonClicked.notify();
        } else {
            $.mobile.changePage($("#section-show-user"));
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

    that.elements.execute.live("click tap", function(event) {
        var dslInput = $('#input-move-name').val();
        var gameId = sessionStorage.getItem("showgameId");
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId});
    });

    //----------------------------------------------------------------------------------------
    //    DISPLAY MAZE AFTER EXECUTE
    //----------------------------------------------------------------------------------------
    that.refreshMazeWithMove = function(data) {


        ktMaze(document.getElementById('canvas'), {
            images: {
                franklin: 'turtle.png',
                emily: 'turtle.png',
                tree1: 'tree.png'
            },
            //winningAnimation: { x: that.randomEmilyX, y: that.randomEmilyY },
            steps: data.configuration.steps,
            grid: 15,
            stepDuration: 1000
        }, function () {
            console.log('done');
        })
    };

    that.elements.add.live('pageshow', function (event) {
        $('#form-update-game').validationEngine('hide');
        $("#form-update-game").validationEngine();

        var id = sessionStorage.getItem("showgameId");

        if (id) {
            //sessionStorage.removeItem("showgameId");
            showElement(id);
        } else {
            var obj = {user1: sessionStorage.getItem("KissingTurtles.UserId")};
            var newElement = {
                game: JSON.stringify(obj)
            };
            that.createButtonClicked.notify(newElement, event);
            showElement();
        }
    });

    var createElement = function () {

    };

    var showElement = function (id) {
        resetForm("form-update-game");
        var element = that.model.items[id];
 
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
        div.find('input:text, input:hidden, input[type="number"], input:file, input:password, textarea').val('');
        div.find('input:radio, input:checkbox').removeAttr('checked').removeAttr('selected');//.checkboxradio('refresh');
    };
    

    var renderList = function () {
        
        $('#list-games').empty();
        var key, items = model.getItems();
        for (key in items) {
            var game = items[key];
            if (game.user1.id != sessionStorage.getItem("KissingTurtles.UserId")) {
                renderElement(items[key]);
            }
        }
        $('#list-games').listview('refresh');
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
        return 'Play with ' + data.user1.name;
    };

    return that;
};