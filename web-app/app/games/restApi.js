(function (define) {
	define(function (require) {

		var when = require('when');

		/**
		 * Implementation of the client-side game API that communicates with the
		 * REST game server.
		 */
		return {
			/**
			 * Game client implementation that will perform the actual REST operations.
			 * Must be injected
			 * @required
			 * @type {Function}
			 */
			gameClient: null,

			/**
			 * Base host/url of the REST API server endpoint with which to communicate
			 * @required
			 * @type {String}
			 */
			host: null,

			/**
			 * Create a new game resource
			 * @return {Promise} promise for the new game
			 */
			createGame: function () {
                var cfg = {
                    user1: localStorage.getItem('kissingturtles.userid'),
                    language: localStorage.getItem('kissingturtles.settings.franklin-lang')
                };

                cfg.userIdNotification = this.userIdNotification;

				return this.gameClient({
                        path: this.host + "/game/save",
                        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                        method: 'POST',
                        entity: cfg
                    }).then(function (game) {
						return game;
					}
				);
			},

			/**
			 */
			listGame: function () {
                return this.gameClient({
                    path: this.host + "/game/list",
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    method: 'POST'
                }).then(function (list) {
                        return list;
                    }
                );
			},

            /**
             */
            run: function (game) {
                game.userIdNotification = this.userIdNotification;
                return this.gameClient({
                    path: this.host + "/game/run",
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    method: 'POST',
                    entity: game
                }).then(function (game) {
                        return game;
                    }
                );
            },
            /**
             */
            update: function (game) {
                game.userIdNotification = this.userIdNotification;
                return this.gameClient({
                    path: this.host + "/game/update",
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    method: 'POST',
                    entity: game
                }).then(function (game) {
                        return game;
                    }
                );
            },
            /**
             */
            delete: function (game) {
                game.userIdNotification = this.userIdNotification;
                return this.gameClient({
                    path: this.host + "/game/delete",
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    method: 'POST',
                    entity: game
                }).then(function (game) {
                        return game;
                    }
                );
            },
            /**
             */
            answer: function (response) {
                response.userIdNotification = this.userIdNotification;

                return this.gameClient({
                    path: this.host + "/game/answer",
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    method: 'POST',
                    entity: response
                }).then(function () {
                        return "";
                    }
                );
            }
		};


	});
})(typeof define === 'function' && define.amd ? define : function (factory) { module.exports = factory(require); });