package distove.presence.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserPresenceEvent implements Event {
    private Long userId;
    private String serviceInfo;

    public static UpdateUserPresenceEvent of(Long userId,String serviceInfo){
        return UpdateUserPresenceEvent.builder().userId(userId).serviceInfo(serviceInfo).build();
    }
}
