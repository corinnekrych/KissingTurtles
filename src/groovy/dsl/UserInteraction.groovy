package dsl

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit


class UserInteraction {

    final def sharedContext = SharedContext.getInstance()
    def gameController
    def gameId

    def UserInteraction(gameController, gameId) {
        this.gameController = gameController
        this.gameId = gameId
    }

    String waitForAnswer(question) {
        FutureTask<String> future =
            new FutureTask<String>(new Callable<String>() {
                public String call() {
                    //println("I am in my future waiting....");
                    gameController.event topic: "askgame", data: "{\"question\": \"$question\"}"
                    def sharedResponse = sharedContext.addToGames(gameId)
                    synchronized(sharedResponse) {
                        sharedResponse.wait()
                    }
                    //println("I've been awake!!!");
                    return sharedContext.remove(gameId)
                }});
        ExecutorService executor = Executors.newSingleThreadExecutor()
        executor.submit(future);
        def response = future.get();
        executor.shutdownNow()
        return response
    }

    def notifyResponse(String myResponse) {
        def sharedResponse = sharedContext.get(gameId)
        synchronized(sharedResponse) {
            sharedResponse.response = myResponse
            sharedResponse.notify()
        }
    }

}
