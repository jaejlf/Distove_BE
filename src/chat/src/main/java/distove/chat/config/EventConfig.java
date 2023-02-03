package distove.chat.config;

import distove.chat.event.DelChannelEvent;
import distove.chat.process.EventConsumer;
import distove.chat.service.EventService;
import distove.chat.event.NewChannelEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static distove.chat.enumerate.EventTopic.*;

@Configuration
public class EventConfig {

    @Bean
    public EventConsumer<NewChannelEvent> newChannelEventEventConsumer(EventService eventService) {
        return new EventConsumer<>(getEventQ(NewChannelEvent.class), eventService::runNewChannel);
    }

    @Bean
    public EventConsumer<DelChannelEvent> delChannelEventEventConsumer(EventService eventService) {
        return new EventConsumer<>(getEventQ(DelChannelEvent.class), eventService::runDelChannel);
    }

}
