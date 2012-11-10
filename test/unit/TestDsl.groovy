import dsl.Turtle
import org.junit.Test

import static dsl.Direction.left
import static dsl.Direction.*

/**
 * Look at
 * http://groovy.codeplex.com/wikipage?title=Guillaume%20Laforge%27s%20%22Mars%20Rover%22%20tutorial%20on%20Groovy%20DSL%27s
 */

class TestDsl {

    @Test
    void testMoveLeft() {
        Turtle turtle = new Turtle()
        turtle.turn left
    }

    @Test
    void testWithoutNew() {
        def binding = new Binding([
                turtle: new Turtle(),
                left:     dsl.Direction.left,
                right:    dsl.Direction.right,
                backward: dsl.Direction.backward,
                forward:  dsl.Direction.forward

        ])
        def shell = new GroovyShell(binding)
        shell.evaluate("turtle.turn left")
    }

    @Test
    void testWithoutTurtle() {
        def turtle= new Turtle()
        def binding = new Binding([
                turtle: turtle,
                left:     dsl.Direction.left,
                right:    dsl.Direction.right,
                backward: dsl.Direction.backward,
                forward:  dsl.Direction.forward,
                turn:  turtle.&turn

        ])
        def shell = new GroovyShell(binding)
        shell.evaluate("turn left")
    }
}


