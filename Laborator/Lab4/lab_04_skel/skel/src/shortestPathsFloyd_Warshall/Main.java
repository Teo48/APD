package shortestPathsFloyd_Warshall;
/**
 * @author cristian.chilipirea
 *
 */
public class Main {

	public static void main(String[] args) {
		int M = 9;
		int graph[][] = { { 0, 1, M, M, M }, 
				          { 1, 0, 1, M, M }, 
				          { M, 1, 0, 1, 1 }, 
				          { M, M, 1, 0, M },
				          { M, M, 1, M, 0 } };
		
		// Parallelize me (You might want to keep the original code in order to compare)
//		for (int k = 0; k < 5; k++) {
//			for (int i = 0; i < 5; i++) {
//				for (int j = 0; j < 5; j++) {
//					graph[i][j] = Math.min(graph[i][k] + graph[k][j], graph[i][j]);
//				}
//			}
//		}

		int cores = Runtime.getRuntime().availableProcessors();
		RoyFloyd [] threads = new RoyFloyd[cores];

		for (int i = 0 ; i < cores ; ++i) {
			threads[i] = new RoyFloyd(graph, cores, i, 5);
			threads[i].start();
		}

		for (var t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				System.out.print(graph[i][j] + " ");
			}
			System.out.println();
		}
	}
}
