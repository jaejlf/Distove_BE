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
    private Long serverId;
    private Long channelId;
    private List<Member> members;

    public static Connection newConnection(Long serverId, Long channelId, List<Member> members) {
        return Connection.builder()
                .serverId(serverId)
                .channelId(channelId)
                .members(members)
                .build();
    }

    public void updateMembers(List<Member> members) {
        this.members = members;
    }

}