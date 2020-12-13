package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Intersection;
import com.apd.tema2.intersections.SimpleIntersection;
import com.apd.tema2.intersections.SimpleNRoundAbout;
import com.apd.tema2.intersections.SimpleStrictRoundAbout;

import java.util.HashMap;
import java.util.Map;

/**
 * Prototype Factory: va puteti crea cate o instanta din fiecare tip de implementare de Intersection.
 */
public class IntersectionFactory {
    private static Map<String, Intersection> cache = new HashMap<>();

    static {
        cache.put("simple_semaphore", new SimpleIntersection(Main.carsNo));
        cache.put("simple_n_round_about", new SimpleNRoundAbout());
        cache.put("simple_strict_1_car_roundabout", new SimpleStrictRoundAbout());
    }

    public static Intersection getIntersection(String handlerType) {
        return cache.get(handlerType);
    }

}
