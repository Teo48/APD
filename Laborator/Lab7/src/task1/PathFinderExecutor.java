package task1;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class PathFinderExecutor implements Runnable {
	private ArrayList<Integer> current;
	private ExecutorService tpe;
	private final int destination;

	public PathFinderExecutor(ArrayList<Integer> current, ExecutorService tpe, int destination) {
		this.current = current;
		this.tpe = tpe;
		this.destination = destination;
	}

	@Override
	public void run() {
		if (current.get(current.size() - 1) == destination) {
			System.out.println(current);
			tpe.shutdown();
		}

		int lastNodeInPath = current.get(current.size() - 1);
		for (int [] v : Main.graph) {
			if (v[0] == lastNodeInPath && !current.contains(v[1])) {
				ArrayList<Integer> newPath = (ArrayList<Integer>) current.clone();
				newPath.add(v[1]);
				tpe.submit(new PathFinderExecutor(newPath, tpe, destination));
			}
		}
	}
}
