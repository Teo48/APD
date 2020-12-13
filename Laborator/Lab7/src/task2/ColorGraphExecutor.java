package task2;

import java.util.concurrent.ExecutorService;

public class ColorGraphExecutor implements Runnable{
	private ExecutorService tpe;
	private final int step;
	private final int [] colors;

	public ColorGraphExecutor(ExecutorService tpe, int step, int[] colors) {
		this.tpe = tpe;
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
	public void run() {
		if (step == Main.N) {
			Main.printColors(colors);
			tpe.shutdown();
		}

		for (int i = 0; i < Main.COLORS; ++i) {
			int[] newColors = colors.clone();
			newColors[step] = i;
			if (verifyColors(newColors, step)) {
				tpe.submit(new ColorGraphExecutor(tpe, step + 1, newColors));
			}
		}
	}
}
