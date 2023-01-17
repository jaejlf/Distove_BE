package distove.chat.entity;

import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Document(collection = "connection")
public class Connection {

    @Id
    private String id;
    private Long channelId;
    private List<Long> connectedMemberIds;

    public Connection(Long channelId, List<Long> connectedMemberIds) {
        this.channelId = channelId;
        this.connectedMemberIds = connectedMemberIds;
    }

    public void updateConnectedMemberIds(List<Long> connectedMemberIds) {
        this.connectedMemberIds = connectedMemberIds;
    }

}
