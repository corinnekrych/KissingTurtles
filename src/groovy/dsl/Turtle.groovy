package dsl

class Turtle {
    def name
    def image
    def steps = []
    def currentPosition
    def previousPosition
    def result = [:]
    def maze
    def i = 1;
    def asks =[]
    def meeting = []

    UserInteraction userInteraction


    Turtle(myName, myImage, Position start, walls, userInterac) {
        name = myName
        image = myImage
        currentPosition = start
        meeting = []
        result = ['name': name, 'image': image, 'steps': steps, 'asks': asks, 'meeting':meeting]
        maze = walls
        userInteraction = userInterac
    }

    Turtle(Position start) {
        this(null, null, start, null, null)
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
        byDefault(1)
        this
    }

    Turtle byDefault(Integer step) {
        Position newPosition = currentPosition.move(step)

        newPosition = obstacleResolution(newPosition)

        steps.add(newPosition)
        previousPosition = currentPosition
        currentPosition = newPosition

        this
    }

    Turtle by(Integer step) {
        steps.pop()
        currentPosition = previousPosition
        Position newPosition = currentPosition.move(step)

        newPosition = obstacleResolution(newPosition)

        steps.add(newPosition)
        currentPosition = newPosition
        this
    }

     Position obstacleResolution(Position newPosition) {
        def obstacleX = maze.findAll() { it ->
            ((currentPosition.direction == '+x' && currentPosition.x < it[0] && it[0] <= newPosition.x) ||
                    (currentPosition.direction == '-x' && currentPosition.x > it[0] && it[0] >= newPosition.x)) && (currentPosition.y == it[1])
        }
        if (obstacleX) {

            if (currentPosition.direction == '+x') {
                def x = obstacleX.collect() { Math.abs(it[0]) }
                def min = x.min()
                newPosition = new Position(min - 1, currentPosition.y, 90, '+x')
            } else if (currentPosition.direction == '-x') {
                def x = obstacleX.collect() {
                    Math.abs(it[0])
                }
                def min = x.max()
                newPosition = new Position(min + 1, currentPosition.y, -90, '-x')
            }
        }
        def obstacleY = maze.findAll() { it ->
            ((currentPosition.direction == '+y' && currentPosition.y < it[1] && it[1] <= newPosition.y) ||
                    (currentPosition.direction == '-y' && currentPosition.y > it[1] && it[1] >= newPosition.y)) && (currentPosition.x == it[0])
        }
        if (obstacleY) {

            if (currentPosition.direction == '+y') {
                def y = obstacleY.collect() { Math.abs(it[1]) }
                def minY = y.min()
                newPosition = new Position(currentPosition.x, minY - 1, 0, '+y')
            } else if (currentPosition.direction == '-y') {
                def y = obstacleY.collect() { Math.abs(it[1]) }
                def minY = y.max()
                newPosition = new Position(currentPosition.x, minY + 1, 180, '-y')
            }
        }
        newPosition
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

    Turtle kiss() {
        println "<3 <3"
        Position kissingPosition = new Position(currentPosition.x, currentPosition.y, 0, '+x')
        kissingPosition.k = 1
        steps.add(kissingPosition)
        this
    }

    def meet(points) {
        meeting[0] = points.x
        meeting[1] = points.y
        this
    }


}



enum Direction {
    left, right, up, down
}




