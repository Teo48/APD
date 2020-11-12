package oneProducerOneConsumer;

import java.util.concurrent.Semaphore;

/**
 * @author cristian.chilipirea
 *
 */
public class Buffer {
	private int a = -1;
	private static Semaphore consumerSem = new Semaphore(0);
	private static Semaphore producerSem = new Semaphore(1);

	void put(int value) {
//	Cu semafor
//		try {
//			producerSem.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		a = value;
//		consumerSem.release();

		try {
			synchronized (this) {
				if (a != -1) {
					wait();
				}
				a = value;
//				System.out.println("Producatorul a adaugat: " + a);
				notify();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	int get() {
//	Cu semafor
//		try {
//			consumerSem.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		int preReleaseA = a;
//		producerSem.release();
//		return preReleaseA;

		int preReleaseA = -2;
		try {
			synchronized (this) {
				if (a == -1) {
					wait();
				}
				preReleaseA = a;
				a = -1;
//				System.out.println("Consumatorul a consumat: " + preReleaseA);
				notify();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return preReleaseA;
	}
}
