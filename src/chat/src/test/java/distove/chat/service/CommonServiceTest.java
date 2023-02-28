package distove.chat.service;

import distove.chat.entity.Connection;
import distove.chat.entity.Member;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import distove.chat.web.CommunityClient;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static distove.chat.entity.Connection.newConnection;
import static distove.chat.entity.Member.newMember;

@SpringBootTest
class CommonServiceTest {

    @MockBean
    public UserClient userClient;

    @MockBean
    public CommunityClient communityClient;

    @Autowired
    public MessageRepository messageRepository;

    @Autowired
    public ConnectionRepository connectionRepository;

    public UserResponse dummyUser;
    public Member unreadMember;
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");

    @BeforeEach
    public void setUp() {
        dummyUser = new UserResponse(1L, "더미더미", "www.xxx");
        Connection connection = connectionRepository.save(newConnection(1L, 1L, new ArrayList<>()));

        // 안읽메 멤버 저장
        List<Member> members = connection.getMembers();
        Member member = newMember(10L);
        members.add(member);
        connection.updateMembers(members);
        connectionRepository.save(connection);
        unreadMember = members.stream()
                .filter(x -> x.getUserId().equals(10L)).findFirst()
                .orElse(null);
    }

    @AfterEach
    public void clear() {
        messageRepository.deleteAll();
        connectionRepository.deleteAll();
    }

    @Test
    void common() {
        Assertions.assertThat(connectionRepository.findAll()).hasSize(1);
    }

}
