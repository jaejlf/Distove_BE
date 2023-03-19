package distove.chat.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static distove.chat.enumerate.EventTopic.getEventQ;

@Configuration
public class EventConfig {

    @Bean
    public EventConsumer<DeleteChannelEvent> deleteChannelEventConsumer(EventRunner eventRunner) {
        return new EventConsumer<>(getEventQ(DeleteChannelEvent.class), eventRunner::runDeleteChannel);
    }

    @Bean
    public EventConsumer<DeleteChannelsEvent> deleteChannelsEventConsumer(EventRunner eventRunner) {
        return new EventConsumer<>(getEventQ(DeleteChannelsEvent.class), eventRunner::runDeleteChannels);
    }

}
