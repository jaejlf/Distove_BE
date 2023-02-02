package distove.chat.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Slf4j
@Component
public class EventQ {

    private static final Queue<Event> queue = new LinkedList<>();
    private final int WAIT_TIMEOUT = 10000;  // 10 sec

    synchronized public boolean addQ(Event event) {
        queue.add(event);
        try {
            notifyAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    synchronized public Event removeQ() {
        return queue.poll();
    }

    synchronized public void waitQ() throws InterruptedException {
        wait(WAIT_TIMEOUT);
    }

    synchronized public boolean isEmpty() {
        return queue.isEmpty();
    }

}
