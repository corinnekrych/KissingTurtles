
var kissingturtles = kissingturtles || {};
kissingturtles.view = kissingturtles.view || {};

kissingturtles.view.gameview = function (model, elements) {

    var that = grails.mobile.mvc.view(model, elements);

    that.currentMaze = null;

    that.model.listedItems.attach(function (data) {
        renderList();
    });

    //----------------------------------------------------------------------------------------
    //   Callback after first player create a new game. First player will play as Franklin.
    //----------------------------------------------------------------------------------------
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
               that.draw = ktDraw(document.getElementById('canvas'), conf, that.currentMaze.steps[0]);
               that.player = "franklin";
               that.gameId = data.item.id;
            }
            renderElement(data.item);
            showElement(data.item);
            $("#list-games").listview('refresh');

            if (!data.item.NOTIFIED) {
              $.mobile.changePage($("#section-show-game"));
            }
		}
    });

    //----------------------------------------------------------------------------------------
    //   Callback after joining the game
    //----------------------------------------------------------------------------------------
    that.model.updatedItem.attach(function (data, event) {
        // Display error for third fellow. Only 2 players game.
        if (data.item.errors) {
            alert("Ooops something wrong happens");
        } else if (data.item.message) {
            alert("Ooops something wrong happens" +  data.item.message);
            event.stopPropagation();
        } else {
            // In case of Emily or Franklin we go here
            var confAsString = data.item.mazeDefinition;
            var conf = JSON.parse(confAsString);
            that.currentMaze = conf;
            updateElement(data.item);
            if (!data.item.NOTIFIED) {
                // For Emily game, initialize canvas
                that.draw = ktDraw(document.getElementById('canvas'), conf, that.currentMaze.steps[0]);
                that.player = "emily";
                that.gameId = data.item.id;
                $.mobile.changePage($("#section-show-game"));
            } else if (that.player == "franklin" && that.gameId == data.item.id) {
                // For Franklin game
                that.draw({emily: that.currentMaze.steps[0].emily});
            } else {
                $("#list-games").listview('refresh');
            }
        }
    });

    //----------------------------------------------------------------------------------------
    //    Callback to display the maze after execute method
    //----------------------------------------------------------------------------------------
    that.model.executed.attach(function (data, event) {
        // only for my game
        if (that.gameId == data.item.configuration.id) {
            // refresh me if it's not myself pls
            if (!data.item.NOTIFIED || that.player != data.item.configuration.player) {
                var myGameObject = data.item;
                $.each(myGameObject.configuration.steps, function(key, value) {
                    that.draw(value);
                });
                // TO DO add winning call
                // that.draw.win(data.item.configuration.winningAnimation.x, data.item.configuration.winningAnimation.y);
            }
        }
    });

    //----------------------------------------------------------------------------------------
    //   Click on Play brings you here
    //----------------------------------------------------------------------------------------
    $('#section-list-games').live('pageinit pageshow', function (e) {
        var id = localStorage.getItem("KissingTurtles.UserId");
        if (id) {
            that.listButtonClicked.notify();
        } else {
            $.mobile.changePage($("#section-show-user"));
        }
    });

    //----------------------------------------------------------------------------------------
    //   Click on Settings:change images
    //----------------------------------------------------------------------------------------
    $('#select-emily').live('change', function(event) {
        var value = $('#select-emily').val();
        $('#emily-img').attr({src: "images/game/"+value+".png"}).refresh();
    });

    $("#select-franklin").change(function(event, ui) {
        var value = $('#select-franklin').val();
        //alert(value);
        //$('#emily-img').attr({src: "images/game/"+value+".png"}).refresh();
    });

    //----------------------------------------------------------------------------------------
    //   Click on Navigation buttons header bar same as execute
    //----------------------------------------------------------------------------------------
    $("#left").live("click tap", function(event) {
        var dslInput = "move left by 1";
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    $("#right").live("click tap", function(event) {
        var dslInput = "move right by 1";
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    $("#up").live("click tap", function(event) {
        var dslInput = "move up by 1";
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    $("#down").live("click tap", function(event) {
        var dslInput = "move down by 1";
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    $("#dsl").live("click tap", function(event) {
        if($('#dsl-text').css('display') == 'none'){
            $('#dsl-text').show('slow');
        } else {
            $('#dsl-text').hide('slow');
        }
    });


    //----------------------------------------------------------------------------------------
    //   Click on 'Reset name' in settings page brings you here.
    //   To clear your name from your browser localStorage.
    //----------------------------------------------------------------------------------------
    $('#reset-name').live("click tap", function(event) {
        localStorage.clear();
    });

    //----------------------------------------------------------------------------------------
    //   Click on Save on user page. Your name is asked only once.
    //----------------------------------------------------------------------------------------
    $("#submit-user").live("click tap", function(event) {
        var name = $('#input-user-name').val();
        localStorage.setItem("KissingTurtles.UserId", name);
        $.mobile.changePage($("#section-list-games"));
    });

    //----------------------------------------------------------------------------------------
    //   Click on Execute my DSL script brings you here
    //----------------------------------------------------------------------------------------
    $("#submit-game").live("click tap", function(event) {
        var dslInput = $('#input-move-name').val();
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    //----------------------------------------------------------------------------------------
    //   Click on an element of the list to join the game. Second player is Emily.
    //----------------------------------------------------------------------------------------
    $('a[id ^= "game"]').live('click tap', function (event) {
        var gameId = $(event.currentTarget).attr('data-game-id');
        if(gameId) {
            var obj = {user2: localStorage.getItem("KissingTurtles.UserId"), gameId: gameId};
            var newElement = {
                game: JSON.stringify(obj)
            };
            that.updateButtonClicked.notify(newElement, event);
        }

    });

    //----------------------------------------------------------------------------------------
    //   Click on 'Create your own game' brings you here
    //----------------------------------------------------------------------------------------
    $("#create-game").live('click tap', function (event) {
        var obj = {user1: localStorage.getItem("KissingTurtles.UserId")};
        var newElement = {
                game: JSON.stringify(obj)
        };
        that.createButtonClicked.notify(newElement, event);
    });

    //----------------------------------------------------------------------------------------
    //   Rendering methods
    //----------------------------------------------------------------------------------------
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
        // filter, display only game created by other
        if (element.user1 != localStorage.getItem("KissingTurtles.UserId") && element.user2 == null) {
          $('#game' + element.id + '-in-list').parents('li').replaceWith(createListItem(element));
        } else {
          $('#game' + element.id + '-in-list').parents('li').remove();
        }
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