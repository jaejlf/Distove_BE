var stompClient = null;
var wsLink = "/chat/ws";

/**
 *
 * publishMessage
 */
// var subLink = "/sub/3";
// var pubLink = "/pub/chat/3";
// var inputData = {
//     userId: 1,
//     type: 'TEXT',
//     content: 'Hello Hello~!~!'
// }

/**
 *
 * mod & del
 */
var subLink = "/sub/3";
var pubLink = "/pub/chat/3";
var inputData = {
    id: '63c91724233477018259db99',
    userId: 1,
    type: 'MODIFIED',
    content: '수정된 메시지'
}

/**
 *
 * beingTyped
 */
// var subLink = "/sub/3";
// var pubLink = "/pub/typing/3";
// var inputData = {
//     userId: 1
// }

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS(wsLink);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe(subLink, function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
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

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});