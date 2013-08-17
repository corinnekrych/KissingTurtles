package dsl

class Position {
    int x
    int y
    int rotation
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

    Position random(int gridSize, walls) {
        def randomMe = new Random()
        def randomX = randomMe.nextInt(gridSize-2)
        def randomY = randomMe.nextInt(gridSize-2)
        x = 1 + randomX
        y = 1 + randomY
        rotation = 90
        direction = '+x'
        def isOnWalls = walls.find() {
            it.x == x && it.y == y
        }
        if (isOnWalls != null) {
          random(gridSize, walls)
        }
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