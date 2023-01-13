package distove.community.service;

import distove.community.entity.CategoryChannel;
import distove.community.entity.Channel;
import distove.community.entity.ChannelRequest;
import distove.community.repository.CategoryChannelRepository;
import distove.community.repository.CategoryRepository;
import distove.community.repository.ChannelRepository;
import distove.community.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ServerRepository serverRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryChannelRepository categoryChannelRepository;




    public void postNewChannel(ChannelRequest channelRequest){

        Channel newChannel = new Channel(
                channelRequest.getName(),
                channelRequest.getChannelTypeId(),
                serverRepository.findById(channelRequest.getServerId()).get()
        );
        CategoryChannel newCategoryChannel = new CategoryChannel(
                categoryRepository.findById(channelRequest.getCategoryId()).get(),
                newChannel
        );
        channelRepository.save(newChannel);
        categoryChannelRepository.save(newCategoryChannel);



    }

}
