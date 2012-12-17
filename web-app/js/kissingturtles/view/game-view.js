


var kissingturtles = kissingturtles || {};
kissingturtles.view = kissingturtles.view || {};

kissingturtles.view.gameview = function (model, elements) {

    var that = grails.mobile.mvc.view(model, elements);

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
            sessionStorage.setItem("showgameId", data.item.id);
            var conf = JSON.parse(confAsString);
            ktMaze(document.getElementById('canvas'), conf, function () {
                console.log('done');
            });
		}
    });

    // user interface actions
    that.elements.list.live('pageinit pageshow', function (e) {
        //var id = sessionStorage.getItem("KissingTurtles.UserId");
        //if (id) {
            that.listButtonClicked.notify();
        //} else {
        //    $.mobile.changePage($("#section-show-user"));
        //}
    });

    $('#single-player').live("click tap", function(event) {
        $.mobile.changePage($("#section-show-game"));
    });

    $('#save-settings').live("click tap", function(event) {
        //$.mobile.changePage($("#section-show-game"));
        alert("boo!");
    });

    $("#submit-game").live("click tap", function(event) {
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
            winningAnimation: data.configuration.winningAnimation,
            steps: data.configuration.steps,
            grid: 15,
            stepDuration: 1000
        }, function () {
            console.log('done');
        })
    };

    $('#section-show-game').live('pageshow', function (event) {
        $('#form-update-game').validationEngine('hide');
        $("#form-update-game").validationEngine();

        //var id = sessionStorage.getItem("showgameId");

        //if (id) {
            //var game = that.model.items[id]
            //game.user2 = { id: sessionStorage.getItem("KissingTurtles.UserId") };
            //var newElement = {
            //    game: JSON.stringify(game)
            //};
            //that.updateButtonClicked.notify(newElement, event);

            //showElement(id);
        //} else {
            var obj = {user1: sessionStorage.getItem("KissingTurtles.UserId")};
            var newElement = {
                game: JSON.stringify(obj)
            };
            that.createButtonClicked.notify(newElement, event);
            showElement();
        //}
    });

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
            //if (game.user1.id != sessionStorage.getItem("KissingTurtles.UserId")) {
                //if (!game.user2) {
                    renderElement(items[key]);
                //}
            //}
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
        var date = new Date();
        return 'Played'; //+ data.user1.name;
    };

    return that;
};