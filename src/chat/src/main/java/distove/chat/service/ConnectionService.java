package distove.chat.service;

import distove.chat.entity.Connection;
import distove.chat.repository.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    public void createConnection(Long serverId, Long channelId) {
        if (connectionRepository.findByChannelId(channelId).isPresent()) return;
        Connection connection = new Connection(serverId, channelId, new ArrayList<>());
        connectionRepository.save(connection);
    }

    public void deleteByChannelId(Long channelId) {
        connectionRepository.deleteByChannelId(channelId);
    }

}
