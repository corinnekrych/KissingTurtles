
    

    var kissingturtles = kissingturtles || {};

    kissingturtles.loadgame = (function () {

        kissingturtles.configuration.domain.push(
            {
                name: "game",
                view:
                {
                    'list': $('#section-list-games'),
                    'save': $("#submit-game"),
                    'add': $('#section-show-game'),
                    'remove': $("#delete-game")
                }
                
                , hasOneRelations: [
                
                {type: "user", name: "user1"}
                
                ,
                
                
                {type: "user", name: "user2"}
                
                 ] 

                
            });
}());
