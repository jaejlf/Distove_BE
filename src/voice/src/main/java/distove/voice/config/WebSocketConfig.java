package distove.voice.config;

import distove.voice.handler.SignalingHandler;
import distove.voice.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final SignalingService signalingService;
    private final ParticipantRepository participantRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SignalingHandler(signalingService), "/signaling")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }

}