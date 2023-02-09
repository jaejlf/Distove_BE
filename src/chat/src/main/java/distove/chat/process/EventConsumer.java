package distove.chat.process;

import distove.chat.event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class EventConsumer<T extends Event> {

    private final EventQ<T> eventQ;
    private final Consumer<T> consumer;

    public EventConsumer(EventQ<T> eventQ, Consumer<T> consumer) {
        this.eventQ = eventQ;
        this.consumer = consumer;
        new Thread(this::consume).start();
        log.info(">>>>> Starting Thread ... ");
    }

    private void consume() {
        while (true) {
            try {
                T event = this.eventQ.remove();
                log.info("REMOVE " + event.getClass().getSimpleName() + " <<<<<");
                this.consumer.accept(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
