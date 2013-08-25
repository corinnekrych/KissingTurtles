KissingTurtles is a collaborative game for 2 players. Emily the little pis wnat to  meet Franklin to kiss :)
Helps Emily/Franklin to move the tyrtle/pig around the maze. You will need to use a langgue to give instructions. 
DSL (Domain Specific Language) is:

move up by 2

General Idea
============

1. DSL to move the turtle/robot is written in Groovy

2. Grails for Server side Controller part that will do shell.evaluate and return position value for the turtle. 

3. HTML5 Canvas API for displaying turtle move and position. We can make it more complex with walls. See little drawing.

4. html5-mobile-scaffolding for the UI. No GSP, only HTML and JS. http://3musket33rs.github.com/ To go further if time permit:

5. push notification: multi players joining a virtual room.

Where's it all started 
================
Grails48 in November 2012, it's over but we had great fun!!!
http://www.youtube.com/watch?v=jvVeWHvmG2I&feature=player_embedded

Mathieu, Fabrice, Martyn and Corinne are the coders behind it.

How to deploy to Heroku
=======================

## Pre-requesite
* Have Heroku account
* Have Heroku installed [Heroku install](https://toolbelt.heroku.com/standalone)

```c
wget -qO- https://toolbelt.heroku.com/install.sh | sh
```

* Have heroku-deploy plugin installed

```c
heroku plugins:install https://github.com/heroku/heroku-deploy
```

* Have Grails app prod war

```c
grails prod war
```

Optionally you can minify JS/CSS before running. You should have npm and grunt installed.

```c
grunt
```

## Create app
```c
heroku create dslprez
```

## Deploy app
```c
heroku deploy:war --war dslprez-0.1.war --app dslprez
```
## Read log
```c
heroku logs
```

## Heroku Options

```c
heroku config:set WEBAPP_RUNNER_OPT="--enable-compression" --app dslprez
```
