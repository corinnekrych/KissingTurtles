package kissingturtles

import dsl.Position

class Game {
    String user1
    String user2
    String mazeDefinition
    def maze

    int franklinX
    int franklinY
    int franklinRot
    String franklinDir

    int treeX
    int treeY
    int treeRot
    String treeDir

    int emilyX
    int emilyY
    int emilyRot
    String emilyDir

//    Position franklin
//    Position tree
//    Position emily

    static constraints = {
        user1 nullable: true
        user2 nullable: true
        mazeDefinition type: 'text', nullable: true, maxSize: 15000
        emilyDir nullable: true
//        franklin nullable: true
//        tree nullable: true
//        emily nullable: true
    }
}
