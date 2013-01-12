package dsl

/**
 * Output should be like specified by Mathieu
 * [{ name: 'turtle1', image: 'turtle.png', steps: [{ x: 1, y: 4, rotation: 0},{ x: 2, y: 4, rotation: 0}, ...]}]
 */
class Turtle {
    def name
    def image
    def steps = []
    def currentPosition
    def result = [:]

    Turtle(myName, myImage, Position start) {
        name = myName
        image = myImage
        steps = []
        currentPosition = start
        result = ['name': name, 'image': image, 'steps': steps]
    }

    void turn(Direction dir) {
        Position newPosition
        if (dir == Direction.left) {
            newPosition = currentPosition.left()
        } else if (dir == Direction.right) {
            newPosition = currentPosition.right()
        }
        steps.add(newPosition)
        currentPosition = newPosition
        println "Franklin is turning $dir"
    }

    void move(Direction dir, int step) {
        Position newPosition = currentPosition.move(dir, step)
        steps.add(newPosition)
        currentPosition = newPosition
        println "Franklin is moving $dir by $step steps"
    }

    void moveForward(int step) {
        Position newPosition = currentPosition.move(Direction.forward, step)
        steps.add(newPosition)
        currentPosition = newPosition
        println "Franklin is moving forward by $step steps"
    }

    void kiss() {
        String pathSummary= ''
        steps.eachWithIndex { it, index ->
            pathSummary += "$index: ${it.toString()},"
        }
        println "Franklin has just stopped to kiss his true love. His position is: $pathSummary"
    }
}
enum Direction {
    left, right, forward, backward
}




