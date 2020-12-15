package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Intersection;
import com.apd.tema2.intersections.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Prototype Factory: va puteti crea cate o instanta din fiecare tip de implementare de Intersection.
 */
public class IntersectionFactory {
    private static final Map<String, Intersection> cache = new HashMap<>();

    static {
        cache.put("simple_semaphore", new SimpleIntersection(Main.carsNo));
        cache.put("simple_n_round_about", new SimpleNRoundAbout());
        cache.put("simple_strict_1_car_roundabout", new SimpleStrictRoundAbout());
        cache.put("priority_intersection", new PriorityIntersection());
        cache.put("crosswalk", new Crosswalk(Main.carsNo));
        cache.put("simple_maintenance", new SimpleMaintenance());
        cache.put("railroad", new Railroad(Main.carsNo));
    }

    public static Intersection getIntersection(String handlerType) {
        return cache.get(handlerType);
    }

}
