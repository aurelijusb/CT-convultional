package lt.banelis.aurelijus;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Aurelijus Banelis
 */
public class Channel {
    public Queue<Boolean> queue = new LinkedBlockingQueue<Boolean>();
    
    public void put(Boolean data) {
        queue.add(data);
    }
    
    public Boolean retrieve() {
        return queue.poll();
    }

    /**
     * @deprecated use GUI converter
     * @param binary 
     */
    public void replace(String binary) {
        queue.clear();
        for (byte b : binary.getBytes()) {
            if (b == '1') {
                queue.add(Boolean.TRUE);
            } else if (b == '0') {
                queue.add(Boolean.FALSE);
            } else {
                //TODO: error
            }
        }
    }

    /**
     * @deprecated use GUI converter
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Boolean bit : queue) {
            if (bit.booleanValue()) {
                result.append("1");
            } else {
                result.append("0");
            }
        }
        return result.toString();
    }
    
    public Iterable<Boolean> getBuffer() {
        return queue;
    }
}
