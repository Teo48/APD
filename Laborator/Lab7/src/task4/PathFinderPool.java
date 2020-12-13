package task4;

import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

public class PathFinderPool extends RecursiveTask<Void> {
	private ArrayList<Integer> current;
	private final int destination;

	public PathFinderPool(ArrayList<Integer> current, int destination) {
		this.current = current;
		this.destination = destination;
	}

	@Override
	protected Void compute() {
		ArrayList<PathFinderPool> tasks = new ArrayList<>();
		if (current.get(current.size() - 1) == destination) {
			System.out.println(current);
			return null;
		}

		int lastNodeInPath = current.get(current.size() - 1);
		for (int [] v : Main.graph) {
			if (v[0] == lastNodeInPath && !current.contains(v[1])) {
				ArrayList<Integer> newPath = (ArrayList<Integer>) current.clone();
				newPath.add(v[1]);
				var t = new PathFinderPool(newPath, destination);
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
