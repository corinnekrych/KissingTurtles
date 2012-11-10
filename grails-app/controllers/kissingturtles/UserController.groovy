package kissingturtles


import grails.converters.JSON
import grails.validation.ValidationErrors
import groovy.json.JsonBuilder;

import org.codehaus.groovy.grails.web.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException

class UserController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }
	
    def list() {
      params.max = Math.min(params.max ? params.int('max') : 10, 100)
     	render User.list(params) as JSON
    }

    def save() {
      def jsonObject = JSON.parse(params.user)
      
      User userInstance = new User(jsonObject)

      
      if (!userInstance.save(flush: true)) {
        ValidationErrors validationErrors = userInstance.errors
        render validationErrors as JSON
      }
      render userInstance as JSON
    }
    
    def show() {
      def userInstance = User.get(params.id)
      if (!userInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
        render flash as JSON
      }
      render UserInstance as JSON
    }

    def update() {
      def jsonObject = JSON.parse(params.user)
        
        User userReceived = new User(jsonObject)
        
        def userInstance = User.get(jsonObject.id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
            render flash as JSON
        }

        if (jsonObject.version) {
          def version = jsonObject.version.toLong()
          if (userInstance.version > version) {
            userInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'user.label', default: 'User')] as Object[],
                          "Another user has updated this User while you were editing")
                ValidationErrors validationErrors = userInstance.errors
                render validationErrors as JSON
                return
            }
        }

        userInstance.properties = userReceived.properties

        if (!userInstance.save(flush: true)) {
          ValidationErrors validationErrors = userInstance.errors
          render validationErrors as JSON
        }
		render userInstance as JSON
    }

    def delete() {
      def userId = params.id
      def userInstance = User.get(params.id)
      if (!userInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
        render flash as JSON
      }
      try {
            userInstance.delete(flush: true)
      }
      catch (DataIntegrityViolationException e) {
        flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'user.label', default: 'User'), params.id])
        render flash as JSON
      }
      render userInstance as JSON
    }
}
