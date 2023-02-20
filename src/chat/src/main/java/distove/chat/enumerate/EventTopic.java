package distove.chat.enumerate;

import distove.chat.event.DelChannelEvent;
import distove.chat.event.DelChannelListEvent;
import distove.chat.event.Event;
import distove.chat.process.EventQ;
import distove.chat.exception.DistoveException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static distove.chat.exception.ErrorCode.EVENT_HANDLE_ERROR;

@Getter
@AllArgsConstructor
public enum EventTopic {

    DEL_CHANNEL(DelChannelEvent.class, new EventQ<DelChannelEvent>()),
    DEL_CHANNEL_LIST(DelChannelListEvent.class, new EventQ<DelChannelListEvent>());

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
