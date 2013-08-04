package dsl

class UserInteraction {

    final def sharedContext = SharedContext.getInstance()
    def gameController
    def gameId

    def UserInteraction(gameController, gameId) {
        this.gameController = gameController
        this.gameId = gameId
    }

    String waitForAnswer(question) {
        gameController.event topic: "askgame", data: "{\"question\": \"$question\"}"
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
