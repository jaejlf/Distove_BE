var stompClient = null;
const wsLink = "/presence/ws";
let subscription = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#subscribe").prop("disabled", subscription!==null);
    $("#disconnect").prop("disabled", !connected);

}

function connect() {
    const socket = new SockJS(wsLink);
    stompClient = Stomp.over(socket);
    stompClient.connect({'userId': $("#userId").val()}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

    });
}

function getMembers() {
    const getMembersLink = "/presence/server/" + $("#serverId").val()
    $.ajax({
        url: getMembersLink,
        method: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.setRequestHeader("token",$("#token").val());
        },
        success: function (data) {
            showMembers(data.data);
            console.log(data)
        },
        error: function (xhr, textStatus, error) {
            console.log(xhr.statusText);
            console.log(textStatus);
            console.log(error);
        }
    });
}

function subscribe() {
    getMembers()
    subscription = stompClient.subscribe("/sub/" + $("#serverId").val(), function (update) {
        console.log("Subscribed: ", JSON.parse(update.body))
        let body =JSON.parse(update.body)
        const presence = document.getElementById(body.userId);



        function changeColor(newColor) {
            var elem = document.getElementById('para');
            elem.style.color = newColor;
        }
        presence.innerHTML=color(body.presence.status)+
            "   <div>"+body.presence.status+"</div>" +
            "   <div>"+body.presence.description+"</div></div>"

    });
}
function color(status){
    if(status==='ONLINE'){return '<div style=\"background-color: lightseagreen;">'}
    else if(status==='AWAY'){return '<div style=\"background-color: yellow;">'}
    else if(status==='ONLINE_CALL'){return '<div style=\"background-color: lightcoral;">'}
    else return '<div style=\"background-color: lightgrey;">'

}
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showMembers(members) {
    members.map(user => {
        const profileImgUrl = user.profileImgUrl?user.profileImgUrl:"https://user-images.githubusercontent.com/61377122/220262114-c6f71e55-3c50-4924-bf6b-fa2c442467c3.png"
        const nickname = user.nickname
        let presence = user.presence
        $("#members").append(member(user.id,profileImgUrl,nickname,presence));
    })
}

function member(userId,profileImgUrl,nickname,presence) {
    console.log(userId)
    return "<div  style='display: flex; padding-bottom: 20px'>" +
        "<div><div><img src=" + profileImgUrl + " alt=\"...\" style=\"border-radius: 50%; width:50px; height: 50px\"></div>" +
        "<div>"+userId+" "+nickname+"</div></div>" +
        "<div id=\'"+userId+"\'>" +
        color(presence.status)+
        "   <div>"+presence.status+"</div>" +
        "   <div >"+presence.description+"</div>" +
        "</div>" + "</div>"
        "</div>"
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#subscribe").click(function () {
        subscribe();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
});