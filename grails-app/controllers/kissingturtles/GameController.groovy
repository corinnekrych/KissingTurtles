package kissingturtles

import java.util.Random

import grails.converters.JSON
import grails.validation.ValidationErrors

import org.codehaus.groovy.grails.web.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException

import dsl.DslScript
import dsl.Turtle
import dsl.Position

class GameController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    // A maze is simply a function that returns the list of wall positions
    static mazes = [
        { grid -> ((grid/4)..(grid + 1 - grid/4)).collect { new Position(it, grid/2, 90, '+x') } },// horizontal line
        { grid -> ((grid/4)..(grid + 1 - grid/4)).collectMany { [new Position(it, grid/2, 90, '+x'), new Position(grid/2, it, 90, '+x')] } },// cross
        { grid -> def r = new Random(); (0..grid).collect { new Position(r.nextInt(grid), r.nextInt(grid), 90, '+x') } },// random blocks
        { grid -> def g = grid/5; (0..g).collectMany { [
            new Position(g, g + it, 90, '+x'),
            new Position(g + it, g, 90, '+x'),
            new Position(grid - g, g + it, 90, '+x'),
            new Position(grid - g - it, g, 90, '+x'),
            new Position(g, grid - g - it, 90, '+x'),
            new Position(g + it, grid -g, 90, '+x'),
            new Position(grid - g, grid - g - it, 90, '+x'),
            new Position(grid - g - it, grid - g, 90, '+x')
        ] } }// 4 corners
    ]

    def run() {
        println "in the inputs" + params

        def game = Game.findById(params.gameId)

        Position franklinInitialPosition = new Position(game.franklinX, game.franklinY, game.franklinRot, game.franklinDir)
        Position emilyInitialPosition = new Position(game.emilyX, game.emilyY, game.emilyRot, game.emilyDir)
        Position treeInitialPosition = new Position(game.treeX, game.treeY, game.treeRot, game.treeDir)

        def scriptInstance = new DslScript(params)
        def script = scriptInstance.content
        def turtle
        if (game.user1 == params.user) {
            turtle = new Turtle("franklin", "image", franklinInitialPosition)
        } else if ((game.user2 == params.user)) {
            turtle = new Turtle("emily", "image", emilyInitialPosition)
        }

        def binding = new Binding([
                turtle: turtle,
                left: dsl.Direction.left,
                right: dsl.Direction.right,
                down: dsl.Direction.down,
                up: dsl.Direction.up,
                move: turtle.&move,
                by: turtle.&by
        ])
        def shell = new GroovyShell(binding)
        shell.evaluate(script)
        def result = binding.getVariable('turtle').result

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

        def last = array.last()
        boolean win = false
        if (turtle.name == "franklin") {
            if ((last.franklin.x == treeInitialPosition.x) && (last.franklin.y == treeInitialPosition.y)) {
                win = true
            }
            game.franklinX = last.franklin.x
            game.franklinY = last.franklin.y
            game.franklinRot = last.franklin.rotation
            game.franklinDir = last.franklin.direction
        } else if (turtle.name == "emily") {
            if ((last.emily.x == treeInitialPosition.x) && (last.emily.y == treeInitialPosition.y)) {
                win = true
            }
            game.emilyX = last.emily.x
            game.emilyY = last.emily.y
            game.emilyRot = last.emily.rotation
            game.emilyDir = last.emily.direction
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
            grid 15
            stepDuration 1000
            player turtle.name
            id game.id
        }

        def conf = builder.toString()


        // save current position
        if (!game.save(flush: true)) {
            ValidationErrors validationErrors = game.errors
            render validationErrors as JSON
        }

        // notify when turtle moves
        event topic: "executegame", data: conf
        println conf
        render conf
    }

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        render Game.list([fetch: [user1: 'eager', user2: 'eager']]) as JSON
    }


    def save() {
        def size = 15;
        JSONObject jsonObject = JSON.parse(params.game)
        //TODO generate positions after wall generation and exclude wall places
        Position franklinPosition = new Position().random(size)
        Position treePosition = new Position().random(size)
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
        def walls = mazes[new Random().nextInt(mazes.size())](size)
        walls.eachWithIndex { w, idx ->
            def name = "wall${idx}"
            images[name] = 'wall.png'
            obj[name] = (w as JSON)['target']
        }

        def root = [
            images: images,
            steps: [obj],
            grid: size,
            stepDuration: 1000
        ]
        String mazeDefinition = (root as JSON).toString()
        //end integration with maze
        Game gameInstance = new Game()
        gameInstance.user1 = jsonObject.entrySet().iterator().next().value
        gameInstance.mazeDefinition = mazeDefinition
        // save initial position
        gameInstance.franklinX = franklinPosition.x
        gameInstance.franklinY = franklinPosition.y
        gameInstance.franklinRot = franklinPosition.rotation
        gameInstance.franklinDir = franklinPosition.direction
        gameInstance.treeX = treePosition.x
        gameInstance.treeY = treePosition.y
        gameInstance.treeRot = treePosition.rotation
        gameInstance.treeDir = treePosition.direction
        if (!gameInstance.save(flush: true)) {
            ValidationErrors validationErrors = gameInstance.errors
            render validationErrors as JSON
        }
        // notify when first turtle create a new game
        event topic: "creategame", data: gameInstance
        render gameInstance as JSON
    }

    def update() {
        JSONObject jsonObject = JSON.parse(params.game)
        def gameInstance = Game.get(jsonObject.gameId)

        Position franklinInitialPosition = new Position(gameInstance.franklinX, gameInstance.franklinY, gameInstance.franklinRot, gameInstance.franklinDir)
        Position treeInitialPosition = new Position(gameInstance.treeX, gameInstance.treeY, gameInstance.treeRot, gameInstance.treeDir)
        Position emilyInitialPosition = new Position().random(15)
        // save initial position
        gameInstance.emilyX = emilyInitialPosition.x
        gameInstance.emilyY = emilyInitialPosition.y
        gameInstance.emilyRot = emilyInitialPosition.rotation
        gameInstance.emilyDir = emilyInitialPosition.direction

        JSONObject json = JSON.parse(gameInstance.mazeDefinition)
        def images = json["images"]
        images["emily"] = 'turtle2.png'

        def size = 15;

        def obj = [
                franklin: (franklinInitialPosition as JSON)['target'],
                emily: (emilyInitialPosition as JSON)['target'],
                tree1: (treeInitialPosition as JSON)['target']
        ]
        def root = [
                images: images,
                steps: [obj],
                grid: size,
                stepDuration: 1000
        ]
        gameInstance.mazeDefinition = (root as JSON).toString()
        if (!gameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'game.label', default: 'Game'), params.id])
            render flash as JSON
        }

        if (gameInstance.user2) {
            flash.message = message(code: 'default.game.already.started', args: [message(code: 'game.label', default: 'Game'), params.id])
            render flash as JSON
        }
        gameInstance.user2 = jsonObject.get("user2")

        if (!gameInstance.save(flush: true)) {
            ValidationErrors validationErrors = gameInstance.errors
            render validationErrors as JSON
        }

        // notify when second turtle enter the game
        event topic: "updategame", data: gameInstance
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
