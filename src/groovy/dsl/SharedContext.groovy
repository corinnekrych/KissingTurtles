package dsl

class SharedContext {
    private static final SharedContext instance = new SharedContext()
    def games = [:]

    static SharedContext getInstance() {
        instance
    }

    def addToGames(gameId) {
        def sharedResponse = new SharedResponse()
        games.put(gameId, sharedResponse)
        sharedResponse
    }

    SharedResponse get(gameId) {
        games.get(gameId)
    }

    def remove(gameId) {
        games.remove(gameId).response
    }
}

class SharedResponse {
    String response
}
