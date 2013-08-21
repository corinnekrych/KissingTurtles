package kissingturtles

import dsl.GameCustomizer
import dsl.UserInteraction
import dslprez.scala.eval.Evaluator
import grails.converters.JSON
import grails.validation.ValidationErrors
import org.codehaus.groovy.control.CompilerConfiguration
import groovy.json.JsonBuilder
import org.codehaus.groovy.grails.web.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException

import dsl.DslScript
import dsl.Turtle
import dsl.Position

class GameController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def gameService
    def wallGeneratorService
    Binding binding
    
    /* Transforms walls into an int[][] array for scala conversion */
    def contains(walls, i,j) {
      walls.any {(it.x == i) && (it.y == j)}
    }
    
    def getScalaWalls(walls, size) {
       def scalaWall = new int[size][size]
          for (int i = 0; i < size; i++) {
             for (int j = 0; j < size; j++) {
                if (contains(walls,i,j)) scalaWall[i][j]=1 else scalaWall[i][j]=0
                }
          }
       scalaWall
   }
   /* End of Helper */
   
   static def scalaNotifiers = [:]
   
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

        
        def turtle
        def result = ""
        def stacktrace = ""

        def evaluator
        try {
            evaluator = new Evaluator(printStream).withContinuations().withPluginsDir("lib/plugins")

            /*
            // Temporary solution
            if (params.scalaTimer != null) {
                dslprez.timer.MyTimer.reinit()
                evaluator.withPluginOption("dslplugin:timerValue:"+params.scalaTimer)
            }
            if (params.scalaSecurity != null) {
                evaluator.withPluginOption("dslplugin:blacklistFile:anyfile")
            }
            */

            def userInteract = new UserInteraction(this, params.gameId, params.userIdNotification, params.user, params.role)

            def scalaFranklinDir = game.franklinDir //getScalaDir(game.franklinDir)
            def scalaEmilyDir = game.emilyDir //getScalaDir(game.emilyDir)
            def franklinPosition = dslprez.scala.game.Position.getPosition(game.franklinX, game.franklinY, game.franklinRot, scalaFranklinDir)
            def emilyPosition = dslprez.scala.game.Position.getPosition(game.emilyX, game.emilyY, game.emilyRot, scalaEmilyDir)

            def walls = JSON.parse(game.mazeDefinition)['walls']['steps'][0].values()
            def scalaWalls = getScalaWalls(walls,15)
     
            def userInteraction = new dslprez.scala.game.Notifier(userInteract,null)
           println("My notifier for "+params.role+" = "+userInteraction)
           scalaNotifiers[params.role] = userInteraction
            
            if (game.role1 == params.role) {
              turtle = new dslprez.scala.game.Turtle("franklin", "image", franklinPosition, scalaWalls, userInteraction)
            } else {
              turtle = new dslprez.scala.game.Turtle("emily", "image", emilyPosition, scalaWalls, userInteraction)
            }

            userInteraction.setTurtle(turtle)
            
            evaluator.addImport("dslprez.scala.game._")
            evaluator.addImport("dslprez.scala.game.Turtle.end")

            
            evaluator.bind("turtleInstance","dslprez.scala.game.Turtle",turtle)
            evaluator.eval("implicit val I = turtleInstance")
            
            def finalScript = "Turtle startDsl {\n"+params.content+"\nend\n}\n"
            
            result = evaluator.eval(finalScript)
                 
        } finally {
            if (evaluator != null) evaluator.close()
        }
 
        gameService.runScalaFormatting(game, turtle, params.userIdNotification)

    }

    def run() {
        def conf
        def lang = "scala" //params.lang
        def game = Game.findById(params.gameId)
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
      //println("Answer")
      if (false) { //scala) {
        def userInteraction =  scalaNotifiers[params.role]
         println("My notifier for "+params.role+" = "+userInteraction)
        userInteraction.notify(params.content.toString())
      } else {           
         UserInteraction userInteraction = new UserInteraction(this, params.gameId, "", params.user, params.role)
         userInteraction.notifyResponse(params.content)
      }
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
