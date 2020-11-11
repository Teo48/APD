package multipleProducersMultipleConsumersNBuffer;

import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * @author Gabriel Gutu <gabriel.gutu at upb.ro>
 *
 */
public class Buffer {
    
    Queue queue;
    Semaphore consumerSem = new Semaphore(0);
    Semaphore producerSem = new Semaphore(3);

    public Buffer(int size) {
        queue = new LimitedQueue(size);
    }

	void put(int value) {
        try {
        	producerSem.acquire();
		} catch (InterruptedException e) {
        	e.printStackTrace();
		}

        synchronized (Main.class) {
			queue.add(value);
		}

        consumerSem.release();
	}

	int get() {
    	try {
			consumerSem.acquire();
		} catch (InterruptedException e) {
    		e.printStackTrace();
		}

    	int productBeforeRelease;

    	synchronized (Main.class) {
    		productBeforeRelease = (int) queue.poll();
		}

    	producerSem.release();
    	return productBeforeRelease;
	}
}
