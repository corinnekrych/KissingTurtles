package kissingturtles


import grails.converters.JSON
import grails.validation.ValidationErrors
import groovy.json.JsonBuilder;

import org.codehaus.groovy.grails.web.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException

class GameController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        render Game.list(params) as JSON
    }

    def save() {
        def jsonObject = JSON.parse(params.game)

        Game gameInstance = new Game(jsonObject)


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
