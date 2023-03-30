package distove.chat.service;

import distove.chat.entity.Connection;
import distove.chat.exception.DistoveException;
import distove.chat.repository.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static distove.chat.exception.ErrorCode.CHANNEL_NOT_FOUND_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
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

    public Connection getConnection(Long channelId) {
        return connectionRepository.findByChannelId(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
    }

    public void validateChannel(Long channelId) {
        getConnection(channelId);
    }

}
