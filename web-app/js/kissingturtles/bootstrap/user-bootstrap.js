
    

    var kissingturtles = kissingturtles || {};

    kissingturtles.loaduser = (function () {

        kissingturtles.configuration.domain.push(
            {
                name: "user",
                view:
                {
                    'list': $('#section-list-users'),
                    'save': $("#submit-user"),
                    'add': $('#section-show-user'),
                    'remove': $("#delete-user")
                }
                

                
            });
}());
