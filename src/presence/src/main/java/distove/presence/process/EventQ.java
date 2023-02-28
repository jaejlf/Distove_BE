package distove.presence.process;

import distove.presence.event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Queue;

@Slf4j
public class EventQ<T extends Event> {

    private final Queue<T> queue = new LinkedList<>();

    public void add(T event) {
        queue.offer(event);
        log.info(">>>>> ADD " + event.getClass().getSimpleName() + " QUEUE SIZE = " + size());
    }

    public T remove() throws InterruptedException {
        while (queue.isEmpty()) {
            Thread.sleep(1000); // WAIT_TIMEOUT : 5sec
        }
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }

}
