package dsl

/**
 * Output should be like specified by Mathieu
 * [{ name: 'turtle1', image: 'turtle.png', steps: [{ x: 1, y: 4, rotation: 0},{ x: 2, y: 4, rotation: 0}, ...]}]
 */
class Turtle {
    def name
    def image
    def steps = []
    def result = [:]

    def Turtle(myName, myImage) {
        name = myName
        image = myImage
        steps = [new Position(0, 0, 0)]
        result = ['name': name, 'image': image, 'steps': steps]
    }

    void turn(Direction dir) {
        Position previousPosition = steps[steps.size() - 1]
        Position newPosition
        if (dir == Direction.left) {
            newPosition = previousPosition.left()
        } else if (dir == Direction.right) {
            newPosition = previousPosition.right()
        }
        result['steps'].add(newPosition)
        println "Franklin is turning $dir"
    }

    Turtle move(Direction dir, int step) {
        Position previousPosition = steps[steps.size() - 1]
        Position newPosition = previousPosition.move(dir, step)
        result['steps'].add(newPosition)
        println "Franklin is moving $dir by $step steps"
    }

    void kiss() {
        String pathSummary
        result['steps'].eachWithIndex { it, index ->
            pathSummary += "$index: {$it},"
        }
        println "Franklin has just stopped to kiss his true love. His position is: " pathSummary
    }

    def propertyMissing(String name) {
    }
}
enum Direction {
    left, right, forward, backward
}




