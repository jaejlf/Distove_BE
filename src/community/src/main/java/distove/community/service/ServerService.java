package distove.community.service;

import distove.community.dto.response.CategoryResponse;
import distove.community.entity.Category;
import distove.community.entity.Channel;
import distove.community.entity.Member;
import distove.community.entity.Server;
import distove.community.enumerate.ChannelType;
import distove.community.exception.DistoveException;
import distove.community.repository.CategoryRepository;
import distove.community.repository.ChannelRepository;
import distove.community.repository.MemberRepository;
import distove.community.repository.ServerRepository;
import distove.community.web.ChatClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static distove.community.dto.response.CategoryResponse.newCategoryResponse;
import static distove.community.entity.Category.newCategory;
import static distove.community.entity.Member.newMember;
import static distove.community.entity.Server.newServer;
import static distove.community.exception.ErrorCode.SERVER_NOT_FOUND_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional

public class ServerService {

    private final ChatClient chatClient;
    private final ServerRepository serverRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ChannelRepository channelRepository;
    private final StorageService storageService;
    private final ChannelService channelService;
    private static final String defaultCategoryName = null;
    private static final String defaultChatCategoryName = "채팅 채널";
    private static final String defaultVoiceCategoryName = "음성 채널";
    private static final String defaultChannelName = "일반";


    /**
     * RP
     * getCategoriesWithChannelsByServerId
     * -> 서버 내 카테고리>채널 리스트 불러오기
     * @param serverId 서버 id
     * @return null
     *
     * review point))
     * 1. 단방향 manyToOne을 사용하다보니 server>category>channel의 깊은 parent child 관계로 loop가 많아짐
     * 2. query 직접 날려서 개발을 주로 했어서 jpa 특유의 id 하나씩 조회하는 방식이 낯설어서 어디까지 Loop가 괜찮고, 어디까지가 안 괜찮은지 잘 모르겠음
     * 3. 일단 loop가 많아서 channelRepository에서 channel을 가져올 때는 가져온 다음에 dto로 다시 바꿔주는 게 아니라, channel에 info라는 interface를 만들어서
     *    구현했는데 잘 사용하는 방법인지 모르겠음
     * 4. 기존에는 category에서 serverId로 가져온 categoryIdlist로 channel에서 조회해서 둘을 매핑해주는 방식으로 개발했었는데,
     * jpa orm을 사용할 때는 어떻게 가져와야 가장 jpa를 잘 따르고 효율적인 방식인지 모르겠음
     */
    public List<CategoryResponse> getCategoriesWithChannelsByServerId(Long serverId) {
        List<Category> categories = categoryRepository.findCategoriesByServerId(serverId);
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for (Category category : categories) {
            categoryResponses.add(newCategoryResponse(
                    category.getId(),
                    category.getName(),
                    channelRepository.findChannelsByCategoryId(category.getId()))
            );
        }
        return categoryResponses;
    }


    /**
     * RP
     * createNewServer
     * -> 새로운 서버 생성 메소드
     *
     * @param userId 생성한 user의 id
     * @param name   서버 이름
     * @param image  서버 이미지 (required = false)
     * @return newServer
     * <p>
     * review point))
     * 1. Server를 만들 때 default Category와 Channel들을 만들어 줘야하는데 이렇게 한 줄씩 추가해 주는 게 최선일지?
     * 2. createNewChannel은 원래 (userId,category)와 같은 형식으로 데이터를 보냈는데, channel을 생성할 때 다른 micro service인 chat으로
     * 채팅방 생성 api를 보내기 위해
     */
    public Server createNewServer(Long userId, String name, MultipartFile image) {
        String imgUrl = null;
        if (!image.isEmpty()) {
            imgUrl = storageService.upload(image);
        }
        Server newServer = serverRepository.save(newServer(name, imgUrl));
        categoryRepository.save(newCategory(defaultCategoryName, newServer)); //Category없는 Channel을 위한 default Category

        // 아래 네개는 실제 디스코드에서 서버를 처음 만들면 default로 생성해주는 카테고리 두 개와 채널 두 개
        Category defaultChatCategory = categoryRepository.save(newCategory(defaultChatCategoryName, newServer));
        Category defaultVoiceCategory = categoryRepository.save(newCategory(defaultVoiceCategoryName, newServer));
        channelService.createNewChannel(userId, defaultChannelName, defaultChatCategory.getId(), ChannelType.CHAT.getCode());
        channelService.createNewChannel(userId, defaultChannelName, defaultVoiceCategory.getId(), ChannelType.VOICE.getCode());

        memberRepository.save(newMember(newServer, userId)); //지금 유저 멤버로 추가

        return newServer;

    }


    /**
     * RP
     * updateServer
     * -> 서버의 프로필 이미지, 이름 업데이트 메소드
     *
     * @param serverId
     * @param name
     * @param imgUrl   기존 이미지 url (required = false)
     * @param image    새로 업데이트 하고싶은 이미지 (required = false)
     * @return server
     * <p>
     * review point))
     * 1. image 받아오는 분기를
     * a) 기존 이미지를 유지할 때: image는 비우고, imgUrl은 원래 url은 다시 보낸다
     * b) 새로운 이미지로 변경하고 싶을 때: imgUrl에 상관없이(기존 이미지를 삭제하고 새로 올리더라도), 새로운 image를 넣는다
     * c) 기존 이미지를 삭제하고 싶을 때: image도 비우고, imgUrl을 비워서 보낸다
     * 로 설정하고 아래와같이 코드를 짰는데 생각하지 못한 부분이 있을까요?
     */
    public Server updateServer(Long serverId, String name, String imgUrl, MultipartFile image) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));
        if (!image.isEmpty()) {
            imgUrl = storageService.upload(image);
        }
        server.updateServer(name, imgUrl);

        return server;
    }


    public List<Server> getServersByUserId(Long userId) {

        List<Member> members = memberRepository.findMembersByUserId(userId);
        List<Server> servers = new ArrayList<>();
        for (Member m : members) {
            servers.add(m.getServer());
        }
        return servers;

    }


    /**
     * CR
     * deleteServerById
     * -> 서버 삭제 - 카테고리 삭제 - 채널 삭제, 멤버 삭제
     * @param serverId
     *
     * review point))
     * 1. manyToOne을 사용하니 cascade 사용을 못하는데, server 조회에서의 loop 중첩 문제도 그렇고, 서버-카테고리-채널 양방향을 사용하는 게 맞는지
     */

    public void deleteServerById(Long serverId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));
        List<Category> categories = categoryRepository.findCategoriesByServerId(serverId);
        for (Category category : categories) {
            // 이 아래 루프는 chatClient에서 channel을 clear하는 api를 리스트로 처리하는 api로 수정해서 곧 작성해서 넘겨주면 없앨 예정입니다.
            for (Channel channel : channelRepository.deleteAllByCategoryId(category.getId())) {
                if (channel.getChannelTypeId().equals(ChannelType.CHAT.getCode())) {
                    //채팅채널일 경우 chat service에 데이터 삭제 요청
                    chatClient.clearAll(channel.getId());
                }
            }
        }
        memberRepository.deleteAllByServerId(serverId);
        categoryRepository.deleteAllByServerId(serverId);
        serverRepository.deleteById(serverId);
    }
}
