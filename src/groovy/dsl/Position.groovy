package dsl

class Position {
    int x
    int y
    int rotation
    String direction

    Position move(Direction dir, int step) {
        def newRotation = rotation
        if (dir.equals(Direction.backward)) {
            newRotation = (rotation + 180) % 360
            println "Back"
        } else if (dir.equals(Direction.left)) {
            newRotation = (rotation - 90) % 360
            println "Left"
        } else if (dir.equals(Direction.right)) {
            println "Right"
            newRotation = (rotation + 90) % 360
        }

        def newDirection = whichDirection(newRotation)
        def newPosition
        if (((newRotation % 360) == 0) || ((newRotation % 360) == -360)) {
            println "y+step = " + y+step
            newPosition = new Position(x, y + step, newRotation, newDirection);
        } else if (((newRotation % 360) == 90) || ((newRotation % 360) == -270)) {
            newPosition = new Position(x + step, y, newRotation, newDirection);
        } else if (((newRotation % 360) == 180) || ((newRotation % 360) == -180)) {
            newPosition = new Position(x, y - step, newRotation, newDirection);
        } else if (((newRotation % 360) == 270) || ((newRotation % 360) == -90)) {
            newPosition = new Position(x - step, y, newRotation, newDirection);
        }
        newPosition
    }

    Position left() {
        println "left"
        def newRotation = rotation - 90;
        def newDirection = whichDirection(newRotation)
        new Position(x, y, newRotation, newDirection);
    }

    Position right() {
        println "right"
        def newRotation = rotation + 90;
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