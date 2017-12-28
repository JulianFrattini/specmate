"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var http_1 = require("@angular/common/http");
var url_1 = require("../../../../../util/url");
var objects_1 = require("../../../../../util/objects");
var CEGConnection_1 = require("../../../../../model/CEGConnection");
var type_1 = require("../../../../../util/type");
require("rxjs/add/operator/toPromise");
var ServiceInterface = /** @class */ (function () {
    function ServiceInterface(http) {
        this.http = http;
    }
    ServiceInterface.prototype.checkConnection = function () {
        return this.http.get(url_1.Url.urlCheckConnectivity()).toPromise().then(function () { return true; }).catch(function () { return false; });
    };
    ServiceInterface.prototype.createElement = function (element) {
        var payload = this.prepareElementPayload(element);
        return this.http.post(url_1.Url.urlCreate(element.url), payload).toPromise().catch(this.handleError).then(function (response) { });
    };
    ServiceInterface.prototype.readElement = function (url) {
        return this.http
            .get(url_1.Url.urlElement(url)).toPromise()
            .catch(this.handleError)
            .then(function (element) { return element; });
    };
    ServiceInterface.prototype.readContents = function (url) {
        return this.http
            .get(url_1.Url.urlContents(url)).toPromise()
            .catch(this.handleError)
            .then(function (contents) { return contents; });
    };
    ServiceInterface.prototype.updateElement = function (element) {
        var payload = this.prepareElementPayload(element);
        return this.http.put(url_1.Url.urlUpdate(element.url), payload).toPromise().catch(this.handleError);
    };
    ServiceInterface.prototype.deleteElement = function (url) {
        return this.http.delete(url_1.Url.urlDelete(url)).toPromise().catch(this.handleError);
    };
    ServiceInterface.prototype.performOperation = function (url, serviceSuffix, payload) {
        return this.http.post(url_1.Url.urlCustomService(url, serviceSuffix), payload).toPromise().catch(this.handleError);
    };
    ServiceInterface.prototype.performQuery = function (url, serviceSuffix, parameters) {
        var urlParams = new http_1.HttpParams();
        for (var key in parameters) {
            if (parameters[key]) {
                urlParams = urlParams.append(key, parameters[key]);
            }
        }
        return this.http
            .get(url_1.Url.urlCustomService(url, serviceSuffix), { params: urlParams }).toPromise()
            .catch(this.handleError)
            .then(function (data) { return data; });
    };
    ServiceInterface.prototype.search = function (query) {
        var urlParams = new http_1.HttpParams();
        urlParams = urlParams.append('query', query);
        return this.http
            .get(url_1.Url.urlCustomService('', 'search'), { params: urlParams }).toPromise()
            .catch(this.handleError)
            .then(function (response) { return response; });
    };
    ServiceInterface.prototype.handleError = function (error) {
        console.error('Error in Service Interface! (details below)');
        return Promise.reject(error.message || error);
    };
    ServiceInterface.prototype.prepareElementPayload = function (element) {
        var payload = objects_1.Objects.clone(element);
        payload.url = undefined;
        delete payload.url;
        if (type_1.Type.is(element, CEGConnection_1.CEGConnection)) {
            payload.source.___proxy = 'true';
            payload.target.___proxy = 'true';
        }
        if (!element.id) {
            payload['___proxy'] = 'true';
        }
        return payload;
    };
    return ServiceInterface;
}());
exports.ServiceInterface = ServiceInterface;
//# sourceMappingURL=service-interface.js.map