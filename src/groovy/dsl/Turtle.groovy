package dsl

import kissingturtles.WallGeneratorService

class Turtle {
    def name
    def image
    def steps = []
    def currentPosition
    def result = [:]
    def maze

    Turtle(myName, myImage, Position start, mazeId) {
        name = myName
        image = myImage
        steps = []
        currentPosition = start
        result = ['name': name, 'image': image, 'steps': steps]
        maze = new WallGeneratorService().getWalls(mazeId)
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


    Turtle by(Integer step) {
        Position newPosition = currentPosition.move(step)

        def obstacleX = maze.findAll() {it ->
           ((currentPosition.direction == '+x' && currentPosition.x < it.x && it.x <= newPosition.x) ||
            (currentPosition.direction == '-x' && currentPosition.x > it.x && it.x >= newPosition.x)) && (currentPosition.y == it.y)
        }
        if (obstacleX) {
            def x = obstacleX.collect() { Math.abs(it.x) }
            def min = x.min()
            if (currentPosition.direction == '+x') {
              newPosition = new Position(min - 1, currentPosition.y, 90, '+x')
            } else if (currentPosition.direction == '-x') {
              newPosition = new Position(min + 1, currentPosition.y, -90, '-x')
            }
        }
        def obstacleY = maze.findAll() {it -> ((currentPosition.direction == '+y' && currentPosition.y < it.y && it.y <= newPosition.y)||
                                               (currentPosition.direction == '-y' && currentPosition.y > it.y && it.y >= newPosition.y)) && (currentPosition.x == it.x)}
        if (obstacleY) {
            def y = obstacleY.collect() { Math.abs(it.y) }
            def minY = y.min()
            if (currentPosition.direction == '+y') {
                newPosition = new Position(currentPosition.x, minY - 1, 0, '+y')
            } else if (currentPosition.direction == '-y') {
                newPosition = new Position(currentPosition.x, minY + 1, 180, '-y')
            }
        }

        steps.add(newPosition)
        currentPosition = newPosition
        this
    }

}
enum Direction {
    left, right, up, down
}




