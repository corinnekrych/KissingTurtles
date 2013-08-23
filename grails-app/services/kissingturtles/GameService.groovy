package kissingturtles

import grails.converters.JSON
import dsl.Position
import groovy.json.JsonBuilder
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

class GameService {

    def runFormatting(game, mazeDefinition, turtle, result, idNotification) {
        Position treeInitialPosition = new Position(mazeDefinition.turtles.position.tree1[0], mazeDefinition.turtles.position.tree1[1], 0, '+x')

        def steps = []
        result.steps.each(){
            if (it instanceof String) {
                it = JSON.parse(it)
            }
            steps << [it.x, it.y]
        }

        def obj
        if (turtle.name == "emily") {
            obj = [
                    emily: steps
            ]
        } else {
            obj = [
                    franklin: steps
            ]
        }

        def last = steps.size() == 0 ? null :  steps.last()
        boolean win = false
        if (last) {
            if (turtle.name == "franklin") {
                if ((last[0] == treeInitialPosition.x) && (last[1] == treeInitialPosition.y) && (mazeDefinition.turtles.position.emily[0] == treeInitialPosition.x) && (mazeDefinition.turtles.position.emily[1] == treeInitialPosition.y)) {
                    win = true
                }
                JSONArray array = new JSONArray()
                array.add(last[0])
                array.add(last[1])
                mazeDefinition.turtles.position.franklin = array
            } else if (turtle.name == "emily") {
                if ((last[0] == treeInitialPosition.x) && (last[1] == treeInitialPosition.y) && (mazeDefinition.turtles.position.franklin[0] == treeInitialPosition.x) && (mazeDefinition.turtles.position.franklin[1] == treeInitialPosition.y)) {
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

        [
                images: images,
                position: obj,
                grid: 15,
                stepDuration: 1000,
                asks: result.asks,
                win: win,
                user1: game.user1,
                user2: game.user2,
                id: game.id,
                winningAnimation: [treeInitialPosition.x, treeInitialPosition.y]
        ]
    }

    def createFormatting(walls, franklinPosition, treePosition) {

        def images = [
                franklin: 'turtle.png',
                emily: 'turtle2.png',
                tree1: 'tree.png'
        ]
        def fObj = []
        fObj << franklinPosition.x
        fObj << franklinPosition.y
        def tObj = []
        tObj << treePosition.x
        tObj << treePosition.y
        def obj = [
                franklin: fObj,
                tree1: tObj
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

    def runScalaFormatting(game, turtle, idNotification) {
        Position treeInitialPosition = new Position(game.treeX, game.treeY, game.treeRot, game.treeDir)

        def builder = new groovy.json.JsonBuilder()

        /* todo in Scala
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
            
            def asksArray = []
        }*/

        def result = turtle.getNewStepsAsJavaMap()['steps']

        def array = []
        result.each(){
            def obj = new LinkedHashMap()
            def temp = it as JSON
            if (turtle.name == "franklin") {
                obj.franklin = temp['target']
            } else if (turtle.name == "emily") {
                obj.emily = temp['target']
            }
            array.add(obj)
        }

        println("Array is ===== "+array)

        def asksArray = turtle.getNewAsksAsJavaList()
        println("Ask Array is ===== "+asksArray)

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
            asks asksArray
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

}
