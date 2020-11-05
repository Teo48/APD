package bug3;
/**
 * @author cristian.chilipirea
 * 
 *         Why is value set correct even though we use different locks in
 *         different threads?
 *         Se face lock pe acelasi obiect, chiar daca avem verificari diferite in if,
 *         in momentul in care s-a facut lock fie de catre thread-ul 0, fie de catre 1,
 *         celalalt nu mai poate intra.
 */
public class MyThread implements Runnable {
	static String a = "LOCKa";
	static String b = "LOCKb";
	int id;
	static int value = 0;

	MyThread(int id) {
		this.id = id;
	}

	@Override
	public void run() {
		if (id == 0) {
			synchronized (a) {
				for (int i = 0; i < Main.N; i++)
					value = value + 3;
			}
		} else {
			synchronized (b) {
				for (int i = 0; i < Main.N; i++)
					value = value + 3;
			}
		}
	}
}
