package lt.banelis.aurelijus;

import java.util.Collection;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Aurelijus Banelis
 */
public class Channel {
    public Queue<Boolean> queue = new LinkedBlockingQueue<Boolean>();
    public Queue<Boolean> queueOriginal = new LinkedBlockingQueue<Boolean>();
    private double noise = 0;
    private Random randomGenerator = new Random();
    
    public void put(Boolean data) {
        double random = randomGenerator.nextFloat();
        queueOriginal.add(data);
        if (random <= noise) {
            data = !data;
        }
        queue.add(data);
    }
    
    public Boolean retrieve() {
        queueOriginal.poll();
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

    public void setNoise(double noise) {
        this.noise = noise;
    }
    
    public Iterable<Boolean> getBuffer() {
        return queue;
    }
    
    public Collection<Boolean> getOriginalBuffer() {
        return queueOriginal;
    }
}
