package kissingturtles

import dsl.Position

class WallGeneratorService {

    def getWalls() {
        def gridSize = 7

        MazeGenerator maze = new MazeGenerator(gridSize, gridSize);

        def myWalls = [] as Set
        for (int i = 0; i < gridSize; i++) {
            // draw the north edge
            for (int j = 0; j < gridSize; j++) {
                if((maze.maze[j][i] & 1) == 0) {
                    myWalls << new Position(j*2, i*2, 90, '+x')
                    myWalls << new Position(j*2+1, i*2, 90, '+x')
                    myWalls << new Position(j*2+2, i*2, 90, '+x')
                }
            }
            // draw the west edge
            for (int j = 0; j < gridSize; j++) {
                if((maze.maze[j][i] & 8) == 0) {
                    myWalls << new Position(j*2, i*2, 90, '+x')
                    myWalls << new Position(j*2, i*2+1, 90, '+x')
                    myWalls << new Position(j*2, i*2+2, 90, '+x')
                }
            }
            myWalls << new Position(14, i*2, 90, '+x')
            myWalls << new Position(14, i*2+1, 90, '+x')
            myWalls << new Position(14, i*2+2, 90, '+x')
        }

        for (int j = 0; j < gridSize; j++) {
            myWalls << new Position(j*2, 14, 90, '+x')
            myWalls << new Position(j*2+1, 14, 90, '+x')
            myWalls << new Position(j*2+2, 14, 90, '+x')
        }

        myWalls
    }
}
