package kissingturtles

import grails.converters.JSON
import dsl.Position
import groovy.json.JsonBuilder
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

class GameService {

    def runFormatting(game, mazeDefinition, turtle, result, ex, idNotification) {
        Position treeInitialPosition = new Position(mazeDefinition.turtles.position.tree1[0], mazeDefinition.turtles.position.tree1[1], 0, '+x')

        def steps = []
        result.steps.each(){
            if (it instanceof String) {
                it = JSON.parse(it)
            }
            steps << [it.x, it.y, it.k]
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
        if (result.meeting) {
            mazeDefinition.turtles.position.tree1[0] = result.meeting[0]
            mazeDefinition.turtles.position.tree1[1] = result.meeting[1]
            obj['tree1'] = [result.meeting];
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
        if (ex) {
            return [
                    images: images,
                    position: obj,
                    grid: 15,
                    stepDuration: 1000,
                    asks: result.asks,
                    win: win,
                    user1: game.user1,
                    user2: game.user2,
                    id: game.id,
                    winningAnimation: [treeInitialPosition.x, treeInitialPosition.y],
                    exception: ex
            ]
        } else {
            return [
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
        
    def runScalaFormatting(game,turtle, ex, idNotification) {
        def mazeDefinition = JSON.parse(game.mazeDefinition)
        Position treeInitialPosition = new Position(mazeDefinition.turtles.position.tree1[0], mazeDefinition.turtles.position.tree1[1], 0, '+x')

        def result = turtle.getNewStepsAsJavaList()
        def scalaAsks = turtle.getNewAsksAsJavaList()
        
        def steps = []
        result.each(){
            steps << [it['x'], it['y']]
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
                asks: scalaAsks,
                win: win,
                user1: game.user1,
                user2: game.user2,
                id: game.id,
                winningAnimation: [treeInitialPosition.x, treeInitialPosition.y],
                exception: ex
        ]
    }

}
