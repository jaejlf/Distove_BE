package distove.chat.config;

import distove.chat.event.DelChannelEvent;
import distove.chat.event.DelChannelListEvent;
import distove.chat.process.EventConsumer;
import distove.chat.service.EventService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static distove.chat.enumerate.EventTopic.*;

@Configuration
public class EventConfig {

    @Bean
    public EventConsumer<DelChannelEvent> delChannelEventEventConsumer(EventService eventService) {
        return new EventConsumer<>(getEventQ(DelChannelEvent.class), eventService::runDelChannel);
    }

    @Bean
    public EventConsumer<DelChannelListEvent> delChannelListEventEventConsumer(EventService eventService) {
        return new EventConsumer<>(getEventQ(DelChannelListEvent.class), eventService::runDelChannelList);
    }

}
