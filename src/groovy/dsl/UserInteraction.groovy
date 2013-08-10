package dsl

class UserInteraction {

    final def sharedContext = SharedContext.getInstance()
    def gameController
    def gameId
    def userIdNotification

    def UserInteraction(gameController, gameId, userIdNotification) {
        this.gameController = gameController
        this.gameId = gameId
        this.userIdNotification = userIdNotification
    }

    String waitForAnswer(question) {
        gameController.event topic: "ask-game", data: "{\"question\": \"$question\", \"userIdNotification\":\"" + this.userIdNotification+ "\"}"
        def sharedResponse = sharedContext.addToGames(gameId)
        synchronized(sharedResponse) {
            sharedResponse.wait()
        }

        return sharedContext.remove(gameId)
    }

    def notifyResponse(String myResponse) {
        def sharedResponse = sharedContext.get(gameId)
        synchronized(sharedResponse) {
            sharedResponse.response = myResponse
            sharedResponse.notify()
        }
    }

}
