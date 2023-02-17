package distove.community.dto.response;

import lombok.Builder;
import lombok.Getter;

public class RoleResponse {

    @Getter
    @Builder
    public static class MemberInfo {
        private Long id;
        private String nickname;
        private String roleName;
        private boolean isActive;
    }

    @Getter
    @Builder
    public static class Detail {
        private Long roleId;
        private String roleName;
    }

}