package distove.chat.service;

import distove.chat.common.CommonServiceTest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.TypedUserResponse;
import distove.chat.entity.Message;
import distove.chat.exception.DistoveException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static distove.chat.entity.Message.newMessage;
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
                    dummyUser.getId(), TEXT, null, null, "{{MESSAGE CONTENT}}"
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
                    dummyUser.getId(), WELCOME, null, null, "{{MESSAGE CONTENT}}"
            );

            //when & then
            assertThatThrownBy(() -> messageService.publishMessage(channelId, request))
                    .isInstanceOf(DistoveException.class)
                    .hasMessageContaining("잘못된 메시지 타입입니다.");
        }

        @Test
        void 수정_삭제_권한_없음() {
            // given
            Long channelId = 1L;
            given(userClient.getUser(any())).willReturn(dummyUser);
            Message message = messageRepository.save(newMessage(channelId, 99L, TEXT, "{{MESSAGE CONTENT}}"));
            MessageRequest request = new MessageRequest(
                    dummyUser.getId(), DELETED, message.getId(), null, "{{MESSAGE CONTENT}}"
            );

            //when & then
            assertThatThrownBy(() -> messageService.publishMessage(channelId, request))
                    .isInstanceOf(DistoveException.class)
                    .hasMessageContaining("수정/삭제 권한이 없습니다.");
        }

    }

    @DisplayName("메시지 작성 중")
    @Test
    void publishTypedUserTest() {
        // given
        Long userId = dummyUser.getId();
        given(userClient.getUser(any())).willReturn(dummyUser);

        // when
        TypedUserResponse result = messageService.publishTypedUser(userId);

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

    @DisplayName("메시지 리스트 조회")
    @Nested
    class GetMessagesByChannelIdTest {

        @Test
        void 성공() {
            // given
            Long channelId = 1L;
            given(userClient.getUser(any())).willReturn(dummyUser);

            // when
            List<MessageResponse> result = messageService.getMessagesByChannelId(dummyUser.getId(), channelId);

            // then
            MessageResponse expected = MessageResponse.builder()
                    .type(WELCOME)
                    .content(dummyUser.getNickname())
                    .hasAuthorized(false)
                    .build();

            assertAll(
                    () -> assertThat(result.get(0).getType()).isEqualTo(expected.getType()),
                    () -> assertThat(result.get(0).getContent()).isEqualTo(expected.getContent()),
                    () -> assertThat(result.get(0).getHasAuthorized()).isEqualTo(expected.getHasAuthorized())
            );
        }

        @Test
        void 존재하지_않는_채널() {
            // given
            Long channelId = 99L;
            given(userClient.getUser(any())).willReturn(dummyUser);

            //when & then
            assertThatThrownBy(() -> messageService.getMessagesByChannelId(dummyUser.getId(), channelId))
                    .isInstanceOf(DistoveException.class)
                    .hasMessageContaining("존재하지 않는 채널입니다.");
        }

    }

}