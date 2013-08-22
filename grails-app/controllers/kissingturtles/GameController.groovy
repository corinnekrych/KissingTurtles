package kissingturtles

import dsl.GameCustomizer
import dsl.UserInteraction
import dslprez.scala.eval.Evaluator
import grails.converters.JSON
import grails.validation.ValidationErrors
import org.codehaus.groovy.control.CompilerConfiguration
import groovy.json.JsonBuilder
import org.codehaus.groovy.grails.web.json.JSONObject
import dsl.DslScript
import dsl.Turtle
import dsl.Position

//import dslprez.Turtle
import dslprez.up
import dslprez.Direction
//import dslprez.Position

class GameController {

    int delay = 60000   // delay for 5 sec.
    int period = 60000  // repeat every sec.

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def gameService
    def wallGeneratorService
    Binding binding


    Timer timer = new Timer()

    def executeScala(game) {
        // Ugly search how to do better
        def cp = System.getProperty("java.class.path")
        if (!cp.contains("scala")) {
            cp = "lib/scaladsl.jar:lib/scalainterpreter.jar:lib/scala-reflect.jar:lib/scala-compiler.jar:lib/scala-library.jar:lib/lift-json.jar:target/classes:"+cp
            System.setProperty("java.class.path", cp)
        }

        def encoding = 'UTF-8'
        def stream = new ByteArrayOutputStream()
        def printStream = new PrintStream(stream, true, encoding)

        def result = ""
        def stacktrace = ""

        def evaluator
        try {
            evaluator = new Evaluator(printStream).withContinuations().withPluginsDir("lib/plugins")

            // Temporary solution
            if (params.scalaTimer != null) {
                dslprez.timer.MyTimer.reinit()
                evaluator.withPluginOption("dslplugin:timerValue:"+params.scalaTimer)
            }
            if (params.scalaSecurity != null) {
                evaluator.withPluginOption("dslplugin:blacklistFile:anyfile")
            }

            // Example for the game use bind and import
            def turtle = new dslprez.Turtle(new dslprez.Position(0,0,up as Direction))
            evaluator.addImport("dslprez._")
            println("Turtle is $turtle ==========")
            evaluator.bind("I","dslprez.Turtle",turtle)
            //End

            // editor2
//            def turtle = new Turtle()
//            evaluator.bind("I","dslprez.steps.editor2.Turtle",turtle)
//            evaluator.bind("left","String","left")
            // End editor2

            result = evaluator.eval(params.content)
            println result
        } catch (Exception e) {
            stacktrace = e.message
        } finally {
            if (evaluator != null) evaluator.close()
        }

//        def resultObject = new Result()
//        resultObject.result = stream.toString(encoding)
//        resultObject.shellResult = result
//        resultObject.stacktrace = stacktrace
//
//        // to avoid grails bringing 404 error
//        render resultObject as JSON
    }

    def run() {
        def conf
        def lang = "groovy"//params.lang
        def game = Game.findById(params.gameId)
        game.lastModified = new Date().getTime()

        // save game
        if (!game.save(flush: true)) {
            ValidationErrors validationErrors = gameInstance.errors
            render validationErrors as JSON
        }

        if (lang == "scala") {
            conf = executeScala(game)
        } else {
            conf = executeGroovy(game)
        }

        // save current position
        if (!game.save(flush: true)) {
            ValidationErrors validationErrors = game.errors
            render validationErrors as JSON
        }

        // notify when turtle moves
        event topic: "execute-game", data: conf
        render conf
    }

    def executeGroovy(game) {
        binding = new Binding()
        def userInteraction = new UserInteraction(this, params.gameId, params.userIdNotification, params.user, params.role)

        Position franklinPosition = new Position(game.franklinX, game.franklinY, game.franklinRot, game.franklinDir)
        Position emilyPosition = new Position(game.emilyX, game.emilyY, game.emilyRot, game.emilyDir)

        def scriptInstance = new DslScript(params)
        def script = scriptInstance.content
        def turtle
        def walls = JSON.parse(game.mazeDefinition)['walls']['steps'][0].values()
        if (game.role1 == params.role) {
            turtle = new Turtle("franklin", "image", franklinPosition, walls, userInteraction)
        } else {
            turtle = new Turtle("emily", "image", emilyPosition, walls, userInteraction)
        }

        binding.setVariable("turtle", turtle)
        binding.setVariable("left", dsl.Direction.left)
        binding.setVariable("right", dsl.Direction.right)
        binding.setVariable("up", dsl.Direction.up)
        binding.setVariable("down", dsl.Direction.down)
        binding.setVariable("move", turtle.&move)
        binding.setVariable("by", turtle.&by)
        binding.setVariable("ask", turtle.&ask)
        binding.setVariable("to", turtle.&to)

        def config = new CompilerConfiguration()
        config.addCompilationCustomizers(new GameCustomizer())

        def shell = new GroovyShell(this.class.classLoader,
                binding,
                config)
        shell.evaluate(script)

        def result = binding.getVariable('turtle').result

        gameService.runFormatting(game, turtle, result, params.userIdNotification)
    }

    def answer() {
        UserInteraction userInteraction = new UserInteraction(this, params.gameId, "", params.user, params.role)
        userInteraction.notifyResponse(params.content)
        render "{\"userIdNotification\":\"" + params.userIdNotification + "\"}"
    }

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        render Game.list() as JSON
    }

    // TODO configuration per user
    def saveConfiguration() {
        JSONObject jsonObject = JSON.parse(params.game)
        def gameInstance = Game.get(jsonObject.gameId)
    }

    def save() {
        def size = 15

        // generate walls
        def walls = wallGeneratorService.getWalls()

        // generate position for Franklin and the meeting point
        Position franklinPosition = new Position().random(size, walls)
        Position treePosition = new Position().random(size, walls)

        // format into json like
        def mazeDefinition = gameService.createFormatting(walls, franklinPosition, treePosition)

        // create new Game
        Game gameInstance = new Game()
        gameInstance.user1 = params.user1
        gameInstance.role1 = 'franklin'
        gameInstance.mazeDefinition = mazeDefinition
        gameInstance.franklinX = franklinPosition.x
        gameInstance.franklinY = franklinPosition.y
        gameInstance.franklinRot = franklinPosition.rotation
        gameInstance.franklinDir = franklinPosition.direction
        gameInstance.treeX = treePosition.x
        gameInstance.treeY = treePosition.y
        gameInstance.treeRot = treePosition.rotation
        gameInstance.treeDir = treePosition.direction
        gameInstance.lastModified = new Date().getTime()
        if (!gameInstance.save(flush: true)) {
            ValidationErrors validationErrors = gameInstance.errors
            render validationErrors as JSON
        }
        // notify when first turtle create a new game
        def asJSON = gameInstance as JSON
        def json = asJSON.toString(true)
        def builder = new JsonBuilder()
        builder {
            userIdNotification params.userIdNotification
            instance json
        }
        event topic: "save-game", data: builder.toString()
        timer.scheduleAtFixedRate(new CleanGameTask(gameInstance.id, this), delay, period)
        render gameInstance as JSON
    }

    def update() {
        JSONObject jsonObject = JSON.parse(params.game)
        def gameInstance = Game.get(jsonObject.gameId)

        // generate position for Emily (anywhere except on the walls)
        def walls = JSON.parse(gameInstance.mazeDefinition)['walls']['steps'][0].values()
        Position emilyPosition = new Position().random(15, walls)
        gameInstance.emilyX = emilyPosition.x
        gameInstance.emilyY = emilyPosition.y
        gameInstance.emilyRot = emilyPosition.rotation
        gameInstance.emilyDir = emilyPosition.direction

        gameInstance.mazeDefinition = gameService.updateFormatting(gameInstance, emilyPosition)

        if (!gameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'game.label', default: 'Game'), params.id])
            render flash as JSON
        }

        // only two users game
        if (gameInstance.user2) {
            flash.message = message(code: 'default.game.already.started', args: [message(code: 'game.label', default: 'Game'), params.id])
            render flash as JSON
        }
        gameInstance.user2 = jsonObject.get("user2")
        gameInstance.role2 = 'emily'
        gameInstance.lastModified = new Date().getTime()

        // save game
        if (!gameInstance.save(flush: true)) {
            ValidationErrors validationErrors = gameInstance.errors
            render validationErrors as JSON
        }

        // notify that Emily enters the game
        def asJSON = gameInstance as JSON
        def builder = new JsonBuilder()
        builder {
            userIdNotification params.userIdNotification
            instance asJSON.toString()
        }
        event topic: "update-game", data: builder.toString()
        render gameInstance as JSON
    }

    def delete() {
        def gameInstance = Game.get(params.id)
        if (!gameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'game.label', default: 'Game'), params.id])
            render flash as JSON
        }
        gameInstance.delete(flush: true)
        def builder = new JsonBuilder()
        builder {
            userIdNotification params.userIdNotification
            id params.id.toString()
        }
        event topic: "delete-game", data: builder.toString()
        render builder as JSON
    }
}
