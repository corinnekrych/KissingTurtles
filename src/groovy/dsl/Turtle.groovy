package dsl

class Turtle {

    void turn(Direction dir) {
        println "Turn $dir"
    }

    Turtle move(Direction dir, Integer steps) {
        println "Move $dir $steps steps"
        return this;
    }

    void kiss() {

    }

    def propertyMissing(String name) {
    }
}
enum Direction {
    left, right, forward, backward
}