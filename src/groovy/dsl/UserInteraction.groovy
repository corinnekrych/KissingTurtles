package dsl

class UserInteraction {

    final def sharedContext = SharedContext.getInstance()
    def gameController
    def gameId
    def userIdNotification
    def user
    def role

    def UserInteraction(gameController, gameId, userIdNotification, user, role) {
        this.gameController = gameController
        this.gameId = gameId
        this.userIdNotification = userIdNotification
        this.user = user
        this.role = role
    }

    String waitForAnswer(question) {

        def ask = [
                question: question,
                gameId: this.gameId,
                user: this.user,
                role: this.role,
                userIdNotification: this.userIdNotification
        ]
        gameController.event topic: "ask-game", data: ask
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
