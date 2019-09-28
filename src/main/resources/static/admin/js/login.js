$(function () {

    $("#username, #password").focus(restore);
    $("#username").blur(checkName);
    $("#password").blur(checkPass);
});

function checkName() {
    let flag;
    let name = $("#username").val();

    if(name === ""){
        setError("Enter admin name!");
        flag = false;
    }else {
        flag = true;
    }
    return flag;
}

//check whether pass is empty
function checkPass() {

    let flag = true;

    if($("#password").val() === ""){
        flag = false;
        setError("Enter password!");
    }
    return flag;
}

$("#login-form").bind("submit", function (event) {
    event.preventDefault();
    try_login();
})

function try_login() {
    if(!(checkName()&&checkPass())){
        return false;
    }
    let name = $("#username").val();
    let password = $("#password").val();
    $.ajax({
        url: "login",
        dataType: "json",
        type: "post",
        data: {
            "username": name,
            "password": password,
        },

        statusCode: {
            201: function (result) {
                window.location.href = "/admin";
            },
            404: function (result) {
                setError(result.responseJSON.message);
            },
            400: function (result) {
                setError(result.responseJSON.message);
            },
        },
    });
}
//clear error info
function restore() {
    $(".error").text("");
}

//set error info
function setError(info) {
    $(".error").text(info);
}

$(document).keydown(function(event) {
    if (event.keyCode == "13") {//keyCode=13是回车键
        $('#submit').click();
    }
});