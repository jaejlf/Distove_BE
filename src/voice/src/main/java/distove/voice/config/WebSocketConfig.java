package distove.voice.config;

import distove.voice.controller.SignalingController;
import distove.voice.repository.ParticipantRepository;
import distove.voice.service.SignalingService;
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
        registry.addHandler(new SignalingController(signalingService), "/signaling")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }

}