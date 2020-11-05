package doubleVectorElements;

public class Multiply extends Thread {
	private final int nrThreads;
	private final int threadId;
	private final int N;
	private final int [] v;

	Multiply(int [] v, int N, int threadId, int nrThreads) {
		this.v = v;
		this.N = N;
		this.threadId = threadId;
		this.nrThreads = nrThreads;
	}

	@Override
	public void run() {
		final int start = threadId * (int)Math.ceil((double) N / nrThreads);
		final int end = (int) Math.min((threadId + 1) * Math.ceil((double) N / nrThreads), N);

		for (int i = start ; i < end ; ++i) {
			v[i] <<= 1;
		}
	}
}
