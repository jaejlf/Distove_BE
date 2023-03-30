package distove.chat.event;

import distove.chat.entity.EventFail;
import distove.chat.exception.DistoveException;
import distove.chat.repository.EventFailRepository;
import distove.chat.service.ConnectionService;
import distove.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static distove.chat.exception.ErrorCode.EVENT_HANDLE_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRunner {

    private final ConnectionService connectionService;
    private final ChatService chatService;
    private final EventFailRepository eventFailRepository;

    public void runDeleteChannel(DeleteChannelEvent event) {
        try {
            connectionService.deleteByChannelId(event.getChannelId());
//            chatService.deleteByChannelId(event.getChannelId());
        } catch (Exception e) {
            eventFailRepository.save(new EventFail(event));
            throw new DistoveException(EVENT_HANDLE_ERROR);
        }
    }

    public void runDeleteChannels(DeleteChannelsEvent event) {
        try {
            for (Long channelId : event.getChannelIds()) {
                connectionService.deleteByChannelId(channelId);
//                chatService.deleteByChannelId(channelId);
            }
        } catch (Exception e) {
            eventFailRepository.save(new EventFail(event));
            throw new DistoveException(EVENT_HANDLE_ERROR);
        }
    }

}
