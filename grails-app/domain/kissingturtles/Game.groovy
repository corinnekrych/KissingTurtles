package kissingturtles

import dsl.Position

class Game {
    User user1
    User user2
    String mazeDefinition

    int fX
    int fY
    int fRot
    String fDir

    int tX
    int tY
    int tRot
    String tDir

//    Position franklin
//    Position tree
//    Position emily

    static constraints = {
        user1 nullable: true
        user2 nullable: true
        mazeDefinition type: 'text', nullable: true, maxSize: 15000
//        franklin nullable: true
//        tree nullable: true
//        emily nullable: true
    }
}
