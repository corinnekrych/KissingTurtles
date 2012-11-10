package dsl

class Position {
    int x
    int y
    int rotation

    Position move(Direction dir, int step) {
        def newRotation = rotation
        if (dir == Direction.backward) {
            newRotation = (rotation + 180) % 360
        } else if (dir == Direction.left) {
            newRotation = (rotation - 90) % 360
        } else if (dir == Direction.right) {
            newRotation = (rotation + 90) % 360
        }

        if ((newRotation % 360) == 0) {
            return new Position(x, y + step, newRotation);
        } else if ((rotation % 360) == 90) {
            return new Position(x + step, y, newRotation);
        } else if ((rotation % 360) == 180) {
            return new Position(x, y - step, newRotation);
        } else if ((rotation % 360) == 270) {
            return new Position(x - step, y, newRotation);
        }

    }

    Position left() {
        new Position(x, y, rotation - 90);
    }

    Position right() {
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