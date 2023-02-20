package distove.presence.event;

import distove.presence.enumerate.PresenceType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendNewUserConnectionEvent implements Event {
    private Long userId;
    private PresenceType presenceType;

    public static SendNewUserConnectionEvent of(Long userId,PresenceType presenceType){
        return SendNewUserConnectionEvent.builder()
                .userId(userId)
                .presenceType(presenceType)
                .build();
    }
}
