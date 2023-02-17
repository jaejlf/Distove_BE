/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
// var ws = new WebSocket('ws://localhost:8080/voice/signaling');

var ws = new WebSocket('wss://distove.onstove.com/voice/signaling');
var participants = {};
var name;

window.onbeforeunload = function () {
    ws.close();
};

ws.onmessage = function (message) {
    var parsedMessage = JSON.parse(message.data);
    console.info('Received message: ' + message.data);

    switch (parsedMessage.type) {
        case 'existingParticipants':
            onExistingParticipants(parsedMessage);
            break;
        case 'newParticipantArrived':
            onNewParticipant(parsedMessage);
            break;
        case 'participantLeft':
            onParticipantLeft(parsedMessage);
            break;
        case 'sdpAnswer':
            receiveVideoResponse(parsedMessage);
            break;
        case 'iceCandidate':
            participants[parsedMessage.userId].rtcPeer.addIceCandidate(parsedMessage.candidateInfo, function (error) {
                if (error) {
                    console.error("Error adding candidate: " + error);

                }
            });
            break;
        default:
            console.error('Unrecognized message', parsedMessage);
    }
}

function register() {
    name = document.getElementById('name').value;
    var room = document.getElementById('roomName').value;

    document.getElementById('room-header').innerText = 'ROOM ' + room;
    document.getElementById('join').style.display = 'none';
    document.getElementById('room').style.display = 'block';

    var message = {
        type: 'joinRoom',
        userId: Number(name),
        channelId: Number(room),
        isCameraOn: false,
        isMicOn: true
    }
    sendMessage(message);
}
function updateVideoInfo(){
    var isCameraOn = document.getElementById('checkbox-camera').checked;
    var isMicOn = document.getElementById('checkbox-mic').checked;
    var message = {
        type: 'updateVideoInfo',
        isCameraOn,
        isMicOn
    }
    sendMessage(message);
}

function onNewParticipant(request) {
    receiveVideo(request.user);
}

function receiveVideoResponse(result) {
    participants[result.userId].rtcPeer.processAnswer(result.sdpAnswer, function (error) {
        if (error) return console.error(error);
    });
}

function callResponse(message) {
    if (message.response != 'accepted') {
        console.info('Call not accepted by peer. Closing call');
        stop();
    } else {
        webRtcPeer.processAnswer(message.sdpAnswer, function (error) {
            if (error) return console.error(error);
        });
    }
}

function onExistingParticipants(msg) {
    var constraints = {
        audio: true,
        video: {
            mandatory: {
                maxWidth: 320,
                maxFrameRate: 15,
                minFrameRate: 15
            }
        }
    };
    console.log(name + " registered in room " + room);
    var participant = new Participant(name);
    participants[name] = participant;
    var video = participant.getVideoElement();

    var options = {
        localVideo: video,
        mediaConstraints: constraints,
        onicecandidate: participant.onIceCandidate.bind(participant)
    }
    participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(options,
        function (error) {
            if (error) {
                return console.error(error);
            }
            this.generateOffer(participant.offerToReceiveVideo.bind(participant));
        });

    console.log("msg.users", msg.users)
    msg.users.forEach(receiveVideo);
}

function resetAllRoom() {
    sendMessage({
        type: 'resetAllRoom'
    })
    ws.close();
}

function leaveRoom() {
    sendMessage({
        type: 'leaveRoom'
    });

    for (var key in participants) {
        participants[key].dispose();
    }

    document.getElementById('join').style.display = 'block';
    document.getElementById('room').style.display = 'none';

    ws.close();

}

function receiveVideo(sender) {
    var senderId = sender.id;
    var participant = new Participant(senderId);
    participants[senderId] = participant;
    var video = participant.getVideoElement();

    var options = {
        remoteVideo: video,
        onicecandidate: participant.onIceCandidate.bind(participant)
    }

    participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(options,
        function (error) {
            if (error) {
                return console.error(error);
            }
            this.generateOffer(participant.offerToReceiveVideo.bind(participant));
        });

}

function onParticipantLeft(request) {
    console.log('Participant ' + request.userId + ' left');
    var participant = participants[request.userId];
    participant.dispose();
    delete participants[request.userId];
}

function sendMessage(message) {
    var jsonMessage = JSON.stringify(message);
    console.log('Sending message: ' + jsonMessage);
    ws.send(jsonMessage);
}
