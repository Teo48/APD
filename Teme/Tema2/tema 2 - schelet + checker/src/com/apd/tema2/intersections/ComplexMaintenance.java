package com.apd.tema2.intersections;

import com.apd.tema2.entities.Car;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ComplexMaintenance {
	private int maxCars;
	private int noLanes;
	private int noNewLanes;
	public ConcurrentHashMap<Integer, ArrayBlockingQueue<Car>[]> newOldLanesMap;
	public ComplexMaintenance(int maxCars, int noLanes, int noNewLanes) {
		this.maxCars = maxCars;
		this.noLanes = noLanes;
		this.noNewLanes = noNewLanes;
	}
}
