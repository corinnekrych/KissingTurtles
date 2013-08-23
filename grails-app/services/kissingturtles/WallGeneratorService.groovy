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
                    def coordinates =[]
                    coordinates
                    myWalls << [j*2, i*2]
                    myWalls << [j*2+1, i*2]
                    myWalls << [j*2+2, i*2]
                }
            }
            // draw the west edge
            for (int j = 0; j < gridSize; j++) {
                if((maze.maze[j][i] & 8) == 0) {
                    myWalls << [j*2, i*2]
                    myWalls << [j*2, i*2+1]
                    myWalls << [j*2, i*2+2]
                }
            }
            myWalls << [14, i*2]
            myWalls << [14, i*2+1]
            myWalls << [14, i*2+2]
        }

        for (int j = 0; j < gridSize; j++) {
            myWalls << [j*2, 14]
            myWalls << [j*2+1, 14]
            myWalls << [j*2+2, 14]
        }

        myWalls
    }
}
