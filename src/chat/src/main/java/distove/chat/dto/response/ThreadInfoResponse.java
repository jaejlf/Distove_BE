package distove.chat.dto.response;

import distove.chat.client.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ThreadInfoResponse {

    private String threadName;
    private UserResponse threadStarter;

    public static ThreadInfoResponse of(String threadName, UserResponse threadStarter) {
        return ThreadInfoResponse.builder()
                .threadName(threadName)
                .threadStarter(threadStarter)
                .build();
    }

}
