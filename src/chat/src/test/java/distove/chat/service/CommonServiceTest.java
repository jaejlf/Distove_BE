package distove.chat.service;

import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;

import static distove.chat.entity.Connection.newConnection;

@SpringBootTest
class CommonServiceTest {

    @MockBean
    public UserClient userClient;

    @Autowired
    public MessageRepository messageRepository;

    @Autowired
    public ConnectionRepository connectionRepository;

    public UserResponse dummyUser;

    @BeforeEach
    public void setUp() {
        dummyUser = new UserResponse(1L, "더미더미", "www.xxx");
        connectionRepository.save(newConnection(1L, 1L, new ArrayList<>()));
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