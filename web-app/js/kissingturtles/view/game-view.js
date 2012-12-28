
var kissingturtles = kissingturtles || {};
kissingturtles.view = kissingturtles.view || {};

kissingturtles.view.gameview = function (model, elements) {

    var that = grails.mobile.mvc.view(model, elements);

    that.currentMaze = null;

    that.model.listedItems.attach(function (data) {
        renderList();
    });

    that.model.createdItem.attach(function (data, event) {
        if (data.item.errors) {
            alert("Ooops something wrong happens");
        } else if (data.item.message) {
            alert("Ooops something wrong happens");
        } else {
            var confAsString = data.item.mazeDefinition;
            var conf = JSON.parse(confAsString);
            if (!data.item.NOTIFIED) {
               that.currentMaze = conf;
            }
            if (that.currentMaze != null) {
                ktMaze(document.getElementById('canvas'), that.currentMaze, function () {
                    console.log('done');
                });
            }
            renderElement(data.item);
            showElement(data.item);
            $("#list-games").listview('refresh');
            if (!data.item.NOTIFIED) {
              $.mobile.changePage($("#section-show-game"));
            }
		}
    });


    that.model.updatedItem.attach(function (data, event) {
        if (data.item.errors) {
            alert("Ooops something wrong happens");
        } else if (data.item.message) {
            alert("Ooops something wrong happens");
        } else {
            var confAsString = data.item.mazeDefinition;
            //localStorage.setItem("showgameId", data.item.id);
            var conf = JSON.parse(confAsString);
//            if (!data.item.NOTIFIED) {
                that.currentMaze = conf;
//            }
            if (that.currentMaze != null) {
                ktMaze(document.getElementById('canvas'), that.currentMaze, function () {
                    console.log('done');
                });
            }
            updateElement(data.item);
            $("#list-games").listview('refresh');
        }
    });

    // user interface actions
    $('#section-list-games').live('pageinit pageshow', function (e) {
        var id = localStorage.getItem("KissingTurtles.UserId");
        if (id) {
            that.listButtonClicked.notify();
        } else {
            $.mobile.changePage($("#section-show-user"));
        }
    });

    $('#single-player').live("click tap", function(event) {
        $.mobile.changePage($("#section-show-game"));
    });

    $('#save-settings').live("click tap", function(event) {
        //$.mobile.changePage($("#section-show-game"));
        alert("boo!");
    });

    $("#submit-user").live("click tap", function(event) {
        var name = $('#input-user-name').val();
        localStorage.setItem("KissingTurtles.UserId", name);
        $.mobile.changePage($("#section-list-games"));
    });

    $("#submit-game").live("click tap", function(event) {
        var dslInput = $('#input-move-name').val();
        var gameId = $("#input-game-id").val();
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    $('a[id ^= "game"]').live('click tap', function (event) {
        var gameId = $(event.currentTarget).attr('data-game-id');
        if(gameId) {
            var obj = {user2: localStorage.getItem("KissingTurtles.UserId"), gameId: gameId};
            var newElement = {
                game: JSON.stringify(obj)
            };
            that.updateButtonClicked.notify(newElement, event);
        }
        showElement();
        $.mobile.changePage($("#section-show-game"));
    });


    $("#create-game").live('click tap', function (event) {
        var obj = {user1: localStorage.getItem("KissingTurtles.UserId")};
        var newElement = {
                game: JSON.stringify(obj)
        };
        that.createButtonClicked.notify(newElement, event);
        //showElement();

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
            winningAnimation: data.configuration.winningAnimation,
            steps: data.configuration.steps,
            grid: 15,
            stepDuration: 1000
        }, function () {
            console.log('done');
        })
    };

    var showElement = function (element) {
        resetForm("form-update-game");
        if (element) {
            $.each(element, function(name, value) {
                var input = $("#input-game-" + name);
                input.val(value);

            });
        }
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
            renderElement(items[key]);
        }
        $('#list-games').listview('refresh');
    };


    
    var renderElement = function (element) {
        if (element.offlineAction !== 'DELETED') {
            var a = $('<a>').attr({ href: '#section-show-game'});
            a.attr({id : 'game' + element.id + '-in-list'});
            a.attr({'data-game-id' : element.id});
            a.attr({'data-transition': 'fade' });
            a.text(getText(element));
            // filter, display only game created by other
            if (element.user1 != localStorage.getItem("KissingTurtles.UserId") && element.user2 == null) {
                if (element.offlineStatus === "NOT-SYNC") {
                    $("#list-games").append($('<li data-theme="e">').append(a));
                } else {
                    $("#list-games").append($('<li>').append(a));
                }
            }
        }
    };

    var updateElement = function (element) {
        $('#game' + element.id + '-in-list').parents('li').replaceWith(createListItem(element));
    };

    var createListItem = function (element) {
        var li, a = $('<a>');
        a.attr({
            href: '#playing_game',
            'data-game-id': element.id,
            id : 'game' + element.id + '-in-list',
            'data-transition': 'fade'
        });
        a.text(getText(element));
        if (element.offlineStatus === 'NOT-SYNC') {
            li =  $('<li>').attr({'data-theme': 'e'});
            li.append(a);
        } else {
            li = $('<li>').append(a);
        }
        return li;
    };

    var getText = function (data) {
        return data.user1 + " playing with " + data.user2;
    };

    return that;
};