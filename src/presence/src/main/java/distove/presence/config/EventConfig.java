package distove.presence.config;

import distove.presence.event.SendNewUserConnectionEvent;
import distove.presence.event.UpdateUserPresenceEvent;
import distove.presence.process.EventConsumer;
import distove.presence.service.EventService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static distove.presence.enumerate.EventTopic.getEventQ;

@Configuration
public class EventConfig {
    @Bean
    public EventConsumer<UpdateUserPresenceEvent> UpdateUserPresenceEventConsumer(EventService eventService) {
        return new EventConsumer<>(getEventQ(UpdateUserPresenceEvent.class), eventService::runUpdateUserPresence);
    }
    @Bean
    public EventConsumer<SendNewUserConnectionEvent> SendNewUserConnectionEventConsumer(EventService eventService){
        return new EventConsumer<>(getEventQ(SendNewUserConnectionEvent.class),eventService::runSendNewUserConnection);
    }

}
