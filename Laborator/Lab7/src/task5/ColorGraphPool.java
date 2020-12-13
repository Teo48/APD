package task5;

import task4.PathFinderPool;

import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

public class ColorGraphPool extends RecursiveTask<Void> {
	private final int step;
	private final int [] colors;

	public ColorGraphPool(int step, int[] colors) {
		this.step = step;
		this.colors = colors;
	}

	private static boolean verifyColors(int[] colors, int step) {
		for (int i = 0; i < step; i++) {
			if (colors[i] == colors[step] && isEdge(i, step))
				return false;
		}
		return true;
	}

	private static boolean isEdge(int a, int b) {
		for (int[] ints : Main.graph) {
			if (ints[0] == a && ints[1] == b)
				return true;
		}
		return false;
	}

	@Override
	protected Void compute() {
		ArrayList<ColorGraphPool> tasks = new ArrayList<>();
		if (step == Main.N) {
			Main.printColors(colors);
			return null;
		}

		for (int i = 0; i < Main.COLORS; ++i) {
			int[] newColors = colors.clone();
			newColors[step] = i;
			if (verifyColors(newColors, step)) {
				var t = new ColorGraphPool(step + 1, newColors);
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
