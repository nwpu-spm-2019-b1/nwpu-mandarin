$(function () {
    $("#origPasswd, #newPasswd, #conPasswd").focus(restore);
    $("#origPasswd").blur(checkOrig);
    $("#newPasswd").blur(checkPass);
    $("#conPasswd").blur(checkConPass);
});

function checkOrig() {
    let flag;
    let value = $("#origPasswd").val();

    if(value === ""){
        setError("Original Password Cannot be Empty!");
        flag = false;
    }else {
        flag = true;
    }
    return flag;
}

function checkPass() {

    let flag = true;

    if($("#newPasswd").val() === ""){
        flag = false;
        setError("New Password Cannot be Empty!");
    }
    return flag;
}

function checkConPass(){
    if (!checkPass()){
        return false;
    }
    let flag = true;
    let newPasswd = $("#newPasswd").val();
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

$("#submit-form").bind("submit", function (event) {
    event.preventDefault();
    try_submit();
})

function try_submit() {
    if(!(checkOrig()&&checkPass()&&checkConPass())){
        return false;
    }
    let origPasswd = $("#origPasswd").val();
    let newPasswd = $("#newPasswd").val();
    $.ajax({
        url: "changepasswd",
        dataType: "json",
        type: "post",
        data: {
            "origPasswd": origPasswd,
            "newPasswd": newPasswd,
        },

        statusCode: {
            200: function (result) {
                console.log(result);
                alert(result.message);
                setTimeout("location.href = '/admin'",1000);
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