package oneProducerOneConsumer;

import java.util.concurrent.Semaphore;

/**
 * @author cristian.chilipirea
 *
 */
public class Buffer {
	private int a;
	private static Semaphore consumerSem = new Semaphore(0);
	private static Semaphore producerSem = new Semaphore(1);

	void put(int value) {
		try {
			producerSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		a = value;
		consumerSem.release();
	}

	int get() {
		try {
			consumerSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int preReleaseA = a;
		producerSem.release();
		return preReleaseA;
	}
}
