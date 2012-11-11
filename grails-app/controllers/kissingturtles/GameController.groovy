package kissingturtles


import grails.converters.JSON
import grails.validation.ValidationErrors

import org.codehaus.groovy.grails.web.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException

import dsl.DslScript
import dsl.Turtle
import dsl.Position

class GameController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def run() {
        println "in the inputs" + params

        def game = Game.findById(params.gameId)
        Position franklinInitialPosition = new Position(game.fX, game.fY, game.fRot, game.fDir)
        Position treeInitialPosition = new Position(game.tX, game.tY, game.tRot, game.tDir)

        def scriptInstance = new DslScript(params)
        def script = scriptInstance.content
        def turtle = new Turtle("franklin", "image", franklinInitialPosition)

        def binding = new Binding([
                turtle: turtle,
                left: dsl.Direction.left,
                right: dsl.Direction.right,
                backward: dsl.Direction.backward,
                forward: dsl.Direction.forward,
                turn: turtle.&turn,
                move: turtle.&move,
                kiss:  turtle.&kiss
        ])
        def shell = new GroovyShell(binding)
        shell.evaluate(script)
        def result = binding.getVariable('turtle').result //as JSON

        def builder = new groovy.json.JsonBuilder()

        def array = []
        result.steps.each(){
            def obj = new LinkedHashMap()
            def tempFrank = it as JSON
            obj.franklin = tempFrank['target']
            def tempTree = treeInitialPosition as JSON
            obj.tree1 = tempTree['target']
            array.add(obj)
        }

        def root = builder.configuration {
            images {
                franklin 'turtle.png'
                emily 'turtle.png'
                tree1 'tree.png'
            }
            steps array
            grid 15
            stepDuration 1000
        }
        def conf = builder.toString()

        println conf
        render conf
    }

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        render Game.list([fetch: [user1: 'eager', user2: 'eager']]) as JSON
    }
//    that.randomEmilyX=Math.floor(Math.random()*15);
//    that.randomEmilyY=Math.floor(Math.random()*15);
//    ktMaze(document.getElementById('canvas'), {
//        images: {
//            franklin: 'turtle.png',
//            emily: 'turtle.png',
//            tree1: 'tree.png'
//        },
//        //winningAnimation: { x: that.randomEmilyX, y: that.randomEmilyY },
//        steps: [{
//            franklin: { x: 0, y: 0, direction: '+x' },
//            emily: { x: that.randomEmilyX, y: that.randomEmilyY, direction: '-y' },
//            tree1: { x: 14, y: 14 }
//        }],
//        grid: 15,
//        stepDuration: 1000
//    }
    def save() {
        JSONObject jsonObject = JSON.parse(params.game)
        String mazeDefinition = "";
        //MazeService.createMaze();
        Position franklinPosition = new Position().random(15)
        Position treePosition = new Position().random(15)
        def builder = new groovy.json.JsonBuilder()

        def obj = new LinkedHashMap()
        def tempFrank = franklinPosition as JSON
        obj.franklin = tempFrank['target']
        def tempTree = treePosition as JSON
        obj.tree1 = tempTree['target']
        def array = [obj]
        def root = builder.configuration {
            images {
                franklin 'turtle.png'
                emily 'turtle.png'
                tree1 'tree.png'
            }
            steps array
            grid 15
            stepDuration 1000
        }
        def conf = builder.toString()
        mazeDefinition = conf
        //end integration with maze
        Game gameInstance = new Game()
        gameInstance.user1 = User.findById(jsonObject.entrySet().iterator().next().value)
        gameInstance.mazeDefinition = mazeDefinition
        // save initial position
        gameInstance.fX = franklinPosition.x
        gameInstance.fY = franklinPosition.y
        gameInstance.fRot = franklinPosition.rotation
        gameInstance.fDir = franklinPosition.direction
        gameInstance.tX = treePosition.x
        gameInstance.tY = treePosition.y
        gameInstance.tRot = treePosition.rotation
        gameInstance.tDir = treePosition.direction
        if (!gameInstance.save(flush: true)) {
            ValidationErrors validationErrors = gameInstance.errors
            render validationErrors as JSON
        }
        render gameInstance as JSON
    }

    def show() {
        def gameInstance = Game.get(params.id)
        if (!gameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'game.label', default: 'Game'), params.id])
            render flash as JSON
        }
        render GameInstance as JSON
    }

    def update() {
        def jsonObject = JSON.parse(params.game)

        Game gameReceived = new Game(jsonObject)

        def gameInstance = Game.get(jsonObject.id)
        if (!gameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'game.label', default: 'Game'), params.id])
            render flash as JSON
        }

        if (jsonObject.version) {
            def version = jsonObject.version.toLong()
            if (gameInstance.version > version) {
                gameInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'game.label', default: 'Game')] as Object[],
                        "Another user has updated this Game while you were editing")
                ValidationErrors validationErrors = gameInstance.errors
                render validationErrors as JSON
                return
            }
        }

        gameInstance.properties = gameReceived.properties

        if (!gameInstance.save(flush: true)) {
            ValidationErrors validationErrors = gameInstance.errors
            render validationErrors as JSON
        }
        render gameInstance as JSON
    }

    def delete() {
        def gameId = params.id
        def gameInstance = Game.get(params.id)
        if (!gameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'game.label', default: 'Game'), params.id])
            render flash as JSON
        }
        try {
            gameInstance.delete(flush: true)
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'game.label', default: 'Game'), params.id])
            render flash as JSON
        }
        render gameInstance as JSON
    }
}
