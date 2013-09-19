(function (define) {
	define(function (require, exports, module) {
		module.exports = {
            // Model Function
            createGame : function() {
                var self = this;
                return this._gameRepository.createGame().then(function(game) {
                    self.game = game;
                });
            },
            loadGames : function() {
                this.setWelcome();
                var self = this;
                this._gameRepository.listGame().then(function(list) {
                    list.forEach(function(game) {
						if(game.user1 == null || game.user2 == null) {
							self.games.add(game);
						}
                    });
                });
                var grailsEvents = new this._grailsEvents(this.gameURL, {transport: 'sse'});
                var self = this;
                grailsEvents.on('save-game' , function (data) {
                    if (data.userIdNotification != self.userIdNotification) {
                        self.games.update(data.instance);
                    }
                });
                grailsEvents.on('update-game', function (data) {
                    if (data.instance) {
					    self.games.remove({ id: data.instance.id });
                    }
					if (data.userIdNotification != self.userIdNotification) {
						if (self.role == 'franklin' && self.game && self.game.id == data.instance.id) {
                            self.game.mazeDefinition.turtles = data.instance.turtles
                            self._showScript();
                            self._emilyJoined(data.instance.user2);
						}
					}
                });
                grailsEvents.on('delete-game' , function (data) {
                    if (data.userIdNotification != self.userIdNotification) {
                        if(self.game && data.id == self.game.id) {
                            self.game = null;
							data.userIdNotification != 'server' ? self.showExit('.partner-left') : self.showExit('.nobody');
                        } else {
                            self.games.remove({ id: data.id });
                        }
                    }
                });
                grailsEvents.on('execute-game', function (data) {
                    if (data.userIdNotification != self.userIdNotification) {
						if (self.game.id == data.instance.id) {
                            data.instance.asks = [];
							self.animate(data.instance);
                            if (!data.instance.exception) {
							    self._showScript();
                            }
						}
                    }
                });
                grailsEvents.on('ask-game', function (data) {
                    if (data.userIdNotification != self.userIdNotification) {
                        if (self.game.id == data.gameId) {
                            self._showAnswer(data);
                            self._blink('.bell');
                        }
                    }
                });
            },
            editGame : function(game) {
                var self = this;
                var language = localStorage.getItem('kissingturtles.settings.emily-lang') || 'scala';
                var newElement = {
                    game: JSON.stringify({
                        user2: localStorage.getItem('kissingturtles.userid'),
                        language: language,
                        gameId: game.id
                    })
                };
                return this._gameRepository.update(newElement).then(function(newGame) {
                    if (newGame.message) {
                        self.game = null;
                        self.showExit('.tooslow');
                    } else {
                        self.game = newGame;
                        self.showGame();
                        document.querySelector('#message').innerHTML = '';
                        self.role = 'emily';
                        self.games.remove({ id: newGame.id });
                    }
                });
            },
            answer : function() {
				var content = document.querySelector('#response').value;
                var response = {
                    title: 'KissingTurtles',
                    content: content,
                    gameId: this.game.id,
                    user: localStorage.getItem('kissingturtles.userid'),
                    role: this.game.role
                };
                this._gameRepository.answer(response);
                document.querySelector('#interaction').value += '\n' + localStorage.getItem('kissingturtles.userid') + '\t: ' + content;
				document.querySelector('.bell').classList.remove('blink');
				document.querySelector('#response').setAttribute('disabled','disabled');
				document.querySelector('.answer').setAttribute('disabled','disabled');
				document.querySelector('#response').value = '';
            },
            run : function() {
                var script = document.querySelector('#input-move-name').value;
                if(script == "") {
                    document.querySelector('#message').innerHTML = 'Please enter a script !!';
                    return;
                } else {
                    document.querySelector('#message').innerHTML = '';
                }
				this._hideScript();
                var value = {
                    content: script,
                    gameId: this.game.id,
                    user: localStorage.getItem('kissingturtles.userid'),
                    role: this.role};
                var self = this;
                return this._gameRepository.run(value).then(function(game) {
                    if (game.exception) {
                        var error = game.exception;
                        document.querySelector('#message').innerHTML = error;
						self._showScript();
                    } else {
                        document.querySelector('#input-move-name').value= '';
                    }
                    self.animate(game);
                });
            },
            checkPlayer: function() {
                var button = document.querySelector('.header-button');
                button.classList.toggle('visible');
                button.classList.toggle('hidden');
                localStorage.getItem('kissingturtles.userid') ? this.showGames() : this.showPlayer();
            },
            storePlayer: function() {
                localStorage.setItem('kissingturtles.userid', document.querySelector('#player').value);
            },
            // View Function
            _stackHide : [],
            _stackShow : [],
			_currentView: null,
			setWelcome: function() {
				this._currentView = this._start;
			},
			_hideView: function(view) {
				this._hide(view);
                this._stackShow.push(view);
			},
			_hide: function(view) {
                view.classList.add('hidden');
                view.classList.remove('visible');
			},
			_showView: function(view) {
				this._hideView(this._currentView);
				this._show(view);
                this._stackHide.push(view);
			},
			_show: function(view) {
                view.classList.add('visible');
                view.classList.remove('hidden');
				this._currentView = view;
			},
            back: function() {
                this._hide(this._stackHide.pop());
                if(this._stackHide.length == 0) {
                    this.showWelcome();
                } else if(this._currentView == this._game) {
                    this.giveup();
                } else {
                    this._show(this._stackShow.pop());
                }
            },
            showWelcome : function() {
                var button = document.querySelector('.header-button');
                button.classList.add('hidden');
                button.classList.remove('visible');
				this._showView(this._start);
                this._stackHide = [];
                this._stackShow = [];
            },
            showPlayer : function() {
				this._showView(this._player);
            },
            showGames : function() {
				this._showView(this._games);
            },
            showHelp : function() {
                this._showView(this._help);
            },
            showExit : function(classState) {
                document.querySelector(classState).classList.add('exit-status');
                this._exit.classList.remove('hidden');
                this._exit.classList.add('visible');
				this._exit.classList.add('modalDialog');
            },
            showGame : function() {
                this._resetGame();
                if(this.game.user2) {
                    document.querySelector('#input-move-name').classList.add(localStorage.getItem('kissingturtles.settings.emily-lang') || 'scala');
                } else {
                    document.querySelector('#input-move-name').classList.add(localStorage.getItem('kissingturtles.settings.franklin-lang') || 'groovy');
                }
                document.querySelector('#message').innerHTML = 'Waiting for Emily to come !!!';
                this._showView(this._game);

                var franklinImageName = localStorage.getItem('kissingturtles.settings.franklin');
                if (franklinImageName) {
                    franklinImageName += '.png';
                    if (!this.game.mazeDefinition.turtles) {
                        this.game.mazeDefinition.turtles = {};
                    }
                    if (!this.game.mazeDefinition.turtles.images) {
                        this.game.mazeDefinition.turtles.images = {}
                    }
                    this.game.mazeDefinition.turtles.images['franklin'] = franklinImageName;
                }
                var emilyImageName = localStorage.getItem('kissingturtles.settings.emily');
                if (emilyImageName) {
                    if (!this.game.mazeDefinition.turtles) {
                        this.game.mazeDefinition.turtles = {};
                    }
                    if (!this.game.mazeDefinition.turtles.images) {
                        this.game.mazeDefinition.turtles.images = {}
                    }
                    emilyImageName += '.png';
                    this.game.mazeDefinition.turtles.images['emily'] = emilyImageName;
                }

                this.drawGrid = this._ktDrawGrid(document.querySelector('#canvasGrid'), this.game.mazeDefinition.turtles.grid);
                this.drawWalls = this._ktDrawWalls(document.querySelector('#canvasWalls'), this.game.mazeDefinition.walls, this.game.mazeDefinition.turtles.grid);
                this.drawTurtles = this._ktDrawTurtles(document.querySelector('#canvasTurtles'), this.game.mazeDefinition.turtles, this.game.mazeDefinition.turtles.grid);
                this.role = 'franklin';
                this.user = localStorage.getItem('kissingturtles.userid');

            },
            _resetGame: function() {
                document.querySelector('#message').innerHTML = '';
                document.querySelector('#input-move-name').value = '';
                document.querySelector('#input-move-name').setAttribute('disabled','disabled');
                document.querySelector('#input-move-name').classList.remove('groovy');
                document.querySelector('#input-move-name').classList.remove('scala');
				document.querySelector('.belldsl').classList.remove('blink');
                document.querySelector('#response').value = '';
                document.querySelector('#interaction').value = '';
                document.querySelector('#response').setAttribute('disabled','disabled');
                document.querySelector('.submit-game').setAttribute('disabled','disabled');
                document.querySelector('.answer').setAttribute('disabled','disabled');
				document.querySelector('.bell').classList.remove('blink');
            },
            _blink: function(elt) {
				var selfBlink = function() {
					var s = document.querySelector(elt);
					if(s.classList.contains('blink')) {
						s.style.visibility = (s.style.visibility == 'visible') ? 'hidden' : 'visible';
						window.setTimeout(selfBlink, 1000);
					} else {
						s.style.visibility = 'visible';
					}
				}
				window.setTimeout(selfBlink, 1000);
            },
            _showScript: function() {
				document.querySelector('.submit-game').removeAttribute('disabled');
				document.querySelector('#input-move-name').removeAttribute('disabled');
                document.querySelector('#input-move-name').focus();
				document.querySelector('.belldsl').classList.add('blink');
				this._blink('.belldsl');
            },
            _hideScript: function() {
				document.querySelector('#input-move-name').setAttribute('disabled','disabled');
				document.querySelector('.submit-game').setAttribute('disabled','disabled');
				document.querySelector('.belldsl').classList.remove('blink');
			},
            _showAnswer: function (data) {
                document.querySelector('#interaction').value += '\n' + data.user + '\t: ' + data.question;
                document.querySelector('#response').value = '';
                document.querySelector('#response').removeAttribute('disabled');
                document.querySelector('#response').focus();
                document.querySelector('.answer').removeAttribute('disabled');
                document.querySelector('.bell').classList.add('blink');
            },
            showSettings : function() {
                var button = document.querySelector('.header-button');
                button.classList.toggle('visible');
                button.classList.toggle('hidden');
                this._showView(this._settings);

                var franklin = localStorage.getItem('kissingturtles.settings.franklin');
                if(!franklin) {
                    franklin = 'turtle';
                    localStorage.setItem('kissingturtles.settings.franklin', franklin);
                }
                var imageF = document.querySelector('#franklin-img');
                imageF.src = 'theme/images/game/' + franklin + '.png';

                // defaults values for Franklin and Emily
                var emily = localStorage.getItem('kissingturtles.settings.emily');
                if(!emily) {
                    emily = 'pig';
                    localStorage.setItem('kissingturtles.settings.emily', emily);
                }
                var image = document.querySelector('#emily-img');
                image.src = 'theme/images/game/' + emily + '.png';

                this._changeSelected('select-emily', emily);
                this._changeSelected('select-franklin', franklin);
                this._changeDisabled('select-franklin', 'select-emily');
                this._changeDisabled('select-emily', 'select-franklin');
            },
            changeFranklinPicture : function(data) {
				this._changePicture(data, 'franklin');
                this._changeDisabled('select-franklin', 'select-emily');
            },
            changeEmilyPicture : function(data) {
                this._changePicture(data, 'emily');
                this._changeDisabled('select-emily', 'select-franklin');

            },
			_changePicture: function(data, character) {
                var value = data.target.options[data.target.selectedIndex].value;
                var image = document.querySelector('#' + character + '-img');
                image.src = 'theme/images/game/' + value + '.png';
                this._changeSelected('select-' + character, value);
			},
            changeFranklinLg : function(data) {
                var value = data.target.options[data.target.selectedIndex].value;
                var image = document.querySelector('#franklin-lang-img');
                image.src = 'theme/images/' + value + '.png';
                this._changeSelected('select-franklin-lang', value);
            },
            changeEmilyLg : function(data) {
                var value = data.target.options[data.target.selectedIndex].value;
                var image = document.querySelector('#emily-lang-img');
                image.src = 'theme/images/' + value + '.png';
                this._changeSelected('select-emily-lang', value);
            },
            _changeDisabled : function(select1Value, select2Value) {
                var select1 =  document.getElementById(select1Value);
                var selectedIndex;
                for (var i = 0; i < select1.length; i++) {
                    if (select1.options[i].selected) {
                        selectedIndex = i;
                    }
                }

                var select2 =  document.getElementById(select2Value);
                for (var i = 0; i < select2.length; i++) {
                    if (select2.options[i].disabled) {
                        select2.options[i].removeAttribute('disabled');
                    }
                }
                select2.options[selectedIndex].disabled = 'disabled';
            },
            _changeSelected : function(selectValue, cartoon) {
                var option = document.querySelector('#' + selectValue + ' option[selected]');
                if(option!= null) {
                    option.removeAttribute('selected');
                }
                document.querySelector('#' + selectValue + " option[value='" + cartoon + "']").setAttribute('selected', 'selected');
            },
            resetSettings : function(event) {
                localStorage.clear();
                localStorage.setItem('kissingturtles.settings.franklin', 'turtle');
                localStorage.setItem('kissingturtles.settings.emily', 'pig');
                var imageF = document.getElementById('franklin-img');
                imageF.src = 'theme/images/game/turtle.png';
                var imageE = document.getElementById('emily-img');
                imageE.src = 'theme/images/game/pig.png';

                this._changeSelected('select-emily', 'pig');
                this._changeSelected('select-franklin', 'turtle');
                this._changeDisabled('select-franklin', 'select-emily');
                this._changeDisabled('select-emily', 'select-franklin');
            },
            saveSettings : function(event) {
                var franklin =  document.querySelector('#select-franklin option[selected]').getAttribute('value');
                var emily =  document.querySelector('#select-emily option[selected]').getAttribute('value');
                var franklinLang =  document.querySelector('#select-franklin-lang option[selected]').getAttribute('value');
                var emilyLang =  document.querySelector('#select-emily-lang option[selected]').getAttribute('value');
                localStorage.setItem('kissingturtles.settings.franklin', franklin);
                localStorage.setItem('kissingturtles.settings.emily', emily);
                localStorage.setItem('kissingturtles.settings.franklin-lang', franklinLang);
                localStorage.setItem('kissingturtles.settings.emily-lang', emilyLang);
            },
            _emilyJoined: function(emilyName) {
                this.drawTurtles({emily: this.game.mazeDefinition.turtles.position['emily']});
                document.querySelector('#message').innerHTML = emilyName + ' joined the game as Emily!';
            },
            hideExit : function() {
                document.querySelector('.exit-status').classList.remove('exit-status');
                this._exit.classList.add('hidden');
                this._exit.classList.remove('visible');
                this._exit.classList.remove('modalDialog');
            },
            giveup: function() {
                var self = this;
                this._gameRepository.delete(this.game).then(function() {
                    self.game = null;
                });
                this.showExit('.giveup');
            },
            animate : function(game) {
                var self = this;
                // only for my game
                if (this.game.id == game.id) {
                    // refresh me if it's not myself pls

                    var otherPlayer = game.user1;
                    if (self.user == game.user1) {
                        otherPlayer = game.user2
                    }

                    if (game.asks) {
						for(var key in game.asks) {
							if(game.asks.hasOwnProperty(key)) {
								var eachAsk = game.asks[key];
								for(var innerKey in eachAsk) {
									if(eachAsk.hasOwnProperty(innerKey)) {
										if (innerKey == '_question') {
											document.querySelector('#interaction').value += '\n' + self.user + '\t: ' + eachAsk[innerKey];
										} else {
											document.querySelector('#interaction').value += '\n' + otherPlayer + '\t: ' + eachAsk[innerKey];
										}
									}
								}
								document.querySelector('#response').setAttribute('disabled','disabled');
								document.querySelector('.answer').setAttribute('disabled','disabled');
							}
						}
                    } 
					
                    if (game.win || game.lost) {
                        this._resetGame();
                    }

                    var animating = 0;
					for(var key in game.position) {
						if(game.position.hasOwnProperty(key)) {
							var value = game.position[key];
							var obj;
							if (value.length != 0) {
                                animating++;
                                for (var i= 0; i < value.length - 1; i++) {
                                    obj = {};
                                    obj[key] = value[i];
                                    self.drawTurtles(obj);
                                }
                                obj = {};
                                obj[key] = value[value.length - 1];
                                self.drawTurtles(obj, function () {
                                    animating--;
                                    if (animating == 0) {
                                        if (game.win) {
                                            self.drawTurtles.win(game.winningAnimation[0], game.winningAnimation[1], function() {
                                                self.game = null;
                                                self.showExit('.won');
                                            });
                                        } else if (game.lost) {
                                            self.drawTurtles.lost(game.winningAnimation[0], game.winningAnimation[1], function() {
                                                self.game = null;
                                                self.showExit('.lost');
                                            });
                                        }
                                    }
                                });
                            }
						}
                    }
                }
            }
		}
	});
}(
	typeof define == 'function' && define.amd
		? define
		: function (factory) { module.exports = factory(require, exports, module); }
));