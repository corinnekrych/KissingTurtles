package kissingturtles

class Game {
    User user1
    User user2
    String mazeDefinition

    static constraints = {
        user2 nullable: true
        mazeDefinition nullable: true
    }
}
