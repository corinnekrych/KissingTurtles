package dsl

class Position {
    int x
    int y
    int rotation

    Position move(Direction dir, int step) {
        def newRotation = rotation
        def newPosition = new Position(x, y, rotation)
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

        if (((newRotation % 360) == 0) || ((newRotation % 360) == -360)) {
            newPosition = new Position(x, y + step, newRotation);
        } else if (((newRotation % 360) == 90) || ((newRotation % 360) == -270)) {
            newPosition = new Position(x + step, y, newRotation);
        } else if (((newRotation % 360) == 180) || ((newRotation % 360) == -180)) {
            newPosition = new Position(x, y - step, newRotation);
        } else if (((newRotation % 360) == 270) || ((newRotation % 360) == -90)) {
            newPosition = new Position(x - step, y, newRotation);
        }
        newPosition
    }

    Position left() {
        println "left"
        new Position(x, y, rotation - 90);
    }

    Position right() {
        println "right"
        new Position(x, y, rotation + 90);
    }

    String toString() {
        "x: $x, y: $y, rotation: $rotation"
    }

    def Position(moveX, moveY, rot) {
        x = moveX
        y = moveY
        rotation = rot
    }
}