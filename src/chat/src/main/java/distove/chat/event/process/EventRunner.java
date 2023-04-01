package distove.chat.event.process;

import distove.chat.entity.EventFail;
import distove.chat.event.DeleteChannelEvent;
import distove.chat.event.DeleteChannelsEvent;
import distove.chat.event.NotifyNewMessageEvent;
import distove.chat.event.NotifyUnreadsEvent;
import distove.chat.exception.DistoveException;
import distove.chat.repository.EventFailRepository;
import distove.chat.service.ConnectionService;
import distove.chat.service.MessageService;
import distove.chat.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static distove.chat.exception.ErrorCode.EVENT_HANDLE_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRunner {

    private final ConnectionService connectionService;
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final EventFailRepository eventFailRepository;

    public void runDeleteChannel(DeleteChannelEvent event) {
        try {
            connectionService.deleteByChannelId(event.getChannelId());
            messageService.deleteByChannelId(event.getChannelId());
        } catch (Exception e) {
            eventFailRepository.save(new EventFail(event));
            throw new DistoveException(EVENT_HANDLE_ERROR);
        }
    }

    public void runDeleteChannels(DeleteChannelsEvent event) {
        try {
            for (Long channelId : event.getChannelIds()) {
                connectionService.deleteByChannelId(channelId);
                messageService.deleteByChannelId(channelId);
            }
        } catch (Exception e) {
            eventFailRepository.save(new EventFail(event));
            throw new DistoveException(EVENT_HANDLE_ERROR);
        }
    }

    public void runNotifyUnreadOfChannels(NotifyUnreadsEvent event) {
        try {
            notificationService.notifyUnreadOfChannels(event.getUserId(), event.getServerId());
        } catch (Exception e) {
            eventFailRepository.save(new EventFail(event));
            throw new DistoveException(EVENT_HANDLE_ERROR);
        }
    }

    public void runNotifyNewMessage(NotifyNewMessageEvent event) {
        try {
            notificationService.notifyNewMessage(event.getChannelId());
        } catch (Exception e) {
            eventFailRepository.save(new EventFail(event));
            throw new DistoveException(EVENT_HANDLE_ERROR);
        }
    }

}
