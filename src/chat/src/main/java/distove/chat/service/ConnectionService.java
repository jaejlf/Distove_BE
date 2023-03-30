package distove.chat.service;

import distove.chat.entity.Connection;
import distove.chat.entity.Member;
import distove.chat.exception.DistoveException;
import distove.chat.repository.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static distove.chat.exception.ErrorCode.CHANNEL_NOT_FOUND_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final ChatService chatService;

    public Member getOrCreateMember(Long userId, Long channelId) {
        Connection connection = getConnection(channelId);
        List<Member> members = connection.getMembers();
        return members.stream()
                .filter(x -> x.getUserId().equals(userId))
                .findFirst()
                .orElseGet(() -> {
                    Member newMember = saveMember(userId, connection, members);
                    chatService.publishWelcomeMessage(userId, channelId);
                    return newMember;
                });
    }

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

    private Member saveMember(Long userId, Connection connection, List<Member> members) {
        Member member = new Member(userId);
        members.add(member);
        connection.updateMembers(members);
        connectionRepository.save(connection);
        return member;
    }

}
