package com.apd.tema2.intersections;

import com.apd.tema2.entities.Intersection;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/***
 * @author teo
 * */

public class SimpleNRoundAbout implements Intersection {
	public Semaphore semaphore;
	public CyclicBarrier barrier;
	private int roundAboutWaitingTime;
	private int maxNumberOfCars;
	private int noCars;

	public SimpleNRoundAbout() {}

	public SimpleNRoundAbout(int roundAboutWaitingTime, int maxNumberOfCars, int noCars) {
		semaphore = new Semaphore(maxNumberOfCars);
		barrier = new CyclicBarrier(noCars);
		this.roundAboutWaitingTime = roundAboutWaitingTime;
		this.maxNumberOfCars = maxNumberOfCars;
		this.noCars = noCars;
	}

	public int getMaxNumberOfCars() {
		return maxNumberOfCars;
	}

	public void setMaxNumberOfCars(int maxNumberOfCars) {
		this.maxNumberOfCars = maxNumberOfCars;
	}

	public int getNoCars() {
		return noCars;
	}

	public void setNoCars(int noCars) {
		this.noCars = noCars;
	}

	public int getRoundAboutWaitingTime() {
		return roundAboutWaitingTime;
	}

	public void setRoundAboutWaitingTime(int roundAboutWaitingTime) {
		this.roundAboutWaitingTime = roundAboutWaitingTime;
	}
}
