package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Pedestrians;
import com.apd.tema2.entities.ReaderHandler;
import com.apd.tema2.intersections.*;

import javax.sql.rowset.CachedRowSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Returneaza sub forma unor clase anonime implementari pentru metoda de citire din fisier.
 */
public class ReaderHandlerFactory {

    public static ReaderHandler getHandler(String handlerType) {
        // simple semaphore intersection
        // max random N cars roundabout (s time to exit each of them)
        // roundabout with exactly one car from each lane simultaneously
        // roundabout with exactly X cars from each lane simultaneously
        // roundabout with at most X cars from each lane simultaneously
        // entering a road without any priority
        // crosswalk activated on at least a number of people (s time to finish all of them)
        // road in maintenance - 1 lane 2 ways, X cars at a time
        // road in maintenance - N lanes 2 ways, X cars at a time
        // railroad blockage for T seconds for all the cars
        // unmarked intersection
        // cars racing
        return switch (handlerType) {
            case "simple_semaphore" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) {
                    // Exemplu de utilizare:
                    Main.intersection = IntersectionFactory.getIntersection("simple_semaphore");
                }
            };
            case "simple_n_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String [] line = br.readLine().split("\\s+");
                    var maxCars = Integer.parseInt(line[0]);
                    var waitingTime = Integer.parseInt(line[1]);
                    Main.intersection = IntersectionFactory.getIntersection("simple_n_round_about");
                    ((SimpleNRoundAbout) Main.intersection).setNoCars(Main.carsNo);
                    ((SimpleNRoundAbout) Main.intersection).setMaxNumberOfCars(maxCars);
                    ((SimpleNRoundAbout) Main.intersection).setRoundAboutWaitingTime(waitingTime);
                    ((SimpleNRoundAbout) Main.intersection).semaphore = new Semaphore(maxCars);
                    ((SimpleNRoundAbout) Main.intersection).barrier = new CyclicBarrier(Main.carsNo);
                }
            };
            case "simple_strict_1_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String [] line = br.readLine().split("\\s+");
                    Main.intersection = IntersectionFactory.getIntersection("simple_strict_1_car_roundabout");
                    var numOfLanes = Integer.parseInt(line[0]);
                    var waitingTime = Integer.parseInt(line[1]);
                    ((SimpleStrictRoundAbout) Main.intersection).setNumberOfLanes(numOfLanes);
                    ((SimpleStrictRoundAbout) Main.intersection).setWaitingTime(waitingTime);
                    ((SimpleStrictRoundAbout) Main.intersection).semaphore = new Semaphore[numOfLanes];
                    Arrays.setAll(((SimpleStrictRoundAbout) Main.intersection).semaphore, i -> new Semaphore(1));
                    ((SimpleStrictRoundAbout) Main.intersection).barrier = new CyclicBarrier(numOfLanes);
                }
            };
            case "simple_strict_x_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String [] line = br.readLine().split("\\s+");
                    Main.intersection = IntersectionFactory.getIntersection("simple_strict_1_car_roundabout");
                    var numOfLanes = Integer.parseInt(line[0]);
                    var waitingTime = Integer.parseInt(line[1]);
                    var maxCars = Integer.parseInt(line[2]);
                    ((SimpleStrictRoundAbout) Main.intersection).setNumberOfLanes(numOfLanes);
                    ((SimpleStrictRoundAbout) Main.intersection).setWaitingTime(waitingTime);
                    ((SimpleStrictRoundAbout) Main.intersection).semaphore = new Semaphore[numOfLanes];
                    Arrays.setAll(((SimpleStrictRoundAbout) Main.intersection).semaphore, i -> new Semaphore(maxCars));
                    ((SimpleStrictRoundAbout) Main.intersection).barrier = new CyclicBarrier(Main.carsNo);
                    ((SimpleStrictRoundAbout) Main.intersection).barrierStrictXRoundAbout =
                            new CyclicBarrier(maxCars * numOfLanes);
                }
            };
            case "simple_max_x_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String [] line = br.readLine().split("\\s+");
                    Main.intersection = IntersectionFactory.getIntersection("simple_strict_1_car_roundabout");
                    var numOfLanes = Integer.parseInt(line[0]);
                    var waitingTime = Integer.parseInt(line[1]);
                    var maxCars = Integer.parseInt(line[2]);
                    ((SimpleStrictRoundAbout) Main.intersection).setNumberOfLanes(numOfLanes);
                    ((SimpleStrictRoundAbout) Main.intersection).setWaitingTime(waitingTime);
                    ((SimpleStrictRoundAbout) Main.intersection).semaphore = new Semaphore[numOfLanes];
                    Arrays.setAll(((SimpleStrictRoundAbout) Main.intersection).semaphore, i -> new Semaphore(maxCars));
                }
            };
            case "priority_intersection" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String [] line = br.readLine().split("\\s+");
                    Main.intersection = IntersectionFactory.getIntersection("priority_intersection");
                    var noCarsWithHighPriority = Integer.parseInt(line[0]);
                    var noCarsWithLowPriority = Integer.parseInt(line[1]);
                    ((PriorityIntersection) Main.intersection).setNoCarsHighPriority(noCarsWithHighPriority);
                    ((PriorityIntersection) Main.intersection).setNoCarsLowPriority(noCarsWithLowPriority);
                    ((PriorityIntersection) Main.intersection).canPass = new AtomicBoolean(true);
                    ((PriorityIntersection) Main.intersection).carsWithHighPriority = new ArrayBlockingQueue<>(noCarsWithHighPriority);
                    ((PriorityIntersection) Main.intersection).carsWithLowPriority = new ArrayBlockingQueue<>(noCarsWithLowPriority);
                }
            };
            case "crosswalk" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String [] line = br.readLine().split("\\s+");
                    Main.pedestrians = new Pedestrians(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
                    Main.intersection = IntersectionFactory.getIntersection("crosswalk");
                    ((Crosswalk) Main.intersection).carMessages = new ConcurrentHashMap<>();
                    for (int i = 0 ; i < Main.carsNo ; ++i) {
                        var prev = ((Crosswalk) Main.intersection).carMessages.
                                putIfAbsent(i, Crosswalk.lightColor.RED_LIGHT);
                    }
                }
            };
            case "simple_maintenance" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String [] line = br.readLine().split("\\s+");
                    Main.intersection = IntersectionFactory.getIntersection("simple_maintenance");
                    ((SimpleMaintenance) Main.intersection).semaphore = new Semaphore[2];
                    ((SimpleMaintenance) Main.intersection).semaphore[0] = new Semaphore(Integer.parseInt(line[0]));
                    ((SimpleMaintenance) Main.intersection).semaphore[1] = new Semaphore(0);
                    ((SimpleMaintenance) Main.intersection).barrier = new CyclicBarrier(Integer.parseInt(line[0]));
                }
            };
            case "complex_maintenance" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    
                }
            };
            case "railroad" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    Main.intersection = IntersectionFactory.getIntersection("railroad");
                }
            };
            default -> null;
        };
    }

}
