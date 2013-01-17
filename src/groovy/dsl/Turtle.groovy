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

    Turtle move(Direction dir) {
        Position newPosition
        if (dir == Direction.left) {
            newPosition = currentPosition.left()
        } else if (dir == Direction.right) {
            newPosition = currentPosition.right()
        } else if (dir == Direction.up) {
            newPosition = currentPosition.up()
        } else if (dir == Direction.down) {
            newPosition = currentPosition.down()
        }
        currentPosition = newPosition
        this
    }

    void by(Integer step) {
        Position newPosition = currentPosition.move(step)
        steps.add(newPosition)
        currentPosition = newPosition
        println "Franklin is moving ${currentPosition} by $step steps"
    }

}
enum Direction {
    left, right, up, down
}




