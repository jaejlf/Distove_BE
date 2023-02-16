package distove.presence.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserPresenceEvent implements Event {
    private  Long userId;

    public static UpdateUserPresenceEvent of(Long userId){
        return UpdateUserPresenceEvent.builder().userId(userId).build();
    }
}
