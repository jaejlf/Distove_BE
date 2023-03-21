package distove.voice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class MessageUtil {

    public <T> void sendMessage(WebSocketSession session, T object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(object);
            session.sendMessage(new TextMessage(jsonInString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
