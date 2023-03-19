package distove.voice.entity;

import lombok.Getter;
import org.kurento.client.MediaPipeline;

@Getter
public class VoiceRoom {

    private final Long channelId;
    private final MediaPipeline pipeline;

    public VoiceRoom(Long channelId, MediaPipeline pipeline) {
        this.channelId = channelId;
        this.pipeline = pipeline;
    }

}
