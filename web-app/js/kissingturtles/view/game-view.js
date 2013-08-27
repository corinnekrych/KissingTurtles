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
            alert('Ooops something wrong happens');
        } else if (data.item.message) {
            alert('Ooops something wrong happens');
        } else {
            if (!data.item.NOTIFIED) {
                that.currentMaze = data.item.mazeDefinition;
                // take local config to customize Franklin's picture
                var franklinImageName = localStorage.getItem('kissingturtles.settings.franklin');
                if (franklinImageName) {
                    franklinImageName += '.png';
                    if (!that.currentMaze.turtles) {
                        that.currentMaze.turtles = {};
                    }
                    if (!that.currentMaze.turtles.images) {
                        that.currentMaze.turtles.images = {}
                    }
                    that.currentMaze.turtles.images['franklin'] = franklinImageName;
                }
                var emilyImageName = localStorage.getItem('kissingturtles.settings.emily');
                if (emilyImageName) {
                    if (!that.currentMaze.turtles) {
                        that.currentMaze.turtles = {};
                    }
                    if (!that.currentMaze.turtles.images) {
                        that.currentMaze.turtles.images = {}
                    }
                    emilyImageName += '.png';
                    that.currentMaze.turtles.images['emily'] = emilyImageName;
                }

                that.drawGrid = ktDrawGrid(document.getElementById('canvasGrid'), that.currentMaze.turtles.grid);
                that.drawWalls = ktDrawWalls(document.getElementById('canvasWalls'), that.currentMaze.walls, that.currentMaze.turtles.grid);
                that.drawTurtles = ktDrawTurtles(document.getElementById('canvasTurtles'), that.currentMaze.turtles, that.currentMaze.turtles.grid);
                that.role = 'franklin';
                that.user = localStorage.getItem('KissingTurtles.UserId');
                that.gameId = data.item.id;
            }
            renderElement(data.item);
            showElement(data.item);
            $('#list-games').listview('refresh');

            if (!data.item.NOTIFIED) {
                $.mobile.changePage($('#section-show-game'));
                $('#input-move-name').textinput('enable')
                $('#input-move-name').val('');
                $('#response').val('');
                $('#submit-game').button('disable');
                $('#script').trigger('expand');
                $('#chat').trigger('collapse');
            }
        }
    });

    //----------------------------------------------------------------------------------------
    //   Callback after joining the game
    //----------------------------------------------------------------------------------------
    that.model.updatedItem.attach(function (data, event) {
        // Display error for third fellow. Only 2 players game.
        if (data.item.errors) {
            alert('Ooops something wrong happens');
        } else if (data.item.message) {
            alert('Ooops something wrong happens' +  data.item.message);
            event.stopPropagation();
        } else {
            // In case of Emily or Franklin we go here
            updateElement(data.item);
            if (!data.item.NOTIFIED) {
                that.currentMaze = data.item.mazeDefinition;
                // For Emily game, initialize canvas
                // take local config to customize Franklin's picture
                var franklinImageName = localStorage.getItem('kissingturtles.settings.franklin');
                if (franklinImageName) {
                    franklinImageName += '.png';
                    that.currentMaze.turtles.images['franklin'] = franklinImageName;
                }
                var emilyImageName = localStorage.getItem('kissingturtles.settings.emily');
                if (emilyImageName) {
                    emilyImageName += '.png';
                    that.currentMaze.turtles.images['emily'] = emilyImageName;
                }
                that.drawGrid = ktDrawGrid(document.getElementById('canvasGrid'), that.currentMaze.turtles.grid);
                that.drawWalls = ktDrawWalls(document.getElementById('canvasWalls'), that.currentMaze.walls, that.currentMaze.turtles.grid);
                that.drawTurtles = ktDrawTurtles(document.getElementById('canvasTurtles'), that.currentMaze.turtles, that.currentMaze.turtles.grid);

                that.user = localStorage.getItem('KissingTurtles.UserId');
                that.role = 'emily';
                that.gameId = data.item.id;
                $.mobile.changePage($('#section-show-game'));
                $('#input-move-name').val('');
                $('#response').val('');
                $('#submit-game').button('disable');
                $('#script').trigger('collapse');
                $('#chat').trigger('expand');
                $('#belldsl').removeClass('blink');
            } else if (that.role == 'franklin' && that.gameId == data.item.id) {
                that.currentMaze.turtles = data.item.turtles
                // For Franklin game
                showGeneralMessage(data.item.user2 + ' joined the game as Emily!');
                $('#submit-game').button('enable');
                that.drawTurtles({emily: that.currentMaze.turtles.position['emily']});
                $('#script').trigger('expand');
                $('#belldsl').addClass('blink');
                blink($('#belldsl'));
            } else {
                $('#list-games').listview('refresh');
            }
        }
    });

    var showGeneralMessage = function(data) {
        $.mobile.showPageLoadingMsg( $.mobile.pageLoadErrorMessageTheme, data, true );
        setTimeout( $.mobile.hidePageLoadingMsg, 3000 );
    };

    var toggle = function(elt) {
        $('#input-move-name').val('');
        $('#response').val('');
        if ($(elt).attr('disabled')) {
            $(elt).button('enable')
            $('#belldsl').addClass('blink');
            blink('#belldsl');
            $('#script').trigger('expand');
            //$('#chat').trigger('collapse');
        } else {
            $('#belldsl').removeClass('blink');
            $(elt).button('disable');
            //$('#script').trigger('collapse');
            $('#chat').trigger('expand');
        }
    };

    $('#script').on('expand', function (e) {
        $('#chat').trigger('collapse');
    });

    $('#chat').on('expand', function (e) {
        $('#script').trigger('collapse');
    });


    //----------------------------------------------------------------------------------------
    //    Callback to display the maze after execute method
    //----------------------------------------------------------------------------------------
    that.model.executed.attach(function (data, event) {
        $('#error').html('');
        // only for my game
        if (that.gameId == data.item.id) {
            // refresh me if it's not myself pls
            var myGameObject = data.item;
            if (!data.item.NOTIFIED) {

                if (myGameObject.exception) {
                    var error = myGameObject.exception.message && myGameObject.exception.message.split('-') && myGameObject.exception.message.split('-')[1]
                    $('#error').html(error);
                    $('#input-move-name').html(myGameObject.script);
                } else {
                    toggle('#submit-game');
                }

                var otherPlayer = data.item.user1;
                if (that.user == data.item.user1) {
                    otherPlayer = data.item.user2
                }

                if (myGameObject.asks) {
                    $.each(myGameObject.asks, function(key, value) {
                        $.each(value, function(innerKey, innerValue) {
                            if (innerKey == '_question') {
                                $('#interaction').append('\n' + that.user + '\t: ' + innerValue).keyup();
                            } else {
                                $('#interaction').append('\n' + otherPlayer + '\t: ' + innerValue).keyup();
                            }

                        });
                        $('#response').textinput('disable');
                        $('#answer').button('disable');
                    });
                }
            } else {
                if (!myGameObject.exception)
                    toggle('#submit-game');
            }

            if (data.item.win) {
                $('#input-move-name').val('');
                $('#input-move-name').textinput('disable')
                $('#response').val('');
                $('#interaction').val('');
                $('#response').textinput('disable');
                $('#submit-game').button('disable');
                $('#answer').button('disable');
                setTimeout(function() {
                    $.mobile.changePage( '#won');
                }, 4000);
            }

            $.each(myGameObject.position, function(key, value) {
                for (var i= 0; i < value.length; i++) {
                    var obj= {};
                    obj[key] = value[i];
                    that.drawTurtles(obj, function () {
                        if (myGameObject.win) {
                            that.drawTurtles.win(myGameObject.winningAnimation[0], myGameObject.winningAnimation[1]);
                        }
                    });
                }
            });



        }
    });


    var blink = function(elt) {
        $(elt).fadeOut('slow', function() {
            $(elt).fadeIn('slow', function() {
                if($(elt).hasClass('blink')) {
                    blink(elt);
                }
            });
        });
    };


    //----------------------------------------------------------------------------------------
    //    Callback to display question asked to partner
    //----------------------------------------------------------------------------------------
    that.model.asked.attach(function (data, event) {
        if (that.gameId == data.item.gameId) {
            $('#interaction').append('\n' + data.item.user + '\t: ' + data.item.question).keyup();
            $('#response').val('').textinput('enable').focus();
            $('#answer').button('enable');

            $('#bell').addClass('blink');
            blink('#bell');
        }
    });

    //----------------------------------------------------------------------------------------
    //   Click on Execute my DSL script brings you here
    //----------------------------------------------------------------------------------------
    $('#answer').on('vclick', function(event) {
        var answer = $('#response').val();
        var gameId = that.gameId;
        $('#response').textinput('disable');
        $('#answer').button('disable');
        that.answerButtonClicked.notify({
            title: 'KissingTurtles',
            content: answer,
            gameId: gameId,
            user: that.user,
            role: that.role});
        $('#interaction').append('\n' + that.user + '\t: ' + answer).keyup();
        $('#bell').removeClass('blink');
    });

    //----------------------------------------------------------------------------------------
    //   Click on Cancel for a Game => Leaving
    //----------------------------------------------------------------------------------------
    $('#leavingGame').on('vclick', function(event) {
        that.deleteButtonClicked.notify({ id: that.gameId });
    });

    that.model.deletedItem.attach(function (data, event) {
        // Display error for third fellow. Only 2 players game.
        if (data.item.errors) {
            alert('Ooops something wrong happens');
        } else if (data.item.message) {
            alert('Ooops something wrong happens' +  data.item.message);
            event.stopPropagation();
        } else {
            // In case of Emily or Franklin we go here
            renderList();
            if (data.item.NOTIFIED && that.gameId == data.item.id) {
                if (data.item.userIdNotification == 'server') {
                    $('#header-left').text('Snifff');
                    $('#content-left').text('Nobody wants to join');
                }
                $.mobile.changePage( '#game-left');
            }
        }
    });

    //----------------------------------------------------------------------------------------
    //   Click on Play brings you here
    //----------------------------------------------------------------------------------------
    $('#play').on('vclick', function (e) {
        var id = localStorage.getItem('KissingTurtles.UserId');
        if (id) {
            $.mobile.changePage($('#section-list-games'));
            that.listButtonClicked.notify();
        } else {
            $.mobile.changePage($('#section-show-user'));
        }
    });

    //----------------------------------------------------------------------------------------
    //   Click on Settings:change images
    //----------------------------------------------------------------------------------------
    $('#select-emily').on('change', function(event) {
        changeDisabled('select-emily', 'select-franklin');

        var value = $('#select-emily').val();
        localStorage.setItem('kissingturtles.settings.emily', value);
        $('#emily-img').attr({src: 'images/game/' + value + '.png'});
    });

    $('#select-franklin').on('change', function(event) {
        changeDisabled('select-franklin', 'select-emily');

        var value = $('#select-franklin').val();
        localStorage.setItem('kissingturtles.settings.franklin', value);
        $('#franklin-img').attr({src: 'images/game/' + value + '.png'});
    });


    var changeDisabled = function(select1Value, select2Value) {
        var select1 = $('#' + select1Value);
        var select2 = $('#' + select2Value);

        $('#' + select2Value + " option[disabled='disabled']").removeAttr('disabled');

        var index = select1[0].selectedIndex;
        var options = select2.prop('options');
        $(options[index]).attr('disabled', 'disabled');
        select1.selectmenu('refresh', true );
        select2.selectmenu('refresh', true );
    }

    var changeSelected = function(selectValue, cartoon) {
        var select  = $('#' + selectValue);
        var options = select.prop('options');
        var selectedIndex = 0;
        $.each(options, function (key, value) {
            if (value.value == cartoon)  {
                selectedIndex = key;
            }
        });
        select.val(selectedIndex);
        $(options[selectedIndex]).attr('selected', 'selected');
    }


    //----------------------------------------------------------------------------------------
    //   Click on Settings:'Reset settings' brings you here.
    //   To clear your name from your browser localStorage.
    //----------------------------------------------------------------------------------------
    $('#reset-settings').on('vclick', function(event) {
        localStorage.clear();
        // defaults values for Franklin and Emily
        var emily = 'pig';
        localStorage.setItem('kissingturtles.settings.emily', emily);
        $('#emily-img').attr({src: 'images/game/pig.png'});
        var franklin = 'turtle';
        localStorage.setItem('kissingturtles.settings.franklin', franklin);
        $('#franklin-img').attr({src: 'images/game/turtle.png'});

        changeSelected('select-emily', emily);
        changeSelected('select-franklin', franklin);
        changeDisabled('select-franklin', 'select-emily');
        changeDisabled('select-emily', 'select-franklin');
    });

    //----------------------------------------------------------------------------------------
    //   Page init on Settings
    //----------------------------------------------------------------------------------------
    $('#settings').on('vclick', function(event) {
        $.mobile.changePage('#section-settings');
        // defaults values for Franklin and Emily
        var emily = localStorage.getItem('kissingturtles.settings.emily');
        if(!emily) {
            emily = 'pig';
            localStorage.setItem('kissingturtles.settings.emily', emily);
        }
        $('#emily-img').attr({src: 'images/game/' + emily + '.png'});
        var franklin = localStorage.getItem('kissingturtles.settings.franklin');
        if(!franklin) {
            franklin = 'turtle';
            localStorage.setItem('kissingturtles.settings.franklin', franklin);
        }
        $('#franklin-img').attr({src: 'images/game/' + franklin + '.png'});
        changeSelected('select-emily', emily);
        changeSelected('select-franklin', franklin);
        changeDisabled('select-franklin', 'select-emily');
        changeDisabled('select-emily', 'select-franklin');
    });

    //----------------------------------------------------------------------------------------
    //   Click on Navigation buttons header bar same as execute
    //----------------------------------------------------------------------------------------
    $('#left').on('vclick', function(event) {
        var dslInput = 'move left by 1';
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: 'KissingTurtles', content: dslInput, gameId: gameId, user: localStorage.getItem('KissingTurtles.UserId')});
    });

    $('#right').on('vclick', function(event) {
        var dslInput = 'move right by 1';
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: 'KissingTurtles', content: dslInput, gameId: gameId, user: localStorage.getItem('KissingTurtles.UserId')});
    });

    $('#up').on('vclick', function(event) {
        var dslInput = 'move up by 1';
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: 'KissingTurtles', content: dslInput, gameId: gameId, user: localStorage.getItem('KissingTurtles.UserId')});
    });

    $('#down').on('vclick', function(event) {
        var dslInput = 'move down by 1';
        var gameId = that.gameId;
        that.executeButtonClicked.notify({title: 'KissingTurtles', content: dslInput, gameId: gameId, user: localStorage.getItem('KissingTurtles.UserId')});
    });

    $('#dsl').on('vclick', function(event) {
        if($('#dsl-text').css('display') == 'none'){
            $('#dsl-text').show('slow');
        } else {
            $('#dsl-text').hide('slow');
        }
    });

    //----------------------------------------------------------------------------------------
    //   Click on Save on user page. Your name is asked only once.
    //----------------------------------------------------------------------------------------
    $('#submit-user').on('vclick', function(event) {
        var name = $('#input-user-name').val();
        localStorage.setItem('KissingTurtles.UserId', name);
        $.mobile.changePage($('#section-list-games'));
    });

    //----------------------------------------------------------------------------------------
    //   Click on Execute my DSL script brings you here
    //----------------------------------------------------------------------------------------
    $('#submit-game').on('vclick', function(event) {
        var dslInput = $('#input-move-name').val();
        var gameId = that.gameId;
        //toggle('#submit-game');
        var lang = 'groovy';
        if (that.role == 'emily') {
            lang = 'scala';
        }
        that.executeButtonClicked.notify({title: 'KissingTurtles', lang: lang, content: dslInput, gameId: gameId, user: that.user, role: that.role});
    });

    //----------------------------------------------------------------------------------------
    //   Click on 'Create your own game' brings you here
    //----------------------------------------------------------------------------------------
    $('#create-game').on('vclick', function (event) {
        var newElement = {
            user1: localStorage.getItem('KissingTurtles.UserId')
        };
        that.createButtonClicked.notify(newElement, event);
    });

    //----------------------------------------------------------------------------------------
    //   Rendering methods
    //----------------------------------------------------------------------------------------
    var showElement = function (element) {
        resetForm('form-update-game');
        if (element) {
            $.each(element, function(name, value) {
                var input = $('#input-game-' + name);
                input.val(value);
            });
        }
        $('#delete-game').show();
    };

    var resetForm = function (form) {
        $("input[type='date']").each(function() {
            $(this).scroller('destroy');
            $(this).scroller({
                preset: 'date',
                theme: 'default',
                display: 'modal',
                mode: 'scroller',
                dateOrder: 'mmD ddyy'
            });
        });
        var div = $('#' + form);
        div.find("input:text, input:hidden, input[type='number'], input:file, input:password, textarea").val('');
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
            var obj = {user2: localStorage.getItem('KissingTurtles.UserId'), gameId: gameId};
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
            if (element.user1 != localStorage.getItem('KissingTurtles.UserId') && element.user2 == null) {
                if (element.offlineStatus === 'NOT-SYNC') {
                    $('#list-games').append($("<li data-theme='e'>").append(a));
                } else {
                    $('#list-games').append($('<li>').append(a));
                }
            }
        }
    };

    var updateElement = function (element) {
        // filter, display only game created by other
        if (element.user1 != localStorage.getItem('KissingTurtles.UserId') && element.user2 == null) {
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
        return data.user1 + ' is waiting for you';
    };

    return that;
};
