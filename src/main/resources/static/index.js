/**
 * Created by 80231428 on 2018/3/19.
 */
function goAudit(){
    var input = $("#content").val();
    $.ajax({
        url: '/audit',
        type: 'POST',
        contentType : "application/json",
        dataType : 'json',
        data: JSON.stringify({
            content:input,
            source:"web"
        }),
        success: function(data) {
            goResult(data);
        },
        error: function(data) {
            console.log(data);
        }
    });
}

function goFeedback(){
    var input = $("#content").val();
    var feedback = $("#feedback").val();
    $.ajax({
        url: '/home',
        type: 'POST',
        data: {
            Content: input,
            Feedback: feedback
        },
        success: function(data) {
            alert("臣领旨");
        },
        error: function(data) {
            console.log(data);
        }
    });
}

function goResult(data){
    var obj = data;
    $("#neg").css("color", "#AAA");
    $("#pos").css("color", "#AAA");

    if(obj.code == "100000"){
        if(obj.data == "0"){
            $("#neg").css("color", "red");
            $("#feedback").val("1");
            $("#feedback").css("display", "none");
        }else if(obj.data == "1"){
            $("#pos").css("color", "green");
            $("#feedback").val("0");
            $("#feedback").css("display", "none");
        }else if(obj.data == "2"){
            $("#spt").css("color", "yellow");
            $("#feedback").val("0");
            $("#feedback").css("display", "none");
        }
    } else {
        $("#feedback").css("display", "inline");
        console.log(obj.ErrMsg);
    }
}

function catchInputArea(){
    var content = document.getElementById("content");
    content.addEventListener("input", change, true);
    function change(){
        if(content.value){
            $("#submit").css("opacity", "1");
        }else{
            $("#submit").css("opacity", "0.3");
        }
        $("#neg").css("color", "#AAA");
        $("#spt").css("color", "#AAA");
        $("#pos").css("color", "#AAA");
        $("#feedback").css("display", "none");
    }
}