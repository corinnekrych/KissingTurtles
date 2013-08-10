var kissingturtles = kissingturtles || {};
kissingturtles.controller = kissingturtles.controller || {};

kissingturtles.controller.gamecontroller = function (feed, model, view, cfg) {
    var that = grails.mobile.mvc.controller(feed, model, view, cfg);
    var userIdNotification = cfg.userIdNotification;
    var url = cfg.baseURL;

    view.executeButtonClicked.attach(function (item, context) {
        execute(item, context);
    });

    view.answerButtonClicked.attach(function (item, context) {
        answer(item, context);
    });

    var execute = function (data, context) {
        feed.send(data, "run", "POST", function(data) {
            return that.model.execute(data, context);
        })
    };

    var answer = function (data, context) {
        feed.send(data, "answer", "POST", function (data) {
        })
    };

    return that;
};
