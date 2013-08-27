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
        gameController.event topic: "ask-game", data: "{\"question\": \"$question\", \"gameId\": \"" + this.gameId + "\", \"user\" : \"" + this.user + "\", \"role\" : \"" + this.role +"\", \"userIdNotification\":\"" + this.userIdNotification+ "\"}"
        def sharedResponse = sharedContext.addToGames(gameId)
        synchronized(sharedResponse) {
            sharedResponse.wait()
        }

        return sharedContext.remove(gameId)
    }

// TODO Pascal
//    String waitForAnswer(question) {
//        gameController.event topic: "ask-game", data: "{\"question\": \"$question\", \"gameId\": \"" + this.gameId + "\", \"user\" : \"" + this.user + "\", \"role\" : \"" + this.role +"\", \"userIdNotification\":\"" + this.userIdNotification+ "\"}"
//        def sharedResponse = sharedContext.get(gameId)
//        if (sharedResponse == null) sharedResponse = sharedContext.addToGames(gameId)
//        def f = new File("/tmp/userinteract_"+role+".txt")
//        f << "wait for answer for "+question+" "+sharedResponse+"\n"
//        synchronized(sharedResponse) {
//            sharedResponse.wait()
//        }
//        //sharedContext.get(gameId).response
//        return sharedContext.remove(gameId)
//    }
    
    def end() {/*
     if (sharedContext.get(gameId) != null) {
     def f = new File("/tmp/userinteract_"+role+".txt")
        f << "End \n"
       
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
