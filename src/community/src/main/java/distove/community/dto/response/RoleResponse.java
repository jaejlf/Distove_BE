package distove.community.dto.response;

import lombok.Builder;
import lombok.Getter;

public class RoleResponse {

    @Getter
    @Builder
    public static class Info {
        private Long userId;
        private String nickname;
        private String roleName;
    }

    @Getter
    @Builder
    public static class Detail {
        private Long roleId;
        private String roleName;
        private boolean isActive;
    }

}