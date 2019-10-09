import $ from 'jquery';
import queryString from 'query-string';

function ajax(o) {
    return new Promise((resolve, reject) => {
        o.success = function (resp) {
            resolve(resp);
        };
        o.error = function (xhr) {
            reject(xhr)
        };
        $.ajax(o);
    });
}

function urlWithParams(url, params) {
    return url + "?" + queryString.stringify(params);
}

export {ajax, urlWithParams};