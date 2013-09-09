package kissingturtles

class Game {
    String user1
    String user2
    String user1_language = 'groovy'
    String user2_language = 'scala'

    String mazeDefinition
    Long lastModified

    static constraints = {
        user1 nullable: true
        user2 nullable: true
        mazeDefinition type: 'text', nullable: true, maxSize:70000
    }
}
