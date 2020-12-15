package com.apd.tema2.intersections;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.Intersection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

public class ComplexMaintenance implements Intersection {
	private int maxCars;
	private int noLanes;
	private int noNewLanes;
	public CyclicBarrier barrier;
	public ConcurrentHashMap<Integer, LinkedBlockingQueue<LinkedBlockingQueue<Car>>> newOldLanesMap;
	public LinkedBlockingQueue<Car>[] oldLaneCars;
	public static final Object lock = new Object();
	public int [] newLanes;

	public ComplexMaintenance() {}

	public ComplexMaintenance(int maxCars, int noLanes, int noNewLanes) {
		this.maxCars = maxCars;
		this.noLanes = noLanes;
		this.noNewLanes = noNewLanes;
	}
	public void init() {
		barrier = new CyclicBarrier(Main.carsNo);
		newLanes = new int[noLanes];
		newOldLanesMap = new ConcurrentHashMap<>();
		oldLaneCars = new LinkedBlockingQueue[noLanes];

		for (int i = 0 ; i < noLanes ; ++i) {
			oldLaneCars[i] = new LinkedBlockingQueue<>();
		}
		for (int i = 0 ; i < noNewLanes ; ++i) {
			final int start = i * noLanes / noNewLanes;
			final int end = (i + 1) * noLanes / noNewLanes;

			for (int j = start ; j < end ; ++j) {
				newLanes[j] = i;
			}
			newOldLanesMap.putIfAbsent(i, new LinkedBlockingQueue<>());
		}
	}
	public int getMaxCars() {
		return maxCars;
	}

	public void setMaxCars(int maxCars) {
		this.maxCars = maxCars;
	}

	public int getNoLanes() {
		return noLanes;
	}

	public void setNoLanes(int noLanes) {
		this.noLanes = noLanes;
	}

	public int getNoNewLanes() {
		return noNewLanes;
	}

	public void setNoNewLanes(int noNewLanes) {
		this.noNewLanes = noNewLanes;
	}
}
