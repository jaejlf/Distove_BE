package distove.chat.entity;

import distove.chat.event.Event;
import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "event-fail")
public class EventFail {

    @Id
    private String id;
    private Event event;

    public EventFail(Event event) {
        this.event = event;
    }

}
