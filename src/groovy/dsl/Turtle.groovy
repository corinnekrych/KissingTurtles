package dsl

import kissingturtles.WallGeneratorService

class Turtle {
    def name
    def image
    def steps = []
    def currentPosition
    def result = [:]
    def maze
    def i = 1;
    def asks =[]

    UserInteraction userInteraction

    Turtle(myName, myImage, Position start, mazeId, userInterac) {
        name = myName
        image = myImage
        currentPosition = start
        result = ['name': name, 'image': image, 'steps': steps, 'asks': asks]
        maze = new WallGeneratorService().getWalls(mazeId)
        userInteraction = userInterac
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

    def ask(question) {
        def myQuestion = question
        [assign : { to ->
            [:].withDefault {variable ->
                def myAsk = [:]
                myAsk["_question"] = myQuestion
                def response = userInteraction.waitForAnswer(myQuestion)
                myAsk[variable] = response
                asks.add(myAsk)
                userInteraction.gameController.binding.setVariable(variable, response)
            }
        }]
    }
    def propertyMissing(def propertyName) {
        propertyName
    }


}
enum Direction {
    left, right, up, down
}




