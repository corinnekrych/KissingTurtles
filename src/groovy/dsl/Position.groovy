package dsl

class Position {
    Integer x
    Integer y
    Integer rotation
    String direction

    Position move(Integer step) {
        Position newPosition
        if (direction == '+y') {
            println "y+step = " + y+step
            newPosition = new Position(x, y + step, rotation, direction)
        } else if (direction == '+x') {
            newPosition = new Position(x + step, y, rotation, direction)
        } else if (direction == '-y') {
            newPosition = new Position(x, y - step, rotation, direction)
        } else if (direction == '-x') {
            newPosition = new Position(x - step, y, rotation, direction)
        }
        newPosition
    }

    Position left() {
        println "left"
        def newRotation = - 90;
        def newDirection = whichDirection(newRotation)
        new Position(x, y, newRotation, newDirection);
    }

    Position right() {
        println "right"
        def newRotation = 90;
        def newDirection = whichDirection(newRotation)
        new Position(x, y, newRotation, newDirection);
    }

    Position up() {
        println "up"
        def newRotation = 0;
        def newDirection = whichDirection(newRotation)
        new Position(x, y, newRotation, newDirection);
    }

    Position down() {
        println "down"
        def newRotation = 180;
        def newDirection = whichDirection(newRotation)
        new Position(x, y, newRotation, newDirection);
    }

    String whichDirection(newRotation) {
        def newDirection
        if (((newRotation % 360) == 0) || ((newRotation % 360) == -360)) {
            newDirection = '+y';
        } else if (((newRotation % 360) == 90) || ((newRotation % 360) == -270)) {
            newDirection = '+x';
        } else if (((newRotation % 360) == 180) || ((newRotation % 360) == -180)) {
            newDirection = '-y';
        } else if (((newRotation % 360) == 270) || ((newRotation % 360) == -90)) {
            newDirection = '-x';
        }
        newDirection
    }

    Position random(int gridSize) {
        def random = new Random()
        def randomX = random.nextInt(gridSize)
        def randomY = random.nextInt(gridSize)
        x = randomX
        y = randomY
        rotation = 90
        direction = '+x'
        this
    }

    String toString() {
        "x: $x, y: $y, rotation: $rotation, direction: $direction"
    }

    def Position(moveX, moveY, rot, dir) {
        x = moveX
        y = moveY
        rotation = rot
        direction = dir
    }

    def Position() {
        x = 0
        y = 0
        rotation = 90
        direction = '+x'
    }
}