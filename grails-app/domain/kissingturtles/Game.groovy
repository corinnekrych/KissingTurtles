package kissingturtles

class Game {
    String user1
    String user2
    String mazeDefinition
    int mazeTTT

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

    static constraints = {
        user1 nullable: true
        user2 nullable: true
        mazeDefinition type: 'text', nullable: true, maxSize: 15000
        emilyDir nullable: true
    }
}
