package com.apd.tema2.intersections;

import com.apd.tema2.entities.Intersection;

import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class SimpleStrictRoundAbout implements Intersection {
	public Semaphore[] semaphore;
	public CyclicBarrier barrier;
	public CyclicBarrier barrierStrictXRoundAbout;
	private int numberOfLanes;
	private int waitingTime;
	private int maxPermits;
	private int noCars;

	public SimpleStrictRoundAbout() {}

	public int getMaxPermits() {
		return maxPermits;
	}

	public void setMaxPermits(int maxPermits) {
		this.maxPermits = maxPermits;
	}

	public SimpleStrictRoundAbout(int noCars, int numberOfLanes, int waitingTime, int maxPermits) {
		this.noCars = noCars;
		this.numberOfLanes = numberOfLanes;
		this.waitingTime = waitingTime;
		this.maxPermits = maxPermits;
		semaphore = new Semaphore[numberOfLanes];
		Arrays.setAll(semaphore, i -> new Semaphore(maxPermits));
		barrier = new CyclicBarrier(noCars);
		barrierStrictXRoundAbout = new CyclicBarrier( maxPermits * numberOfLanes);
	}

	public int getNumberOfLanes() {
		return numberOfLanes;
	}

	public void setNumberOfLanes(int numberOfLanes) {
		this.numberOfLanes = numberOfLanes;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}
}
