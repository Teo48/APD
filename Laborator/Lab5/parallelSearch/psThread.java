package parallelSearch;

import java.util.concurrent.BrokenBarrierException;

public class psThread extends Thread {
	private int left;
	private int right;
	private final int P;
	private final int target;
	private final int threadId;
	private final int [] v;
	private static int leftLimit;
	private static int rightLimit;
	public static int position = -1;

	public psThread(int N, int P, int target, int leftLimit, int rightLimit, int threadId, int [] v) {
		this.P = P;
		this.target = target;
		this.leftLimit = leftLimit;
		this.rightLimit = rightLimit;
		this.threadId = threadId;
		this.v = v.clone();
		left = threadId * (int)Math.ceil((double) N / P);
		right = Math.min((threadId + 1) * (int) Math.ceil((double) N / P), rightLimit);
	}

	@Override
	public void run() {
		if (v[leftLimit] > target  ||  target > v[rightLimit]) {
			return;
		}

		int pos = -1;
		while (position == -1) {
			if (v[left] == target) {
				pos = left;
			}

			if (v[right] == target) {
				pos = right;
			}

			if (v[left] < target && target < v[right]) {
				leftLimit = left;
				rightLimit = right;
			}

			try {
				Main.barrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}

			final double len = rightLimit - leftLimit;

			left = threadId * (int)Math.ceil(len / P) + leftLimit;
			right = Math.min((threadId + 1) * (int) Math.ceil(len / P), rightLimit) + leftLimit;

			if (pos != -1) {
				position = pos;
			} else if (left == leftLimit && right == rightLimit) {
				position = Integer.MIN_VALUE;
			}

			try {
				Main.barrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
	}
}
