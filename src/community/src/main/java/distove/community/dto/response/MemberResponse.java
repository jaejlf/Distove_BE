package distove.community.dto.response;

import distove.community.entity.Member;
import distove.community.web.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String nickname;
    private String profileImgUrl;
    private Auth auth;

    public static MemberResponse of(UserResponse userResponse, Member member) {
        return MemberResponse.builder()
                .id(userResponse.getId())
                .nickname(userResponse.getNickname())
                .profileImgUrl(userResponse.getProfileImgUrl())
                .auth(Auth.of(member))
                .build();
    }

    @Getter
    @Builder
    public static class Auth {
        private boolean canDeleteServer;
        private boolean canManageServer;
        private boolean canManageChannel;
        private boolean canUpdateMemberRole;

        public static Auth of(Member member) {
            return Auth.builder()
                    .canDeleteServer(member.getRole().isCanDeleteServer())
                    .canManageServer(member.getRole().isCanManageServer())
                    .canManageChannel(member.getRole().isCanManageChannel())
                    .canUpdateMemberRole(member.getRole().isCanUpdateMemberRole())
                    .build();
        }
    }

}
