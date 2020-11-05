package shortestPathsFloyd_Warshall;

public class RoyFloyd extends Thread {
	private final int nrThreads;
	private final int threadId;
	private final int N;
	private final int [][] graph;

	RoyFloyd(int [][] graph, int nrThreads, int threadId, int N) {
		this.nrThreads = nrThreads;
		this.threadId = threadId;
		this.N = N;
		this.graph = graph;
	}

	@Override
	public void run() {
		final int start = threadId * (int)Math.ceil((double) N / nrThreads);
		final int end = (int) Math.min((threadId + 1) * Math.ceil((double) N / nrThreads), N);

		for (int k = start; k < end; k++) {
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					synchronized (Main.class) {
						graph[i][j] = Math.min(graph[i][k] + graph[k][j], graph[i][j]);
					}
				}
			}
		}
	}
}
