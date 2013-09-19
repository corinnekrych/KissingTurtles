
#### Pre-requesite
* Have Heroku account
* Have Heroku installed [Heroku install](https://toolbelt.heroku.com/standalone)

```c
wget -qO- https://toolbelt.heroku.com/install.sh | sh
```

* Have heroku-deploy plugin installed

```c
heroku plugins:install https://github.com/heroku/heroku-deploy
```

* Hhave npm and grunt installed.
* Minify JS/CSS before running.

```c
grunt
```

* Have Grails app prod war

```c
grails prod war
```

#### Create app
```c
heroku create kissingturtles
```

#### Deploy app
```c
heroku deploy:war --war kissingturtles-0.1.war --app kissingturtles
```
#### Read log
```c
heroku logs
```

#### Heroku Options

```c
heroku config:set WEBAPP_RUNNER_OPT="--enable-compression" --app kissingturtles
```
