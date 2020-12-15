package com.apd.tema2.intersections;

import com.apd.tema2.entities.Intersection;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class PriorityIntersection implements Intersection {
	public ArrayBlockingQueue<Integer> carsWithLowPriority;
	public ArrayBlockingQueue<Integer> carsWithHighPriority;
	public final Object lock = new Object();
	public AtomicBoolean canPass;
	private int noCarsLowPriority;
	private int noCarsHighPriority;

	public PriorityIntersection() {}

	public PriorityIntersection(int noCarsLowPriority, int noCarsHighPriority) {
		this.noCarsLowPriority = noCarsLowPriority;
		this.noCarsHighPriority = noCarsHighPriority;
		carsWithLowPriority = new ArrayBlockingQueue<>(noCarsLowPriority);
		carsWithHighPriority = new ArrayBlockingQueue<>(noCarsHighPriority);
		canPass = new AtomicBoolean(true);
	}

	public int getNoCarsLowPriority() {
		return noCarsLowPriority;
	}

	public void setNoCarsLowPriority(int noCarsLowPriority) {
		this.noCarsLowPriority = noCarsLowPriority;
	}

	public int getNoCarsHighPriority() {
		return noCarsHighPriority;
	}

	public void setNoCarsHighPriority(int noCarsHighPriority) {
		this.noCarsHighPriority = noCarsHighPriority;
	}
}
