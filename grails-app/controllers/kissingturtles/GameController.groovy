package kissingturtles

import dsl.GameCustomizer
import dsl.UserInteraction
import dslprez.scala.eval.Evaluator
import grails.converters.JSON
import grails.validation.ValidationErrors
import groovy.transform.TypeChecked
import org.codehaus.groovy.control.CompilerConfiguration
import groovy.json.JsonBuilder
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import dsl.DslScript
import dsl.Turtle
import dsl.Position

class GameController {

    int delay = 600000   // delay for 10 min to join game
    int period = 60000  // check every 1 min
    int inactivityTime = 300000 // 5 mins
    def directory

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def gameService
    def wallGeneratorService
    Binding binding
    Timer timer = new Timer()

    /****************************************************************/
    /****************************************************************/
    /*                                                              */
    /*                        Scala part                            */
    /*                                                              */
    /****************************************************************/
    /****************************************************************/
    
    /* Transforms walls into an int[][] array for scala conversion */
    def contains(walls, i,j) {
        walls.any {(it[0] == i) && (it[1] == j)}
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


   /* Scala turtles */
   static def scalaTurtlesPerGame = [:]
   
   /* Init Scala turtles */
   def initTurtle(role,game,userIdNotification) {
     if (scalaTurtlesPerGame[game.id+""] == null) scalaTurtlesPerGame[game.id+""]=[:] 
     
     def mazeDefinition = JSON.parse(game.mazeDefinition)
     def walls = mazeDefinition['walls']
     def scalaWalls = getScalaWalls(walls,15)

     def turtle

     if (role == "franklin") {
       def franklinX = mazeDefinition['turtles']['position']['franklin'][0]
       def franklinY = mazeDefinition['turtles']['position']['franklin'][1]

       def franklinStartPosition = dslprez.scala.game.Position.getPosition(franklinX, franklinY, "+x")
       def userInteract = new scala.ScalaUserInteraction(this, game.id+"", userIdNotification, game.user1, role)

       turtle = dslprez.scala.game.Turtle.getTurtle("franklin", "image", franklinStartPosition, scalaWalls, userInteract)
     }
     
     if (role == "emily") {
       def emilyX = mazeDefinition['turtles']['position']['emily'][0]
       def emilyY = mazeDefinition['turtles']['position']['emily'][1]

       def emilyStartPosition = dslprez.scala.game.Position.getPosition(emilyX, emilyY, "+x")
       def userInteract = new scala.ScalaUserInteraction(this, game.id+"", userIdNotification, game.user2, role)
       turtle = dslprez.scala.game.Turtle.getTurtle("emily", "image", emilyStartPosition, scalaWalls, userInteract)
     }
           
     scalaTurtlesPerGame[game.id+""][role]=turtle
   }
       
   /* Execute DSL */
   def executeScala(game) {
        // Ugly search how to do better
//        def cp = System.getProperty("java.class.path")
//        if (!cp.contains("scala")) {
//            cp = "lib/scaladsl.jar:lib/scalainterpreter.jar:lib/scala-reflect.jar:lib/scala-compiler.jar:lib/scala-library.jar:lib/lift-json.jar:target/classes:"+cp
//            System.setProperty("java.class.path", cp)
//        }
       def cp = System.getProperty("java.class.path")
       if (!cp.contains("scala")) {
           cp = "lib/scaladsl.jar:lib/scalainterpreter.jar:lib/scala-reflect.jar:lib/scala-compiler.jar:lib/scala-library.jar:lib/lift-json.jar:" + cp
           System.setProperty("java.class.path", cp)
           System.getProperty("java.class.path", ".").tokenize(File.pathSeparator).each {
               println it
           }

           def compilerPath = java.lang.Class.forName("scala.tools.nsc.Interpreter").getProtectionDomain().getCodeSource().getLocation().getPath()
           //println " >>>>>>>>> " + compilerPath
           def evaluatorPath = java.lang.Class.forName("dslprez.scala.eval.Evaluator").getProtectionDomain().getCodeSource().getLocation().getPath()
           def libraryPath = java.lang.Class.forName("scala.App").getProtectionDomain().getCodeSource().getLocation().getPath()
           def reflectPath = java.lang.Class.forName("scala.reflect.api.Annotations").getProtectionDomain().getCodeSource().getLocation().getPath()
           def jsonPath = java.lang.Class.forName("net.liftweb.json.JsonParser").getProtectionDomain().getCodeSource().getLocation().getPath()
           def dslPath = java.lang.Class.forName("dslprez.scala.slides.Position").getProtectionDomain().getCodeSource().getLocation().getPath()
           System.setProperty("java.class.path", evaluatorPath + ":" + libraryPath + ":" + compilerPath + ":" + reflectPath + ":" + jsonPath + ":" + dslPath + ":" + cp)

           //println ">>>>>>> $directory"
       }

        def encoding = 'UTF-8'
        def stream = new ByteArrayOutputStream()
        def printStream = new PrintStream(stream, true, encoding)

        def turtle = scalaTurtlesPerGame[game.id+""][params.role]
        
        def result = ""
        def stacktrace = ""

        def evaluator
        def ex
        try {
            if (directory == null) {
                def compilerPath = java.lang.Class.forName("scala.tools.nsc.Interpreter").getProtectionDomain().getCodeSource().getLocation().getPath()
                directory = compilerPath.toString() - "lib/scala-compiler.jar"
            }
            //println ">>>>>>++++++++++++++++      Plug in Dir      ++++++++++>>>>>>>>>>>>>>>>>>> $directory" + "lib"
            evaluator = new Evaluator(printStream).withContinuations().withPluginsDir(directory + "lib")
            //evaluator = new Evaluator(printStream).withContinuations().withPluginsDir("lib/plugins")

            // Compiler Plugins
            evaluator.withPluginOption("dslplugin:blacklistFile:anyfile")
        
            evaluator.addImport("dslprez.scala.game._")
            evaluator.addImport("dslprez.scala.game.Turtle.end")

            evaluator.bind("turtleInstance","dslprez.scala.game.Turtle",turtle)
            //evaluator.bind("meet","(Int,Int)=>Stream[AbstractPosition]",turtle.meet _)
            evaluator.eval("implicit val I = turtleInstance")
            evaluator.eval("def meet = I.meet _")
            
            def finalScript = "Turtle startDsl {\n"+params.content+"\nend\n}\n"

            try {
                result = evaluator.eval(finalScript)
            }
            catch (e) {
                ex = e.message+"\n\n"+stream.toString(encoding)
                //e.getMessage() //stream.toString()
                println ex
            }
        } finally {
            if (evaluator != null) evaluator.close()
        }

        gameService.runScalaFormatting(game, JSON.parse(game.mazeDefinition), turtle, ex, params.userIdNotification)

    }

    /****************************************************************/
    /****************************************************************/
    /*                                                              */
    /*                      End Scala part                          */
    /*                                                              */
    /****************************************************************/
    /****************************************************************/
   
    def run() {
        def conf
        def game = Game.findById(params.gameId)

        def lang = 'groovy'
        
        if (params.role == "emily") {
           lang = game.user2_language
        }
        if (params.role == "franklin") {
           lang = game.user1_language
        }
        
        if (lang == "scala") {
            conf = executeScala(game)
        } else {
            conf = executeGroovy(game)
        }

        game.lastModified = new Date().getTime()
        // save current position
        if (!game.save(flush: true)) {
            ValidationErrors validationErrors = game.errors
            render validationErrors as JSON
        }

        // notify when turtle moves
        event topic: "execute-game", data: [userIdNotification: params.userIdNotification, instance:conf]
        render conf as JSON
    }

    def executeGroovy(game) {
        binding = new Binding()

        def userInteraction = new UserInteraction(this, params.gameId, params.userIdNotification, params.user, params.role)

        def mazeDefinition = JSON.parse(game.mazeDefinition)
        Position emilyPosition = new Position(mazeDefinition['turtles']['position']['emily'][0], mazeDefinition['turtles']['position']['emily'][1], 0, '+x')
        Position franklinPosition = new Position(mazeDefinition['turtles']['position']['franklin'][0], mazeDefinition['turtles']['position']['franklin'][1], 0, '+x')

        def scriptInstance = new DslScript(params)
        def script = scriptInstance.content
        def turtle
        def walls = mazeDefinition['walls']
        if ("franklin" == params.role) {
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
        binding.setVariable("meet", turtle.&meet)

        def config = new CompilerConfiguration()
        config.addCompilationCustomizers(new GameCustomizer(), new ASTTransformationCustomizer(TypeChecked, extensions:['TurtleExtension.groovy']))
        config.scriptBaseClass = GameScript.class.name

        def shell = new GroovyShell(this.class.classLoader,
                binding,
                config)
        //shell.evaluate("use(StepCategory) {" + script + "}")
        def ex
        try {
          shell.evaluate(script)
        } catch(e) {
           ex = e.message;
           if (e.message.contains("Limit of allowed statements exceeded!"))
               ex = " Limit of allowed statements exceeded!"
           println e.stackTrace + "\n>>message=" + e.message + ">>cause" + e.cause
        }
        def result = binding.getVariable('turtle').result

        gameService.runFormatting(game, mazeDefinition, turtle, result, ex, params.userIdNotification)
    }

    def answer() {
        def gameInstance = Game.get(params.gameId)

        def targetTurtle_language = "groovy"
        def targetTurtle = ""
        if (params.role == "franklin") {
           targetTurtle = 'emily'
           targetTurtle_language  = gameInstance.user2_language //Emily language
        }
        if (params.role == "emily") {
           targetTurtle = 'franklin'
           targetTurtle_language  = gameInstance.user1_language //Franklin langauge
        }
          
      if (targetTurtle_language == 'scala') {
        def turtle = scalaTurtlesPerGame[params.gameId+""][targetTurtle]
        turtle.answer(params.content.toString())
        
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
        Position birdPosition = new Position().random(size, walls)

        // format into json like
        def mazeDefinition = gameService.createFormatting(walls, franklinPosition, treePosition, birdPosition)

        // create new Game
        Game gameInstance = new Game()
        gameInstance.user1 = params.user1
        gameInstance.mazeDefinition = mazeDefinition
        gameInstance.lastModified = new Date().getTime()
        if (!gameInstance.save(flush: true)) {
            ValidationErrors validationErrors = gameInstance.errors
            render validationErrors as JSON
        }
        timer.scheduleAtFixedRate(new CleanGameTask(gameInstance.id, this, inactivityTime), delay, period)

        // notify when first turtle create a new game
        event topic: "save-game", data: [userIdNotification: params.userIdNotification, instance:[id:gameInstance.id, version: gameInstance.version, user1: gameInstance.user1]]

        if (params.language == "scala") initTurtle("franklin",gameInstance,params.userIdNotification)
        gameInstance.user1_language = params.language

        def newGame = [
                id : gameInstance.id,
                version : gameInstance.version,
                user1: gameInstance.user1,
                mazeDefinition: mazeDefinition
        ]
        render newGame as JSON
    }

    def update() {
		JSONObject jsonObject = JSON.parse(params.game)

        def gameInstance = Game.get(jsonObject.gameId)
        def language = jsonObject.language

        if (!gameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'game.label', default: 'Game'), params.id])
            render flash as JSON
        }

        // only two users game
        if (gameInstance.user2) {
            flash.message = message(code: 'default.game.already.started', args: [message(code: 'game.label', default: 'Game'), params.id])
            flash.userIdNotification = params.userIdNotification
            flash.id = jsonObject.gameId
            render flash as JSON
            return
        }


        // generate position for Emily (anywhere except on the walls)
        def mazeDefinition = gameService.updateFormatting(gameInstance)

        gameInstance.user2 = jsonObject.get("user2")
        gameInstance.lastModified = new Date().getTime()
        gameInstance.mazeDefinition = mazeDefinition

        // save game
        if (!gameInstance.save(flush: true)) {
            ValidationErrors validationErrors = gameInstance.errors
            render validationErrors as JSON
        }

        // notify when first turtle create a new game
        event topic: "update-game", data: [userIdNotification: params.userIdNotification, instance:[id:gameInstance.id, version: gameInstance.version, user1: gameInstance.user1, user2: gameInstance.user2, turtles: mazeDefinition['turtles']]]

        if (language == "scala") initTurtle("emily",gameInstance,params.userIdNotification)
        gameInstance.user2_language = language

        def returnGame = [
                id : gameInstance.id,
                version : gameInstance.version,
                user1: gameInstance.user1,
                user2: gameInstance.user2,
                mazeDefinition: mazeDefinition
        ]
        render returnGame as JSON
    }

    def delete() {
        def gameInstance = Game.get(params.id)
        def builder = new JsonBuilder()
        builder {
            userIdNotification params.userIdNotification
            id params.id.toString()
        }
        if (gameInstance) {
            gameInstance.delete(flush: true)
            event topic: "delete-game", data: [userIdNotification: params.userIdNotification, id:gameInstance.id]
        }
        render builder as JSON
    }
}
