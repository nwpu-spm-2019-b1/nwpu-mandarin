$(function () {
    $("#username, #passwd, #conPasswd").focus(restore);
    $("#username").blur(checkName);
    $("#passwd").blur(checkPass);
    $("#conPasswd").blur(checkConPass);
});

function checkName() {
    let flag;
    let name = $("#username").val();

    if(name === ""){
        setError("Set your name!");
        flag = false;
    }else {
        flag = true;
    }
    return flag;
}

//check whether pass is empty
function checkPass() {

    let flag = true;

    if($("#passwd").val() === ""){
        flag = false;
        setError("Set your password!");
    }
    return flag;
}

function checkConPass(){
    if (!checkPass()){
        return false;
    }
    let flag = true;
    let newPasswd = $("#passwd").val();
    let conPasswd = $("#conPasswd").val();
    if(conPasswd === null){
        flag = false;
        setError("Confirm Password Cannot be Empty!");
    }else if (newPasswd!=conPasswd){
        flag = false;
        setError("Two Passwords are different!");
    }
    return true;
}

$("#registerForm").bind("submit", function (event) {
    event.preventDefault();
    try_submit();
})

function try_submit() {
    if(!(checkName()&&checkPass())){
        return false;
    }
    let name = $("#username").val();
    let password = $("#passwd").val();
    $.ajax({
        url: "/admin/register",
        dataType: "json",
        type: "post",
        data: {
            "username": name,
            "password": password,
        },

        statusCode: {
            201: function (result) {
                console.log(result);
                alert(result.message);
                setTimeout("window.location.href = '/admin'",1000);
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