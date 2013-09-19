## KissingTurtles Game

### Goal

Franklin is lost in the maze, help him to meet Emily at the Heart before Birdy reaches the heart.
Franklin speaks Groovy and emily is into Scala. Both lang
Using a special commands, you can move, change the meeting point, ask question to your partner.

### Requirements
KissingTurtles is a collaborative game for 2 players. 
You will need need to separate browsers if player on the same computer. 
Some data like game palyer are stored on your browser local storage.

### Rules
Each player takes turn. Franklin start the game by writing his script.
A script can not contain more than 3 specials commands.
Franklin speaks Groovy (blue) and Emily is into Scala (red). Programmers can use plain Groovy/Scala in their script.

### Help on commmands
```java
move up
move left by 2
kiss
ask "Change meeting?" assign to response 
if(response == "y") meet x:12, y:1 
```

```java
I move up
I move left by 2
I kiss;
val response = I ask "Change meeting?"
if(response == "y") meet (12, 1) 
```

## General Idea
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

Mathieu, Fabrice, Martyn, Pascal and Corinne are the coders behind it.
