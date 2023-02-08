package distove.chat.process;

import distove.chat.event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Queue;

@Slf4j
public class EventQ<T extends Event> {

    private final Queue<T> queue = new LinkedList<>();

    public void add(T event) {
        queue.offer(event);
        log.info(">>>>> ADD " + event.getClass().getSimpleName());
    }

    public T remove() throws InterruptedException {
        while (queue.isEmpty()) {
            Thread.sleep(10000); // WAIT_TIMEOUT : 10sec
        }
        return queue.poll();
    }

}
