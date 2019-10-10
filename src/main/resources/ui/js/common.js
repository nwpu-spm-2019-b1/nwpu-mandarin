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

function pickProperties(obj, keys) {
    let dest = {};
    for (let key of keys) {
        dest[key] = obj[key]
    }
    return dest;
}

function escapeToHTML(s) {
    let element = document.createElement("div");
    let child = document.createTextNode(s);
    element.appendChild(child);
    return element.innerText;
}

export {ajax, urlWithParams, pickProperties};