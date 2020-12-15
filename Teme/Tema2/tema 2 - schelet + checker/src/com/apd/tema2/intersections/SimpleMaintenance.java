package com.apd.tema2.intersections;

import com.apd.tema2.entities.Intersection;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class SimpleMaintenance implements Intersection {
	public CyclicBarrier barrier;
	public Semaphore[] semaphore;
	private int maxNumberOfCars;

	public SimpleMaintenance() {}

	public SimpleMaintenance(int maxNumberOfCars) {
		this.maxNumberOfCars = maxNumberOfCars;
	}

	public int getMaxNumberOfCars() {
		return maxNumberOfCars;
	}

	public void setMaxNumberOfCars(int maxNumberOfCars) {
		this.maxNumberOfCars = maxNumberOfCars;
	}
}
