import LibrarianView from "../librarian/LibrarianView.vue";
import {template} from "lodash";
import nanoid from "nanoid";
import errorToastTemplate from "../librarian/error_toast.template";

function showErrorToast(err) {
    let compiled = template(errorToastTemplate);
    let id = nanoid(8);
    let html = compiled({id: id, title: "Error", "body": err});
    $("#toast-container").append(html);
    let element = $(`#toast-${id}`);
    element.toast("show");
    setTimeout(() => {
        element.toast("hide");
    }, 5000);
}

window.showErrorToast = showErrorToast;

let vm = new Vue({
    el: "#app",
    components: {
        LibrarianView
    },
    template: '<LibrarianView />'
});