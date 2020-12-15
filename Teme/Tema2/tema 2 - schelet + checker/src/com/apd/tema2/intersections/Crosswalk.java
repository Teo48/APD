package com.apd.tema2.intersections;

import com.apd.tema2.entities.Intersection;

import java.util.concurrent.ConcurrentHashMap;

public class Crosswalk implements Intersection {
	public enum lightColor {RED_LIGHT, GREEN_LIGHT, NO_LIGHT};
	public ConcurrentHashMap<Integer, lightColor> carMessages;
	private int noCars;

	public Crosswalk(int noCars) {
		this.noCars = noCars;
		carMessages = new ConcurrentHashMap<>();
		for (int i = 0 ; i < noCars ; ++i) {
			var prev = carMessages.putIfAbsent(i, lightColor.RED_LIGHT);
		}
	}

	public void setNoCars(int noCars) {
		this.noCars = noCars;
	}

	public int getNoCars() {
		return noCars;
	}
}
