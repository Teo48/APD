package com.apd.tema2.intersections;

import com.apd.tema2.entities.Intersection;

import java.util.concurrent.ConcurrentHashMap;

public class Crosswalk implements Intersection {
	public static ConcurrentHashMap<Integer, String> carMessages;
	private int noCars;

	public Crosswalk(int noCars) {
		this.noCars = noCars;
		carMessages = new ConcurrentHashMap<>();
		for (int i = 0 ; i < noCars ; ++i) {
			StringBuilder sb = new StringBuilder();
			sb.append("Car ").append(i).append(" has now green light");
			var prev = carMessages.putIfAbsent(i, sb.toString());
		}
	}

	public void setNoCars(int noCars) {
		this.noCars = noCars;
	}

	public int getNoCars() {
		return noCars;
	}
}
