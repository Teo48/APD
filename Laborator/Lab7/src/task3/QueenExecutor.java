package task3;

import java.util.concurrent.ExecutorService;

public class QueenExecutor implements Runnable{
	private final ExecutorService tpe;
	private final int step;
	private final int [] queenPosition;

	public QueenExecutor(ExecutorService tpe, int step, int[] queenPosition) {
		this.tpe = tpe;
		this.step = step;
		this.queenPosition = queenPosition;
	}

	private static boolean check(int[] arr, int step) {
		for (int i = 0; i <= step; i++) {
			for (int j = i + 1; j <= step; j++) {
				if (arr[i] == arr[j] || arr[i] + i == arr[j] + j || arr[i] + j == arr[j] + i)
					return false;
			}
		}
		return true;
	}

	@Override
	public void run() {
		if (Main.N == step) {
			Main.printQueens(queenPosition);
			tpe.shutdown();
		}
		for (int i = 0; i < Main.N; ++i) {
			int[] newGraph = queenPosition.clone();
			newGraph[step] = i;

			if (check(newGraph, step)) {
				tpe.submit(new QueenExecutor(tpe,step + 1, newGraph));
			}
		}
	}
}
