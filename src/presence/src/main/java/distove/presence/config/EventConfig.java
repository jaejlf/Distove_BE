package distove.presence.config;

import distove.presence.event.UpdateUserPresenceEvent;
import distove.presence.process.EventConsumer;
import distove.presence.service.EventService;
import org.springframework.context.annotation.Bean;

import static distove.presence.enumerate.EventTopic.getEventQ;

public class EventConfig {
    @Bean
    public EventConsumer<UpdateUserPresenceEvent> newChannelEventEventConsumer(EventService eventService) {
        return new EventConsumer<>(getEventQ(UpdateUserPresenceEvent.class), eventService::runUpdateUserPresenceEvent);
    }

}
