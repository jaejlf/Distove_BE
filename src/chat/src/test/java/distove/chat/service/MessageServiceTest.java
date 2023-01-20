package distove.chat.service;

import distove.chat.common.CommonServiceTest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.TypedUserResponse;
import distove.chat.exception.DistoveException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static distove.chat.enumerate.MessageType.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
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
                    dummyUser.getId(), TEXT, null, null, "this is 메시지!"
            );

            //when
            MessageResponse result = messageService.publishMessage(channelId, request);

            // then
            MessageResponse expected = MessageResponse.builder()
                    .type(request.getType())
                    .content(request.getContent())
                    .writer(dummyUser)
                    .hasAuthorized(true)
                    .build();

            assertThat(messageRepository.findAll().size()).isEqualTo(1);
            assertAll(
                    () -> assertThat(result.getType()).isEqualTo(expected.getType()),
                    () -> assertThat(result.getContent()).isEqualTo(expected.getContent()),
                    () -> assertThat(result.getWriter()).isEqualTo(expected.getWriter()),
                    () -> assertThat(result.getHasAuthorized()).isEqualTo(expected.getHasAuthorized())
            );
        }

        @Test
        void 허용되지_않은_타입() {
            // given
            Long channelId = 1L;
            given(userClient.getUser(any())).willReturn(dummyUser);
            MessageRequest request = new MessageRequest(
                    dummyUser.getId(), WELCOME, null, null, "welcome!"
            );

            //when & then
            assertThatThrownBy(() -> messageService.publishMessage(channelId, request))
                    .isInstanceOf(DistoveException.class)
                    .hasMessageContaining("잘못된 메시지 타입입니다.");
        }

    }

    @DisplayName("메시지 작성 중")
    @Test
    void 성공() {
        // given
        Long userId = dummyUser.getId();
        given(userClient.getUser(any())).willReturn(dummyUser);
        MessageRequest request = new MessageRequest(
                dummyUser.getId(), TEXT, null, null, "this is 메시지!"
        );

        // when
        TypedUserResponse result = messageService.beingTyped(userId);

        //then
        TypedUserResponse expected = TypedUserResponse.builder()
                .type(TYPING)
                .content(dummyUser.getNickname())
                .build();

        assertAll(
                () -> assertThat(result.getType()).isEqualTo(expected.getType()),
                () -> assertThat(result.getContent()).isEqualTo(expected.getContent())
        );

    }

}