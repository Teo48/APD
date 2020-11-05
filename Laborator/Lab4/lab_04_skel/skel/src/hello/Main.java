package hello;

public class Main {
	public static void main(String [] args) {
		int cores = Runtime.getRuntime().availableProcessors();
		for (int i = 0; i < cores; ++i) {
			final int id = i;
			new Thread( () -> {
				System.out.println("Hello from thread #" + id);
			}).start();
		}
	}
}
