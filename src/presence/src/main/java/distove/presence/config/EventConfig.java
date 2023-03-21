package distove.presence.config;

import distove.presence.event.SendNewUserConnectionEvent;
import distove.presence.event.UpdateUserPresenceEvent;
import distove.presence.event.EventConsumer;
import distove.presence.event.EventRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static distove.presence.enumerate.EventTopic.getEventQ;

@Configuration
public class EventConfig {

    @Bean
    public EventConsumer<UpdateUserPresenceEvent> UpdateUserPresenceEventConsumer(EventRunner eventRunner) {
        return new EventConsumer<>(getEventQ(UpdateUserPresenceEvent.class), eventRunner::runUpdateUserPresence);
    }

    @Bean
    public EventConsumer<SendNewUserConnectionEvent> SendNewUserConnectionEventConsumer(EventRunner eventRunner) {
        return new EventConsumer<>(getEventQ(SendNewUserConnectionEvent.class), eventRunner::runSendNewUserConnection);
    }

}
