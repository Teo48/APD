package multipleProducersMultipleConsumers;

import java.util.concurrent.ArrayBlockingQueue;

public class Buffer {
	ArrayBlockingQueue<Integer> buf = new ArrayBlockingQueue<>(5);

	void put(int value) {
		try {
			buf.put(value);
		} catch (IllegalStateException | InterruptedException | NullPointerException e) {
			e.printStackTrace();
		}
	}

	int get() {
		int retVal = -1;
		try {
			retVal = buf.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return retVal;
	}
}
