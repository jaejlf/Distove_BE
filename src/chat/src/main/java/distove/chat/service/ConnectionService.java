package distove.chat.service;

import distove.chat.entity.Connection;
import distove.chat.repository.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static distove.chat.entity.Connection.newConnection;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    public void createConnection(Long serverId, Long channelId) {
        if (connectionRepository.findByChannelId(channelId).isPresent()) return;
        Connection connection = newConnection(serverId, channelId, new ArrayList<>());
        connectionRepository.save(connection);
    }

    public void clear(Long channelId) {
        connectionRepository.deleteAllByChannelId(channelId);
    }

    public void clearAll(List<Long> channelIds) {
        for (Long channelId : channelIds) {
            connectionRepository.deleteAllByChannelId(channelId);
        }
    }

}