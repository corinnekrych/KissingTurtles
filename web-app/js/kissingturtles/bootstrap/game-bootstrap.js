
    

    var kissingturtles = kissingturtles || {};

    kissingturtles.loadgame = (function () {

        kissingturtles.configuration.domain.push(
            {
                name: "game",
                view:
                {
                    'list': $('#section-list-games'),
                    'execute': $("#submit-game"),
                    'add': $('#section-show-game'),
                    'remove': $("#delete-game")
                }

            });
}());
