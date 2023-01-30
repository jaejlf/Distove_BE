package distove.voice.dto.request;

import lombok.Getter;

@Getter
public class SdpOfferRequest {
    private String type;
    private Long userId;
    private String sdpOffer;
}
