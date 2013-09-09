package kissingturtles

import grails.converters.JSON
import dsl.Position
import groovy.json.JsonBuilder
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

class GameService {

    def createFormatting(walls, franklinPosition, treePosition, birdPosition) {

        def images = [
                franklin: 'turtle.png',
                emily: 'turtle2.png',
                tree1: 'tree.png',
                bird: 'bird.png'
        ]
        def fObj = []
        fObj << franklinPosition.x
        fObj << franklinPosition.y
        def tObj = []
        tObj << treePosition.x
        tObj << treePosition.y
        def bObj = []
        bObj << birdPosition.x
        bObj << birdPosition.y
        def obj = [
                franklin: fObj,
                tree1: tObj,
                bird: bObj
        ]

        def root = [
                images: images,
                position: obj,
                grid: 15,
                stepDuration: 1000,
        ]

        [ turtles : root, walls: walls]
    }

    def updateFormatting(game) {
        def mazeDefinition = JSON.parse(game.mazeDefinition)
        Position emilyPosition = new Position().random(15, mazeDefinition.walls)
        JSONArray array = new JSONArray()
        array.add(emilyPosition.x)
        array.add(emilyPosition.y)

        mazeDefinition.turtles.position.emily = array
        mazeDefinition
    }

    def commonFormatting(name, game, mazeDefinition, steps, meeting, asks, ex, idNotification) {        

        // Change meeting point
        if (meeting) {
            mazeDefinition.turtles.position.tree1[0] = meeting[0]
            mazeDefinition.turtles.position.tree1[1] = meeting[1]
        }

        // Move birdy
        // Calculate path for Birdy
        def winningPathResolver = new PathHelper(mazeDefinition.walls)
        def birdPosition = [mazeDefinition['turtles']['position']['bird'][0], mazeDefinition['turtles']['position']['bird'][1]]
        def meetPosition  = [mazeDefinition['turtles']['position']['tree1'][0], mazeDefinition['turtles']['position']['tree1'][1]]
        def winningPathForBird = winningPathResolver.findMinPath(birdPosition,meetPosition).steps
        
        def randomMe = new Random()
        def randomNumberOfMoves = randomMe.nextInt(10) + 1 // max of 11 steps in a raw
        def birdMoves = []
        def lost = false
        for (int i =0; i < randomNumberOfMoves; i++) {
            if (winningPathForBird[i]) {
                birdMoves << winningPathForBird[i]
            }  else {
                lost = true
            }
        }

        // update new position for Birdy
        if (birdMoves) {
            def lastMove = birdMoves.last()
            JSONArray array = new JSONArray()
            array.add(lastMove[0])
            array.add(lastMove[1])
            mazeDefinition.turtles.position.bird = array
        }
        
        def obj
        
        if (name == "emily") {
            obj = [
                    emily: steps,
                    bird: birdMoves
            ]
        } else {
            obj = [
                    franklin: steps,
                    bird: birdMoves
            ]
        }
        if (meeting) {
            obj['tree1'] = [meeting];
        }

        def last = steps.size() == 0 ? null :  steps.last()
        boolean win = false
        if (last) {
            if (name == "franklin") {
                if ((last[0] == mazeDefinition.turtles.position.tree1[0]) && (last[1] == mazeDefinition.turtles.position.tree1[1]) && (mazeDefinition.turtles.position.emily[0] == mazeDefinition.turtles.position.tree1[0]) && (mazeDefinition.turtles.position.emily[1] == mazeDefinition.turtles.position.tree1[1])) {
                    win = true
                }
                JSONArray array = new JSONArray()
                array.add(last[0])
                array.add(last[1])
                mazeDefinition.turtles.position.franklin = array
            } else if (name == "emily") {
                if ((last[0] == mazeDefinition.turtles.position.tree1[0]) && (last[1] == mazeDefinition.turtles.position.tree1[1]) && (mazeDefinition.turtles.position.franklin[0] == mazeDefinition.turtles.position.tree1[0]) && (mazeDefinition.turtles.position.franklin[1] == mazeDefinition.turtles.position.tree1[1])) {
                    win = true
                }
                JSONArray array = new JSONArray()
                array.add(last[0])
                array.add(last[1])
                mazeDefinition.turtles.position.emily = array
            }
        }

        game.mazeDefinition = mazeDefinition

        def images = [
                franklin: 'turtle.png',
                emily: 'turtle2.png',
                tree1: 'tree.png'
        ]
        if (ex) {
            return [
                    images: images,
                    position: obj,
                    grid: 15,
                    stepDuration: 1000,
                    asks: asks,
                    win: win,
                    lost: lost,
                    user1: game.user1,
                    user2: game.user2,
                    id: game.id,
                    winningAnimation: [mazeDefinition.turtles.position.tree1[0], mazeDefinition.turtles.position.tree1[1]],
                    exception: ex
            ]
        } else {
            return [
                    images: images,
                    position: obj,
                    grid: 15,
                    stepDuration: 1000,
                    asks: asks,
                    win: win,
                    lost: lost,
                    user1: game.user1,
                    user2: game.user2,
                    id: game.id,
                    winningAnimation: [mazeDefinition.turtles.position.tree1[0], mazeDefinition.turtles.position.tree1[1]]
            ]
        }
    }
    
    def runFormatting(game, mazeDefinition, turtle, result, ex, idNotification) {

        def steps = []
        result.steps.each(){
            if (it instanceof String) {
                it = JSON.parse(it)
            }
            steps << [it.x, it.y, it.k]
        }

        return commonFormatting(turtle.name, game, mazeDefinition, steps, result.meeting,result.asks, ex,idNotification)        
    }
        
    def runScalaFormatting(game, mazeDefinition, turtle, ex, idNotification) {

        def result = turtle.getNewStepsAsJavaList()
        def scalaAsks = turtle.getNewAsksAsJavaList()
        
        def steps = []
         result.each(){
              steps << [it['x'], it['y'],0]
         } 
        
        def meeting_ = turtle.getMeetPointAsJavaMap()
        def meeting = null
        if (meeting_.keySet().size() > 0) {
           meeting = []
           meeting[0] = meeting_['x']
           meeting[1] = meeting_['y']
        }

        return commonFormatting(turtle.name, game, mazeDefinition, steps,meeting,scalaAsks, ex,idNotification)        
    }

}
