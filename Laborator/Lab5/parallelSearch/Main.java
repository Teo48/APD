package parallelSearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

public class Main {
	public static CyclicBarrier barrier;

	public static void main(String [] args) throws IOException {
		System.out.println("Introduce the number of threads: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int P = Integer.parseInt(br.readLine());

		final int N = 1000000;
		barrier = new CyclicBarrier(P);
		psThread [] threads = new psThread[P];
		int [] v = new int [N];
		System.out.println("Filling array with power of 2s");
		Arrays.setAll(v, i -> (i << 1));
		System.out.println("Done. Search for a number: ");
		final int target = Integer.parseInt(br.readLine());

		for (int i = 0 ; i < P ; ++i) {
			threads[i] = new psThread(N, P, target, 0, N - 1, i, v);
			threads[i].start();
		}

		for (var i : threads) {
			try {
				i.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (psThread.position >= 0) {
			System.out.println("Found " + target + " at index " + psThread.position);
		} else {
			System.out.println("Target not found!");
		}
	}
}
