package kissingturtles

class Game {
    String user1
    String user2
    String role1
    String role2
    String mazeDefinition

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
    Long lastModified

    static constraints = {
        user1 nullable: true
        user2 nullable: true
        role1 nullable: true
        role2 nullable: true
        mazeDefinition type: 'text', nullable: true, maxSize:70000
        emilyDir nullable: true
    }
}
