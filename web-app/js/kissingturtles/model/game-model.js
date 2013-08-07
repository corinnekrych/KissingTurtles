var kissingturtles = kissingturtles || {};
kissingturtles.model = kissingturtles.model || {};

kissingturtles.model.gamemodel = function () {

    var that = grails.mobile.mvc.model();

    that.executed = grails.mobile.event(that);
    that.execute = function (item, context) {
        that.executed.notify({item: item}, context);
        if (item.errors || item.message) {
            return false;
        }
        that.items[item.id] = item;
        return true;
    };

    that.asked = grails.mobile.event(that);
    that.ask = function (item, context) {
        that.asked.notify({item: item}, context);
        if (item.errors || item.message) {
            return false;
        }
        return true;
    };

    return that;
};