package distove.presence.event.process;

import distove.presence.event.UpdatePresenceEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static distove.presence.event.process.EventTopic.getEventQ;

@Configuration
public class EventConfig {

    @Bean
    public EventConsumer<UpdatePresenceEvent> UpdateUserPresenceEventConsumer(EventRunner eventRunner) {
        return new EventConsumer<>(getEventQ(UpdatePresenceEvent.class), eventRunner::runUpdatePresence);
    }

}
