package distove.chat.service;

import distove.chat.entity.Connection;
import distove.chat.repository.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static distove.chat.entity.Connection.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    public void createConnection(Long channelId, Long userId) {
        List<Long> connectedMemberIds = new ArrayList<>();
        connectedMemberIds.add(userId);

        Connection connection = newConnection(channelId,connectedMemberIds);
        connectionRepository.save(connection);
    }

}