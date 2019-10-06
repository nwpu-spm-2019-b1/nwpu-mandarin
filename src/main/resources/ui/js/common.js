import $ from 'jquery';

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

function copyProperties() {
}