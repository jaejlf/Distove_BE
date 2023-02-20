var stompClient = null;
var wsLink = "/chat/ws";

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
}

function connect() {
    var socket = new SockJS(wsLink);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe($("#sub").val(), {});
    });
}

function unsubscribe() {
    stompClient.unsubscribe($("#sub").val());
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    if (!$("#parentId").val()) {
        if (!$("#replyName").val()) {
            stompClient.send($("#pub").val(), {'userId': $("#userId").val()}, JSON.stringify(
                {
                    'type': $("#type").val(),
                    'status': $("#status").val(),
                    'content': $("#content").val(),
                    'messageId': $("#messageId").val(),

                }
            ));
        } else {
            stompClient.send($("#pub").val(), {'userId': $("#userId").val()}, JSON.stringify(
                {
                    'type': $("#type").val(),
                    'status': $("#status").val(),
                    'content': $("#content").val(),
                    'messageId': $("#messageId").val(),
                    'replyName': $("#replyName").val(),
                }
            ));
        }
    } else {
        if (!$("#replyName").val()) {
            stompClient.send($("#pub").val(), {'userId': $("#userId").val()}, JSON.stringify(
                {
                    'type': $("#type").val(),
                    'status': $("#status").val(),
                    'content': $("#content").val(),
                    'messageId': $("#messageId").val(),
                    'parentId': $("#parentId").val(),

                }
            ));
        } else {
            stompClient.send($("#pub").val(), {'userId': $("#userId").val()}, JSON.stringify(
                {
                    'type': $("#type").val(),
                    'status': $("#status").val(),
                    'content': $("#content").val(),
                    'messageId': $("#messageId").val(),
                    'replyName': $("#replyName").val(),
                    'parentId': $("#parentId").val(),

                }
            ));
        }
    }
}

function sendEmoji() {

    stompClient.send($("#pub").val(), {'userId': $("#userIdForEmoji").val()}, JSON.stringify(
        {
            'messageId': $("#messageIdForEmoji").val(),
            'emoji': $("#emoji").val()
        }
    ));

}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#unsubscribe").click(function () {
        unsubscribe();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });
    $("#sendEmoji").click(function () {
        sendEmoji();
    });
});