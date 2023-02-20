package distove.voice.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
    JOIN_ROOM("joinRoom"),
    SDP_OFFER("sdpOffer"),
    LEAVE_ROOM("leaveRoom"),
    ADD_ICE_CANDIDATE("addIceCandidate"),
    EXISTING_PARTICIPANTS("existingParticipants"),
    ICE_CANDIDATE("iceCandidate"),
    PARTICIPANT_LEFT("participantLeft"),
    NEW_PARTICIPANT_ARRIVED("newParticipantArrived"),
    SDP_ANSWER("sdpAnswer"),
    UPDATED_VIDEO_INFO("updatedVideoInfo"),
    UPDATE_VIDEO_INFO("updateVideoInfo");
    private final String type;
}
