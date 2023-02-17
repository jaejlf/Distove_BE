let stompClient = null;
const wsLink = "/presence/ws";

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
}

function connect() {
    const socket = new SockJS(wsLink);
    stompClient = Stomp.over(socket);
    stompClient.connect({'userId': $("#userId").val()}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe($("#sub").val(),function (greeting) {});
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendEmoji() {

    stompClient.send($("#pub").val(), {'userId': $("#userId").val()}, JSON.stringify(
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
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#sendEmoji").click(function () {
        sendEmoji();
    });
});