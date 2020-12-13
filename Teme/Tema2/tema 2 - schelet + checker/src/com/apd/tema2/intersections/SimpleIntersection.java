package com.apd.tema2.intersections;

import com.apd.tema2.entities.Intersection;

import java.util.concurrent.CyclicBarrier;

public class SimpleIntersection implements Intersection {
	public static CyclicBarrier barrier;
	private int noCars;

	public SimpleIntersection() {}

	public SimpleIntersection(int noCars) {
		this.noCars = noCars;
		barrier = new CyclicBarrier(noCars);
	}

	public int getNoCars() {
		return noCars;
	}

	public void setNoCars(int noCars) {
		this.noCars = noCars;
	}
}
