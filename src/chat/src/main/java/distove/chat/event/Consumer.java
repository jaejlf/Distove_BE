package distove.chat.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Consumer extends Thread {

    private final EventQ eventQ;

    public void run() {
        try {
            log.info("컨슈머 스레드 시작 ...");

            while (true) {
                if (eventQ.isEmpty()) {
                    eventQ.waitQ();
                    continue;
                }

                Event event = eventQ.removeQ();
                log.info(event.getTopic() + " / " + event.getChannelId() + " <<<<<");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

