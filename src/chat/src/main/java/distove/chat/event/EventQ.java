package distove.chat.event;

import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class EventQ<T extends Event> {

    private final Queue<T> queue = new LinkedBlockingQueue<>();

    public void add(T event) {
        queue.offer(event);
    }

    public T remove() throws InterruptedException {
        while (queue.isEmpty()) {
            Thread.sleep(10000);
        }
        return queue.poll();
    }

}
