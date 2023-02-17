package distove.community.service;

import distove.community.common.CommonServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceTest extends CommonServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    void 동시에_멤버_역할_변경() throws InterruptedException {
        // given
        Long serverId = 1L;
        Long roleId = 3L; // '멤버' 권한

        // when
        new Thread(() -> memberService.updateMemberRole(serverId, roleId, 2L)).start();
        new Thread(() -> memberService.updateMemberRole(serverId, roleId, 1L)).start();

        Thread.sleep(1000);

        // then
        /**
         * Test DB 결과 확인
         * 2번 Server에 OWNER가 최소 1명 이상 존재해야함.
         * 둘다 바뀌었으면 X
         */
    }

}