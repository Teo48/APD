package task6;

import task3.Main;
import task4.PathFinderPool;

import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

public class QueenPool extends RecursiveTask<Void> {
	private final int step;
	private final int [] queenPosition;

	public QueenPool(int step, int[] queenPosition) {
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
	protected Void compute() {
		ArrayList<QueenPool> tasks = new ArrayList<>();
		if (Main.N == step) {
			Main.printQueens(queenPosition);
			return null;
		}
		for (int i = 0; i < Main.N; ++i) {
			int[] newGraph = queenPosition.clone();
			newGraph[step] = i;

			if (check(newGraph, step)) {
				var t = new QueenPool(step + 1, newGraph);
				tasks.add(t);
				t.fork();
			}
		}

		for (var i : tasks) {
			i.join();
		}

		return null;
	}
}
