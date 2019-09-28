$("#form").bind("submit", function (event) {
    event.preventDefault();
    try_submit();
})

function try_submit() {
    let username = $("#username").val();
    $.ajax({
        url: "/admin/updateLibrarian",
        dataType: "json",
        type: "post",
        data: {
            "username": username,
        },

        statusCode: {
            200: function (result) {
                alert(result.message);
                setTimeout("window.location.href = '/admin'", 1000);
                ;
            },
            404: function (result) {
                setError(result.responseJSON.message);
            },
            400: function (result) {
                setError(result.responseJSON.message);
            },
        },
    })
}