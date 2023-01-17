//package distove.chat.common;
//
//import distove.chat.repository.ConnectionRepository;
//import distove.chat.repository.MessageRepository;
//import distove.chat.web.UserClient;
//import distove.chat.web.UserResponse;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//@SpringBootTest
//public class CommonServiceTest {
//
//    @MockBean
//    public UserClient userClient;
//
//    @Autowired
//    public MessageRepository messageRepository;
//
//    @Autowired
//    public ConnectionRepository connectionRepository;
//
//    public UserResponse dummyUser;
//
//    @BeforeEach
//    public void setUp() {
//        dummyUser = new UserResponse(1L, "더미더미", "www.xxx");
//        messageRepository.deleteAll();
//    }
//
//    @AfterEach
//    public void clear() {
//        messageRepository.deleteAll();
//    }
//
//}
