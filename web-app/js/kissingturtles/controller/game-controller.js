var kissingturtles = kissingturtles || {};
kissingturtles.controller = kissingturtles.controller || {};

kissingturtles.controller.gamecontroller = function (feed, model, view, cfg) {
    var that = grails.mobile.mvc.controller(feed, model, view, cfg);

    //Place here your custom event
//    view.somethingButtonClicked.attach(function (item, context) {
//          // ....
//          // Notify the model
//          that.model.somethingHappened(data, context);
//    });



    view.executeButtonClicked.attach(function (item, context) {
        execute(item, context);
    });

    view.answerButtonClicked.attach(function (item, context) {
        answer(item, context);
    });

    var execute = function (data, context) {
        var executed = function (data) {
            return that.model.execute(data, context);
        };
        feed.execute(data, executed);
    };

    var answer = function (data, context) {
        feed.answer(data,  function (data) {
        });
    };

///// ONLINE - Feed Method

    that.execute = function (data, executed) {
        send(data, "run", "POST", function(data) {
            executed(data);
        })
    };

    that.answer = function (data, answered) {
        send(data, "answer", "POST", function(data) {
            answered(data);
        })
    };

    return that;
};
