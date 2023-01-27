package distove.voice.entity;

import lombok.Getter;
import org.kurento.client.MediaPipeline;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Room {

    private String id;
    private final Long channelId;
    private final MediaPipeline pipeline;
    private final List<Participant> participants = new ArrayList<>();

    public Room(Long channelId, MediaPipeline pipeline) {
        this.channelId = channelId;
        this.pipeline = pipeline;
    }

}
