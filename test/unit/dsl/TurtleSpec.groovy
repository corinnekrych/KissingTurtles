package dsl

import spock.lang.Specification

class TurtleSpec extends Specification {

    def turtle

    void 'turtle moves right without by, move +1 by default'() {
        given:
        Position position = new Position()
        position.x = 1
        position.y = 1
        turtle = new Turtle(position)


        when:
        turtle = turtle.move(Direction.right)

        then:
        turtle.currentPosition.x == 2
        turtle.currentPosition.y == 1
    }

    void 'turtle moves right by 5, current position is x + 5'() {
        given:
        Position position = new Position()
        position.x = 1
        position.y = 1
        turtle = new Turtle(position)


        when:
        turtle = turtle.move(Direction.right).by(5)

        then:
        turtle.currentPosition.x == 6
        turtle.currentPosition.y == 1
    }
}