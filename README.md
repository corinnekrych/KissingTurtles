## Playing KissingTurtles Game

### Goal

Franklin is lost in the maze, help him to meet Emily at the Heart before Birdy reaches the heart.

* Franklin speaks Groovy and Emily is into Scala.
* Using special commands, you can move up/down/right/left to navigate through the maze.
* If Birdy come too close you can change the meeting point to make it easier for you and Emily to meet.
* You can even ask questions to your partner and you can enrich your script with any valid Scala/Groovy code!

![KissingTurtles](/kissing.png)

### Requirements
* KissingTurtles is a collaborative game for 2 players.
* You will need two separate browsers (like use Chrome for player1, FireFox for player2) if player son the same computer.
Some data like player's name are stored on your browser local storage.
* First time you use KissingTurtles you will be prompted to enter your speudo/name which will be displayed in the list of open games
* When you create a game you become Franklin and you should wait until emily is joining the game. If nobody joins the game will be closed after 10mins.
* You can also join a game as emily bu clicking on the list of available game waiting for their Emily.

### Rules
* Each player takes turn.
* Franklin starts the game by writing his script.
* A script can not contain more than 3 specials commands.
* Franklin speaks Groovy (blue) and Emily is into Scala (red). Programmers can use plain Groovy/Scala in their script.

### Settings
You can change the preferred language. Both players can play using the same language.

### Help on commands

* In Groovy:

```java
move up
move left by 2
kiss
ask "Change meeting?" assign to response 
if(response == "y") meet x:12, y:1 
```

* In Scala

```java
I move up
I move left by 2
I kiss;
val response = I ask "Change meeting?"
if(response == "y") meet (12, 1) 
```

## Contributing to KissingTurtles Game

For grown-up kids who want to bring new ideas/challenges to KissingTurtles, you are welcome! This repo is a place to
have fun, try new technos, new languages.

Let's discuss your idea. Create an issue on github, send a Pull Request. We also have a list of [TODO](https://github.com/corinnekrych/KissingTurtles/blob/master/TODO.md)

### What is it made of

1. DSL to move the turtle/robot is written in Groovy, Scala
2. Grails for Server side Controller part that will do shell.evaluate and return position value for the turtle.
3. HTML5 Canvas API for displaying turtle moves and positions. .
4. html5-mobile-scaffolding for the UI. No GSP, only HTML and JS. http://3musket33rs.github.com/ To go further if time permit:
5. Push notification (atmosphere with SSE): multi players joining a virtual room.

### How to build your dev environment
See [dev environment](https://github.com/corinnekrych/KissingTurtles/blob/master/DEV.md "Deploy to Heroku") instruction

### How to deploy to Heroku
See [deploy](https://github.com/corinnekrych/KissingTurtles/blob/master/DEPLOY.md "Deploy to Heroku") instruction.

### Where's it all started 

Grails48 in November 2012, it's over but we had great fun!!!
http://www.youtube.com/watch?v=jvVeWHvmG2I&feature=player_embedded

Let's carry on the fun an d improve the game.

### Developers
Fabrice, Mathieu, Pascal, Martyn, and Corinne are the coders behind it.
Milou, Lulu are the testers.
