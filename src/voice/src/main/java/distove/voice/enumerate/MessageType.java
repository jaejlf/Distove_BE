package distove.voice.enumerate;

import distove.voice.exception.DistoveException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static distove.voice.exception.ErrorCode.INVALID_MESSAGE_TYPE;

@Getter
@AllArgsConstructor
public enum MessageType {

    JOIN("join"), // 방 입장
    LEAVE("leave"), // 방 나가기
    SDP_OFFER("sdpOffer"), // SDP 정보 전송 - req
    ON_ICE_CANDIDATE("sendIceCandidate"), // ICE Candidate 정보 전송 - req
    UPDATE_SETTING("updateSetting"), // 마이크 & 카메라 설정 변경
    RESET_ALL("resetAll"), // 모든 정보 초기화

    EXISTING_PARTICIPANTS("existingParticipants"), // 참여자 목록
    ICE_CANDIDATE("iceCandidate"), // ICE Candidate 정보 전송 - res
    PARTICIPANT_LEFT("participantLeft"), // 참여자 나가기
    PARTICIPANT_JOINED("participantJoined"), // 참여자 입장
    SDP_ANSWER("sdpAnswer"); // SDP 정보 전송 - res

    private final String message;

    public static MessageType getMessageType(String message) {
        return Arrays.stream(MessageType.values())
                .filter(x -> x.getMessage().equals(message))
                .findFirst()
                .orElseThrow(() -> new DistoveException(INVALID_MESSAGE_TYPE));
    }

}
