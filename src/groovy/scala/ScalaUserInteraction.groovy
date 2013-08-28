package scala

import dsl.SharedContext

class ScalaUserInteraction {

    final def sharedContext = SharedContext.getInstance()
    def gameController
    def gameId
    def userIdNotification
    def user
    def role

    def ScalaUserInteraction(gameController, gameId, userIdNotification, user, role) {
        this.gameController = gameController
        this.gameId = gameId
        this.userIdNotification = userIdNotification
        this.user = user
        this.role = role
    }

    String waitForAnswer(question) {
        gameController.event topic: "ask-game", data: "{\"question\": \"$question\", \"gameId\": \"" + this.gameId + "\", \"user\" : \"" + this.user + "\", \"role\" : \"" + this.role +"\", \"userIdNotification\":\"" + this.userIdNotification+ "\"}"
        def sharedResponse = sharedContext.get(gameId)
        if (sharedResponse == null) sharedResponse = sharedContext.addToGames(gameId)
        synchronized(sharedResponse) {
            sharedResponse.wait()
        }
        return sharedContext.get(gameId)
    }
    
    def end() {/*
     if (sharedContext.get(gameId) != null) {
      sharedContext.remove(gameId)
      }*/
    }

    def notifyResponse(String myResponse) {
        def sharedResponse = sharedContext.get(gameId)
        synchronized(sharedResponse) {
            sharedResponse.response = myResponse
            sharedResponse.notify()
        }
    }

}
