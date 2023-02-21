package distove.community.service;

import distove.community.dto.response.CategoryInfoResponse;
import distove.community.entity.Channel;
import distove.community.repository.ChannelRepository;
import distove.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebService {

    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;

    public List<CategoryInfoResponse> getCategoryIds(List<Long> channelIds) {
        Map<Long, List<Long>> map = new HashMap<>();
        for (Long channelId : channelIds) {
            Channel channel = channelRepository.findById(channelId).get();
            Long categoryId = channel.getCategory().getId();

            List<Long> list = map.getOrDefault(categoryId, new ArrayList<>());
            list.add(channelId);
            map.put(categoryId, list);
        }

        return map.entrySet().stream()
                .map(x -> CategoryInfoResponse.of(x.getKey(), x.getValue()))
                .collect(Collectors.toList());
    }

    public CategoryInfoResponse getCategoryId(Long channelId) {
        Channel channel = channelRepository.findById(channelId).get();
        Long categoryId = channel.getCategory().getId();
        return CategoryInfoResponse.of(categoryId, new ArrayList<>(List.of(channelId)));
    }

    public boolean checkIsMember(Long channelId, Long userId) {
        Long serverId = channelRepository.findById(channelId).get().getCategory().getServer().getId();
        return memberRepository.existsByUserIdAndServerId(userId, serverId);
    }

}
