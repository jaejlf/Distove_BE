package distove.chat.event.process;

import distove.chat.event.DeleteChannelEvent;
import distove.chat.event.DeleteChannelsEvent;
import distove.chat.event.NotifyNewMessageEvent;
import distove.chat.event.NotifyUnreadsEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static distove.chat.event.process.EventTopic.getEventQ;

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

    @Bean
    public EventConsumer<NotifyUnreadsEvent> notifyUnreadEventConsumer(EventRunner eventRunner) {
        return new EventConsumer<>(getEventQ(NotifyUnreadsEvent.class), eventRunner::runNotifyUnreadOfChannels);
    }

    @Bean
    public EventConsumer<NotifyNewMessageEvent> notifyNewMessageEventConsumer(EventRunner eventRunner) {
        return new EventConsumer<>(getEventQ(NotifyNewMessageEvent.class), eventRunner::runNotifyNewMessage);
    }

}
