package distove.presence.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePresenceEvent implements Event {

    private Long userId;
    private String serviceInfo;

    public static UpdatePresenceEvent of(Long userId, String serviceInfo){
        return UpdatePresenceEvent.builder().userId(userId).serviceInfo(serviceInfo).build();
    }

}
