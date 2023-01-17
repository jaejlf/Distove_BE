package distove.chat.service;

import distove.chat.common.CommonServiceTest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.exception.DistoveException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static distove.chat.enumerate.MessageType.TEXT;
import static distove.chat.enumerate.MessageType.WELCOME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("Message 서비스 테스트")
class MessageServiceTest extends CommonServiceTest {

    @Autowired
    private MessageService messageService;

    @DisplayName("메시지 전송")
    @Nested
    class PublishMessageTest {

        @Test
        void 성공() {
            // given
            Long channelId = 1L;
            given(userClient.getUser(any())).willReturn(dummyUser);
            MessageRequest request = new MessageRequest(
                    null, null, TEXT, "메시지 가라가랏"
            );

            //when
            messageService.publishMessage(dummyUser.getId(), channelId, request);

            // then
            assertThat(messageRepository.findAll().size()).isEqualTo(1);
        }

        @Test
        void 허용되지_않은_타입() {
            // given
            Long channelId = 1L;
            given(userClient.getUser(any())).willReturn(dummyUser);
            MessageRequest request = new MessageRequest(
                    null, null, WELCOME, "메시지 내용 ~!"
            );

            //when & then
            assertThatThrownBy(() -> messageService.publishMessage(dummyUser.getId(), channelId, request))
                    .isInstanceOf(DistoveException.class)
                    .hasMessageContaining("잘못된 메시지 타입입니다.");
        }

    }

}