package distove.chat.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
public class CategoryInfoResponse implements Serializable {
    private Long id;
    private List<Long> channelIds;
}
