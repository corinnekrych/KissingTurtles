/*
 * Copyright 2012 3musket33rs
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 */
var grails = grails || {};
grails.mobile = grails.mobile || {};
grails.mobile.push = grails.mobile.push || {};

grails.mobile.push.pushmanager = function (grailsEvents, domainName, store, model) {
    var that = {};
    var store = store;
    var domainName = domainName;
    var model = model;

    grailsEvents.on('create' + domainName , function (data) {
        if (!store.read(data.id)) {
            data.NOTIFIED = true;
            store.store(data);
            model.createItem(data);
        }
    });

    grailsEvents.on('update' + domainName , function (data) {
        var retrievedData = store.read(data.id);
        if (retrievedData && retrievedData.version < data.version) {
            data.NOTIFIED = true;
            store.store(data);
            model.updateItem(data);
        }
    });

    grailsEvents.on('delete' + domainName , function (data) {
        if (!store.read(data.id)) {
            data.NOTIFIED = true;
            store.remove(data);
            model.deleteItem(data);
        }
    });

    grailsEvents.on('execute' + domainName , function (data) {
            var dataParsed = JSON.parse(data);
            dataParsed.NOTIFIED = true;
            model.execute(dataParsed);
    });

    return that;
};