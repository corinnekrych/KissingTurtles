## Playing KissingTurtles Game

### Goal

Franklin is lost in the maze, help him to meet Emily at the Heart before Birdy reaches the heart.
Franklin speaks Groovy and Emily is into Scala. 
Using special commands, you can move up/down/right/left to navigate through the maze. If Birdy come too close you can change the meeting point to make it easier for you and Emily to meet. 
You can even ask questions to your partner and you can enrich your script with any valid Scala/Groovy code!

### Requirements
KissingTurtles is a collaborative game for 2 players. 
You will need two separate browsers (like use Chrome for player1, FireFox for player2) if player son the same computer. 
Some data like player's name are stored on your browser local storage.

### Rules
Each player takes turn. Franklin starts the game by writing his script.
A script can not contain more than 3 specials commands.
Franklin speaks Groovy (blue) and Emily is into Scala (red). Programmers can use plain Groovy/Scala in their script.

### Settings
You can change the prefereed language. Both players can play using the same language.

### Help on commmands

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

### General Idea

1. DSL to move the turtle/robot is written in Groovy

2. Grails for Server side Controller part that will do shell.evaluate and return position value for the turtle. 

3. HTML5 Canvas API for displaying turtle moves and positions. We can make it more complex with walls. See little drawing.

4. html5-mobile-scaffolding for the UI. No GSP, only HTML and JS. http://3musket33rs.github.com/ To go further if time permit:

5. push notification: multi players joining a virtual room.

### Where's it all started 

Grails48 in November 2012, it's over but we had great fun!!!
http://www.youtube.com/watch?v=jvVeWHvmG2I&feature=player_embedded

### Developers
Fabrice, Mathieu, Pascal, Martyn, and Corinne are the coders behind it.
