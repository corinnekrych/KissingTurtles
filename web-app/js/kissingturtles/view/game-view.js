var kissingturtles = kissingturtles || {};
kissingturtles.view = kissingturtles.view || {};

kissingturtles.view.gameview = function (model, elements) {

    var that = grails.mobile.mvc.view(model, elements);
    that.executeButtonClicked = grails.mobile.event();
    that.answerButtonClicked = grails.mobile.event();

    that.currentMaze = null;
    that.init = function () {
        that.listButtonClicked.notify();
    };

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
                // take local config to customize Franklin's picture
               var franklinImageName = localStorage.getItem("kissingturtles.settings.franklin");
                if (franklinImageName) {
                    franklinImageName += '.png';
                    conf.images['franklin'] = franklinImageName;
                }

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
                // take local config to customize Franklin's picture
                var franklinImageName = localStorage.getItem("kissingturtles.settings.franklin");
                if (franklinImageName) {
                    franklinImageName += '.png';
                    conf.images['franklin'] = franklinImageName;
                }
                var emilyImageName = localStorage.getItem("kissingturtles.settings.emily");
                if (emilyImageName) {
                    emilyImageName += '.png';
                    conf.images['emily'] = emilyImageName;
                }
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
                $.each(myGameObject.configuration.asks, function(key, value) {
                    var text = "";
                    $.each(value, function(innerKey, innerValue) {
                        if (innerKey == "_question") {
                            text += "\nquestion = " + innerValue;
                        } else {
                            text += "\nresponse = " + innerValue;
                        }
                    });
                    console.log(text);
                    $('#interaction').text(text);
                });
                $.each(myGameObject.configuration.steps, function(key, value) {
                    that.draw(value, function () {
	                var win = myGameObject.configuration.winningAnimation;
	                if (win) {
	                    that.draw.win(win.x, win.y);
	                }
                    });
                });
            }
        }
    });


    //----------------------------------------------------------------------------------------
    //    Callback to display question asked to partner
    //----------------------------------------------------------------------------------------
    that.model.asked.attach(function (data, event) {
        $('#ask').text(data.item.question);
    });

    //----------------------------------------------------------------------------------------
    //   Click on Execute my DSL script brings you here
    //----------------------------------------------------------------------------------------
    $("#answer").on("vclick", function(event) {
        var answer = $('#response').val();
        var gameId = that.gameId;
        that.answerButtonClicked.notify({title: "KissingTurtles", content: answer, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    //----------------------------------------------------------------------------------------
    //   Click on Play brings you here
    //----------------------------------------------------------------------------------------
    $('#play').on('vclick', function (e) {
        var id = localStorage.getItem("KissingTurtles.UserId");
        if (id) {
            $.mobile.changePage($("#section-list-games"));
            that.listButtonClicked.notify();
        } else {
            $.mobile.changePage($("#section-show-user"));
        }
    });

    //----------------------------------------------------------------------------------------
    //   Click on Settings:change images
    //----------------------------------------------------------------------------------------
    $('#select-emily').on('change', function(event) {
        var value = $('#select-emily').val();
        localStorage.setItem("kissingturtles.settings.emily", value);
        $('#emily-img').attr({src: "images/game/" + value + ".png"});
    });

    $("#select-franklin").on('change', function(event) {
        var value = $('#select-franklin').val();
        localStorage.setItem("kissingturtles.settings.franklin", value);
        $('#franklin-img').attr({src: "images/game/" + value + ".png"});
    });


    //----------------------------------------------------------------------------------------
    //   Click on Settings:'Reset settings' brings you here.
    //   To clear your name from your browser localStorage.
    //----------------------------------------------------------------------------------------
    $('#reset-settings').on("click tap", function(event) {
        localStorage.clear();
        // defaults values for Franklin and Emily
        localStorage.setItem("kissingturtles.settings.emily", "pig");
        $('#emily-img').attr({src: "images/game/pig.png"});
        //$('#select-emily').val('Emily is a pig');
        localStorage.setItem("kissingturtles.settings.franklin", "turtle");
        $('#franklin-img').attr({src: "images/game/turtle.png"});
        //$('#select-franklin').val('Franklin is a turtle');
    });

    //----------------------------------------------------------------------------------------
    //   Page init on Settings
    //----------------------------------------------------------------------------------------
    $('#settings').on('vclick', function(event) {
        // defaults values for Franklin and Emily
        var emily = localStorage.getItem("kissingturtles.settings.emily");
        if(!emily) {
            emily = 'pig';
        }
        $('#emily-img').attr({src: "images/game/" + emily + ".png"});
        var select  = $('#select-emily');
        var options = null;
        if(select.prop) {
            options = select.prop('options');
        }
        else {
            options = select.attr('options');
        }
        var selectedIndex = 0;
        $.each(options, function (key, value) {
            if (value.value == emily)  {
                selectedIndex = key;
            }
        });
        select.val(selectedIndex);
        $(options[selectedIndex]).attr("selected", "selected");
        select.selectmenu('refresh');

        var franklin = localStorage.getItem("kissingturtles.settings.franklin");
        if(!franklin) {
            franklin = 'turtle';
        }
        $('#franklin-img').attr({src: "images/game/" + franklin + ".png"});
        select  = $('#select-franklin');
        options = null;
        if(select.prop) {
            options = select.prop('options');
        }
        else {
            options = select.attr('options');
        }
        var selectedIndex = 0;
        $.each(options, function (key, value) {
            if (value.value == franklin)  {
                selectedIndex = key;
            }
        });
        select.val(selectedIndex);
        $(options[selectedIndex]).attr("selected", "selected");
        select.selectmenu('refresh');
    });

    //----------------------------------------------------------------------------------------
    //   Click on Navigation buttons header bar same as execute
    //----------------------------------------------------------------------------------------
    $("#left").on("vclick", function(event) {
        var dslInput = "move left by 1";
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    $("#right").on("vclick", function(event) {
        var dslInput = "move right by 1";
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    $("#up").on("vclick", function(event) {
        var dslInput = "move up by 1";
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    $("#down").on("vclick", function(event) {
        var dslInput = "move down by 1";
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    $("#dsl").on("vclick", function(event) {
        if($('#dsl-text').css('display') == 'none'){
            $('#dsl-text').show('slow');
        } else {
            $('#dsl-text').hide('slow');
        }
    });

    //----------------------------------------------------------------------------------------
    //   Click on Save on user page. Your name is asked only once.
    //----------------------------------------------------------------------------------------
    $("#submit-user").on("vclick", function(event) {
        var name = $('#input-user-name').val();
        localStorage.setItem("KissingTurtles.UserId", name);
        $.mobile.changePage($("#section-list-games"));
    });

    //----------------------------------------------------------------------------------------
    //   Click on Execute my DSL script brings you here
    //----------------------------------------------------------------------------------------
    $("#submit-game").on("vclick", function(event) {
        var dslInput = $('#input-move-name').val();
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: "KissingTurtles", content: dslInput, gameId: gameId, user: localStorage.getItem("KissingTurtles.UserId")});
    });

    //----------------------------------------------------------------------------------------
    //   Click on 'Create your own game' brings you here
    //----------------------------------------------------------------------------------------
    $("#create-game").on('vclick', function (event) {
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


    var clickGame = function(event) {
        var gameId = $(event.currentTarget).attr('data-id');
        if(gameId) {
            var obj = {user2: localStorage.getItem("KissingTurtles.UserId"), gameId: gameId};
            var newElement = {
                game: JSON.stringify(obj)
            };
            that.updateButtonClicked.notify(newElement, event);
        }
    };
    
    var renderElement = function (element) {
        if (element.offlineAction !== 'DELETED') {
            var a = $('<a>').attr({ href: '#section-show-game'});
            a.attr({id : 'game-list-' + element.id});
            a.attr({'data-id' : element.id});
            a.attr({'data-transition': 'fade' });
            a.text(getText(element));
            a.on('vclick', clickGame);
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
          $('#game-list-' + element.id).parents('li').replaceWith(createListItem(element));
        } else {
          $('#game-list-' + element.id).parents('li').remove();
        }
    };

    var createListItem = function (element) {
        var li, a = $('<a>');
        a.attr({
            href: '#playing_game',
            'data-id': element.id,
            id : 'game-list-' + element.id,
            'data-transition': 'fade'
        });
        a.text(getText(element));
        a.on('vclick', clickGame);
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
