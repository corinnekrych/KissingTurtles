define({

	// Load a basic theme. This is just a CSS file, and since a moduleLoader is
	// configured in run.js, curl knows to load this as CSS.
    theme: { modules: [
        {module:'theme/css/topcoat-mobile-light.css'},
        {module:'theme/css/kt.css'}
    ]},
    games: { create: 'cola/Collection' },
    headerView: {
        render: {
            template: { module: 'text!app/header/template.html' },
            css: { module: 'css!app/header/header.css' }
        },
        insert: { at: 'dom.first!body' }
    },
    startView: {
        render: {
            template: { module: 'text!welcome/template.html' },
            css: { module: 'css!welcome/welcome.css' }
        },
        insert: { after: 'headerView' }
    },
    settingsView: {
        render: {
            template: { module: 'text!app/settings/template.html' },
            css: { module: 'css!app/settings/settings.css' }
        },
        insert: { after: 'startView' }
    },
    gamesView: {
        render: {
            template: { module: 'text!app/games/template.html' },
            css: { module: 'css!app/games/games.css' }
        },
        insert: { after: 'startView' },
        bind: {
            to: { $ref: 'games' },
            bindings : {
                user1: ".user"
            }
        }
    },
    gameView: {
        render: {
            template: { module: 'text!app/game/template.html' },
            css: { module: 'css!app/game/game.css' }
        },
        insert: { after: 'gamesView' },
        on: {
            submit: 'form.getValues | games.update'
        }
    },
    exitView: {
        render: {
            template: { module: 'text!app/exit/template.html' },
            css: { module: 'css!app/exit/exit.css' }
        },
        insert: { after: 'gameView' }
    },
    helpView: {
        render: {
            template: { module: 'text!app/help/template.html' },
            css: { module: 'css!app/help/help.css' }
        },
        insert: { after: 'gameView' }
    },
    playerView: {
        render: {
            template: { module: 'text!app/player/template.html' },
            css: { module: 'css!app/player/player.css' }
        },
        insert: { after: 'gameView' }
    },
    controller: {
        create: 'app/games/controller',
        properties: {
            games: { $ref: 'games' },
            _gameRepository: { $ref: 'gameRepository' },
            _form: { $ref: 'gameView' },
            _start: { $ref: 'startView' },
            _games: { $ref: 'gamesView' },
            _game: { $ref: 'gameView' },
            _settings: { $ref: 'settingsView' },
            _help: { $ref: 'helpView' },
            _exit: { $ref: 'exitView' },
            _player: { $ref: 'playerView' },
            _updateForm: { $ref: 'form.setValues' },
            _ktDrawGrid: { $ref: 'ktDrawGrid'},
            _ktDrawTurtles: { $ref: 'ktDrawTurtles'},
            _ktDrawWalls: { $ref: 'ktDrawWalls'},
            _grailsEvents: { $ref: 'grailsEvents'},
            userIdNotification: { $ref: 'userIdNotification' },
            gameURL: { $ref: 'gameURL' }
        },
        on: {
            startView: {
                'click:.play': 'checkPlayer',
                'click:.settings': 'showSettings',
                'click:.help': 'showHelp'
            },
            gamesView: {
                'click:.game': 'games.edit',
                'click:.create-game': 'createGame'
            },
            gameView: {
                'click:.submit-game' : 'run',
                'click:.answer' : 'answer'
            },
            playerView: {
                'click:.player': 'storePlayer | showGames'
            },
            headerView: {
                'click:.header-button': 'back'
            },
            exitView: {
                'click:.start': 'hideExit | showWelcome'
            },
            settingsView: {
                'change:.franklin-picture': 'changeFranklinPicture',
                'change:.emily-picture': 'changeEmilyPicture',
                'change:.franklin-lg': 'changeFranklinLg',
                'change:.emily-lg': 'changeEmilyLg',
                'click:.reset': 'resetSettings',
                'click:.save': 'saveSettings | showWelcome'
            },
            helpView: {
                'click:.exit-help': 'showWelcome'
            }
        },
        connect: {
            'games.onEdit': 'editGame'
        },
        afterFulfilling: {
            'createGame': 'showGame',
            'run': 'animate'
        },
        ready: 'loadGames'
    },
    gameRepository: {
        wire: {
            spec: 'app/games/rest',
            provide: {
                $userIdNotification: { $ref: 'userIdNotification' },
                $gameURL: { $ref: 'gameURL' }
            }
        }
    },
    ktDrawGrid: { module: 'app/game/ktDrawGrid' },
    ktDrawTurtles: { module: 'app/game/ktDrawTurtles' },
    ktDrawWalls: { module: 'app/game/ktDrawWalls' },
    userIdNotification: { create: 'app/userIdNotification' },
    gameURL: 'http://localhost:8090/KissingTurtles',
    //gameURL: 'http://kisscujo2.herokuapp.com',
    //gameURL: 'http://kisscujo.herokuapp.com',
    grailsEvents: { module: 'js/vendor/grailsEvents/grailsEvents' },
    form: { module: 'cola/dom/form' },
	// Wire.js plugins
    plugins: [
        { module: 'wire/dom', classes: { init: 'loading' } },
        { module: 'wire/dom/render' }, { module: 'wire/on' },
        { module: 'wire/connect' }, { module: 'wire/aop' },
        { module: 'cola' }
    ]
});


