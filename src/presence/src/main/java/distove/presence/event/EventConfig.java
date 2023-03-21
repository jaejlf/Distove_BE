package distove.presence.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static distove.presence.event.EventTopic.getEventQ;

@Configuration
public class EventConfig {

    @Bean
    public EventConsumer<UpdatePresenceEvent> UpdateUserPresenceEventConsumer(EventRunner eventRunner) {
        return new EventConsumer<>(getEventQ(UpdatePresenceEvent.class), eventRunner::runUpdatePresence);
    }

}
