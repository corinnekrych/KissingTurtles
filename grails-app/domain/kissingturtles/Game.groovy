package kissingturtles

class Game {

    static constraints = {
        user1 nullable: true
        user2 nullable: true
        mazeDefinition nullable: true
    }

    User user1
    User user2
    String mazeDefinition
}
