package kissingturtles

import groovy.json.JsonBuilder


class CleanGameTask extends TimerTask {
    def gameId
    def controller

    CleanGameTask(gameId, controller) {
        this.gameId = gameId
        this.controller = controller
    }
    public void run() {
        def gameInstance = Game.get(gameId);
        if (gameInstance) {
            if ((new Date().time - gameInstance.lastModified) > 5000l) {
                println "Game deleted"
                gameInstance.delete(flush: true)
                def builder = new JsonBuilder()
                builder {
                    userIdNotification "server"
                    id gameId
                }
                controller.event topic: "delete-game", data: builder.toString()
                cancel()
            }
        } else {
            cancel()
        }
    }
}