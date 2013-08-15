package kissingturtles

import grails.converters.JSON
import dsl.Position
import org.codehaus.groovy.grails.web.json.JSONObject

class GameService {

    def runFormatting(game, turtle, result, idNotification) {
        Position treeInitialPosition = new Position(game.treeX, game.treeY, game.treeRot, game.treeDir)

        def builder = new groovy.json.JsonBuilder()

        def array = []
        result.steps.each(){
            def obj = new LinkedHashMap()
            def temp = it as JSON
            if (turtle.name == "franklin") {
                obj.franklin = temp['target']
            } else if (turtle.name == "emily") {
                obj.emily = temp['target']
            }
            array.add(obj)
        }

        def last = array.size() == 0 ? null :  array.last()
        boolean win = false
        if (last) {
            if (turtle.name == "franklin") {
                if ((last.franklin.x == treeInitialPosition.x) && (last.franklin.y == treeInitialPosition.y) && (game.emilyX == treeInitialPosition.x) && (game.emilyY == treeInitialPosition.y)) {
                    win = true
                }
                game.franklinX = last.franklin.x
                game.franklinY = last.franklin.y
                game.franklinRot = last.franklin.rotation
                game.franklinDir = last.franklin.direction
            } else if (turtle.name == "emily") {
                if ((last.emily.x == treeInitialPosition.x) && (last.emily.y == treeInitialPosition.y) && (game.franklinX == treeInitialPosition.x) && (game.franklinY == treeInitialPosition.y)) {
                    win = true
                }
                game.emilyX = last.emily.x
                game.emilyY = last.emily.y
                game.emilyRot = last.emily.rotation
                game.emilyDir = last.emily.direction
            }
        }
        def root = builder.configuration {
            images {
                franklin 'turtle.png'
                emily 'turtle2.png'
                tree1 'tree.png'
            }
            (win)? winningAnimation {x treeInitialPosition.x
                y treeInitialPosition.y}:""
            steps array
            asks result.asks
            grid 15
            stepDuration 1000
            role1 game.role1
            user1 game.user1
            user2 game.user2
            role2 game.role2
            id game.id
            userIdNotification idNotification
        }

        def conf = builder.toString()
    }

    def createFormatting(walls, franklinPosition, treePosition) {
        //TODO generate positions after wall generation and exclude wall places
        def builder = new groovy.json.JsonBuilder()

        def images = [
                franklin: 'turtle.png',
                emily: 'turtle2.png',
                tree1: 'tree.png'
        ]
        def obj = [
                franklin: (franklinPosition as JSON)['target'],
                tree1: (treePosition as JSON)['target']
        ]

        def flowerNumber = 8;
        def rnd = new Random()
        walls.eachWithIndex { w, idx ->
            def name = "wall${idx}"
            images[name] = "flower${rnd.nextInt(flowerNumber) + 1}.png"
            obj[name] = (w as JSON)['target']
        }

        def root = [
                images: images,
                steps: [obj],
                grid: 15,
                stepDuration: 1000
        ]
        String mazeDefinition = (root as JSON).toString()
    }

    def updateFormatting(game, emilyPosition) {
        Position franklinPosition = new Position(game.franklinX, game.franklinY, game.franklinRot, game.franklinDir)

        JSONObject json = JSON.parse(game.mazeDefinition)
        def images = json["images"]
        images["emily"] = 'turtle2.png'
        def obj = json['steps'][0]
        obj['franklin'] = (franklinPosition as JSON)['target']
        obj['emily'] = ( emilyPosition as JSON)['target']

        def root = [
                images: images,
                steps: [obj],
                grid: 15,
                stepDuration: 1000
        ]
       (root as JSON).toString()

    }
}
