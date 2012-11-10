package dsl

import grails.converters.JSON
import org.codehaus.groovy.control.CompilerConfiguration

class DslScriptController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [surveyInstanceList: DslScript.list(params), surveyInstanceTotal: DslScript.count()]
    }

    def run() {

        println "in the inputs" + params
        def scriptInstance = new DslScript(params)
        def script = scriptInstance.content
        def turtle = new Turtle("Fanklin", "image")

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
        def json = binding.turtle.result as JSON

    }

}
