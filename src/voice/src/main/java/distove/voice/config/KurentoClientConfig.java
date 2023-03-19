package distove.voice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KurentoClientConfig {

    @Bean
    public KurentoClient kurentoClient() {
        return KurentoClient.create("ws://localhost:8888/kurento");
    }

}
