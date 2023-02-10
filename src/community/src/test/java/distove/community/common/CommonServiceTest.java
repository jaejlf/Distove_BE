package distove.community.common;

import distove.community.repository.MemberRepository;
import distove.community.repository.MemberRoleRepository;
import distove.community.repository.ServerRepository;
import distove.community.web.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class CommonServiceTest {

    @MockBean
    public UserClient userClient;

    @Autowired
    public MemberRepository memberRepository;

    @Autowired
    public ServerRepository serverRepository;

    @Autowired
    public MemberRoleRepository memberRoleRepository;

}
