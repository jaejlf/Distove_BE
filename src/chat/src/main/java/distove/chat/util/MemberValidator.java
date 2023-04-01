package distove.chat.util;

import distove.chat.client.CommunityClient;
import distove.chat.exception.DistoveException;
import lombok.RequiredArgsConstructor;

import static distove.chat.exception.ErrorCode.MEMBER_NOT_FOUND_ERROR;

@RequiredArgsConstructor
public class MemberValidator {

    private final CommunityClient communityClient;

    public void validateMember(Long userId, Long channelId) {
        if (!communityClient.isMember(channelId, userId)) throw new DistoveException(MEMBER_NOT_FOUND_ERROR);
    }

}
