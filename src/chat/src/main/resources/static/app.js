var stompClient = null;
var wsLink = "/chat/ws";

/**
 * publishMessage
 */
// var subLink = "/sub/1";
// var pubLink = "/pub/chat/1";
// var inputData = {
//     userId: 1,
//     type: 'TEXT',
//     content: 'Hello Hello~!~!'
// }

/**
 * mod & del
 */
// var subLink = "/sub/1";
// var pubLink = "/pub/chat/1";
// var inputData = {
//     userId: 1,
//     type: 'MODIFIED',
//     messageId: '63ce2a96ac2d4468f06c9971',
//     content: '수정된 메시지'
// }

/**
 * beingTyped
 */
// var subLink = "/sub/3";
// var pubLink = "/pub/typing/3";
// var inputData = {
//     userId: 1
// }

/**
 * createReply
 */
var subLink = "/sub/1";
var pubLink = "/pub/reply/1";
var inputData = {
    userId: 1,
    type: 'TEXT',
    content: 'repl-repl-repl',
    parentId: "63ce2a96ac2d4468f06c9971"
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
}

function connect() {
    var socket = new SockJS(wsLink);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe(subLink, function (greeting) {
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send(pubLink, {}, JSON.stringify(inputData));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});