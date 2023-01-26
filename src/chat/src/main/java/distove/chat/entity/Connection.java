package distove.chat.entity;

import lombok.Builder;
import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Builder
@Document(collection = "connection")
public class Connection {

    @Id
    private String id;
    private Long channelId;
    private List<Long> connectedMemberIds;

    public static Connection newConnection(Long channelId, List<Long> connectedMemberIds) {
        return Connection.builder()
                .channelId(channelId)
                .connectedMemberIds(connectedMemberIds)
                .build();
    }

    public void updateConnectedMemberIds(List<Long> connectedMemberIds) {
        this.connectedMemberIds = connectedMemberIds;
    }

}