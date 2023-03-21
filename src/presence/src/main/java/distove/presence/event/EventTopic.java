package distove.presence.event;

import distove.presence.exception.DistoveException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static distove.presence.exception.ErrorCode.EVENT_HANDLE_ERROR;

@Getter
@AllArgsConstructor
public enum EventTopic {

    UPDATE_PRESENCE(UpdatePresenceEvent.class, new EventQ<UpdatePresenceEvent>());

    private final Class<? extends Event> eventType;
    private final EventQ<? extends Event> eventQ;

    @SuppressWarnings("unchecked")
    public static <T extends Event> EventQ<T> getEventQ(Class<T> payEvent) {
        return (EventQ<T>) Arrays.stream(EventTopic.values())
                .filter(event -> event.getEventType().equals(payEvent))
                .findFirst()
                .orElseThrow(() -> new DistoveException(EVENT_HANDLE_ERROR))
                .getEventQ();
    }

}
