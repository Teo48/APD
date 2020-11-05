package bug2;
/**
 * @author cristian.chilipirea
 * 
 *         Why does this code not block? We took the same lock twice!
 *         Exceptand faptul ca e un lock reentrat, se porneste un singur thread,
 *         deci ar fi imposibil sa apara un deadlock.
 */
public class MyThread implements Runnable {
	static int i;

	@Override
	public void run() {
		synchronized (this) {
			synchronized (this) {
				i++;
			}
		}
	}
}
