package distove.chat;

import distove.chat.event.Consumer;
import distove.chat.event.EventQ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ChatApplication {

    private static final EventQ eventQ = new EventQ();

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
        new Consumer(eventQ).start();
    }

}
