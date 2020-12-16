package com.apd.tema2.intersections;

import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.Intersection;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CyclicBarrier;

/***
 * @author teo
 * cars - Lista masinilor ce ajung la bariera inainte de trecerea trenului
 * */

public class Railroad implements Intersection {
	public ArrayBlockingQueue<Car> cars;
	public CyclicBarrier barrier;
	public static final Object lock = new Object();
	private int noCars;

	public Railroad(int noCars) {
		this.noCars = noCars;
		cars = new ArrayBlockingQueue<>(noCars);
		barrier = new CyclicBarrier(noCars);
	}

	public int getNoCars() {
		return noCars;
	}

	public void setNoCars(int noCars) {
		this.noCars = noCars;
	}
}
