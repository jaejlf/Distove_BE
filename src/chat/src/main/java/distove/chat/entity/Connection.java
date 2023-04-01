package distove.chat.entity;

import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Document(collection = "connection")
public class Connection {

    @Id
    private String id;
    private final Long serverId;
    private final Long channelId;
    private List<Member> members;

    public Connection(Long serverId, Long channelId, List<Member> members) {
        this.serverId = serverId;
        this.channelId = channelId;
        this.members = members;
    }

    public void updateMembers(List<Member> members) {
        this.members = members;
    }

    @Getter
    public static class Member {

        private final Long userId;
        private final LocalDateTime lastReadAt;

        public Member(Long userId) {
            this.userId = userId;
            this.lastReadAt = LocalDateTime.now();
        }

    }

}
