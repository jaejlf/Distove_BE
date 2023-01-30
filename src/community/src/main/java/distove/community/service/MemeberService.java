package distove.community.service;

import distove.community.entity.Member;
import distove.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemeberService {
    private final MemberRepository memberRepository;

    public List<Member> getMembersByServerId(Long serverId) {
        List<Member> members = memberRepository.findMembersByServerId(serverId);
        return members;
    }
}
