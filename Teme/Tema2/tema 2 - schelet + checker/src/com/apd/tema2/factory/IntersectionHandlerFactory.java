package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.*;
import com.apd.tema2.intersections.*;
import com.apd.tema2.utils.Constants;

import java.util.concurrent.BrokenBarrierException;

import static java.lang.Thread.sleep;

/**
 * Clasa Factory ce returneaza implementari ale InterfaceHandler sub forma unor
 * clase anonime.
 */
public class IntersectionHandlerFactory {

    public static IntersectionHandler getHandler(String handlerType) {
        // simple semaphore intersection
        // max random N cars roundabout (s time to exit each of them)
        // roundabout with exactly one car from each lane simultaneously
        // roundabout with exactly X cars from each lane simultaneously
        // roundabout with at most X cars from each lane simultaneously
        // entering a road without any priority
        // crosswalk activated on at least a number of people (s time to finish all of
        // them)
        // road in maintenance - 2 ways 1 lane each, X cars at a time
        // road in maintenance - 1 way, M out of N lanes are blocked, X cars at a time
        // railroad blockage for s seconds for all the cars
        // unmarked intersection
        // cars racing
        return switch (handlerType) {
            case "simple_semaphore" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Car ").append(car.getId()).append(" has reached the semaphore, now waiting...");
                        System.out.println(sb.toString());
                        SimpleIntersection.barrier.await();
                        sleep(car.getWaitingTime());
                        sb.setLength(0);
                        sb = new StringBuilder();
                        sb.append("Car ").append(car.getId()).append(" has waited enough, now driving...");
                        System.out.println(sb.toString());
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            case "simple_n_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    var waitingTime = ((SimpleNRoundAbout) Main.intersection).getRoundAboutWaitingTime();
                    StringBuilder sb = new StringBuilder();
                    try {
                        sb.append("Car ").append(car.getId()).append(" has reached the roundabout, now waiting...");
                        System.out.println(sb.toString());
                        SimpleNRoundAbout.barrier.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        SimpleNRoundAbout.semaphore.acquire();
                        sb.setLength(0);
                        sb = new StringBuilder();
                        sb.append("Car ").append(car.getId()).append(" has entered the roundabout");
                        System.out.println(sb.toString());
                        sleep(waitingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sb.setLength(0);
                    sb = new StringBuilder();
                    sb.append("Car ").append(car.getId()).append(" has exited the roundabout after ").
                            append(waitingTime).append(" seconds");
                    System.out.println(sb.toString());
                    SimpleNRoundAbout.semaphore.release();

                }
            };
            case "simple_strict_1_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    var waitingTime = ((SimpleStrictRoundAbout) Main.intersection).getWaitingTime();
                    var laneId = car.getStartDirection();
                    StringBuilder sb = new StringBuilder();
                    try {
                        SimpleStrictRoundAbout.semaphore[laneId].acquire();
                        sb.append("Car ").append(car.getId()).append(" has reached the roundabout");
                        System.out.println(sb.toString());

                        sb.setLength(0);
                        sb = new StringBuilder();
                        sb.append("Car ").append(car.getId()).append(" has entered the roundabout from lane ").append(laneId);
                        System.out.println(sb.toString());
                        try {
                            SimpleStrictRoundAbout.barrier.await();
                        } catch (BrokenBarrierException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        sleep(waitingTime);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    sb.setLength(0);
                    sb = new StringBuilder();
                    sb.append("Car ").append(car.getId()).append(" has exited the roundabout after ").
                            append(waitingTime).append(" seconds");
                    System.out.println(sb.toString());
                    SimpleStrictRoundAbout.semaphore[laneId].release();
                }
            };
            case "simple_strict_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    var waitingTime = ((SimpleStrictRoundAbout) Main.intersection).getWaitingTime();
                    var laneId = car.getStartDirection();
                    var carId = car.getId();

                    StringBuilder sb = new StringBuilder();
                    sb.append("Car ").append(carId).append(" has reached the roundabout, now waiting...");
                    System.out.println(sb.toString());
                    try {
                        SimpleStrictRoundAbout.barrier.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        SimpleStrictRoundAbout.semaphore[laneId].acquire();
                        sb.setLength(0);
                        sb = new StringBuilder();
                        sb.append("Car ").append(carId).append(" was selected to enter the roundabout from lane ").append(laneId);
                        System.out.println(sb.toString());
                        try {
                            SimpleStrictRoundAbout.barrierStrictXRoundAbout.await();
                        } catch (BrokenBarrierException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        sb.setLength(0);
                        sb = new StringBuilder();
                        sb.append("Car ").append(carId).append(" has entered the roundabout from lane ").append(laneId);
                        System.out.println(sb.toString());
                        sleep(waitingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sb.setLength(0);
                    sb = new StringBuilder();
                    sb.append("Car ").append(car.getId()).append(" has exited the roundabout after ").
                            append(waitingTime).append(" seconds");
                    System.out.println(sb.toString());
                    SimpleStrictRoundAbout.semaphore[laneId].release();
                }
            };
            case "simple_max_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Get your Intersection instance

                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // NU MODIFICATI
                    var waitingTime = ((SimpleStrictRoundAbout) Main.intersection).getWaitingTime();
                    var laneId = car.getStartDirection();
                    var carId = car.getId();

                    StringBuilder sb = new StringBuilder();
                    sb.append("Car ").append(carId).append(" has reached the roundabout from lane ").append(laneId);
                    System.out.println(sb.toString());

                    try {
                        SimpleStrictRoundAbout.semaphore[laneId].acquire();
                        sb.setLength(0);
                        sb = new StringBuilder();
                        sb.append("Car ").append(carId).append(" has entered the roundabout from lane ").append(laneId);
                        System.out.println(sb.toString());
                        sleep(waitingTime);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                    sb.setLength(0);
                    sb = new StringBuilder();
                    sb.append("Car ").append(car.getId()).append(" has exited the roundabout after ").
                            append(waitingTime).append(" seconds");
                    System.out.println(sb.toString());
                    SimpleStrictRoundAbout.semaphore[laneId].release();

                }
            };
            case "priority_intersection" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Get your Intersection instance

                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // NU MODIFICATI

                    var carId = car.getId();
                    StringBuilder sb = new StringBuilder();
                    // Continuati de aici
                    if (car.getPriority() > 1) {
                        try {
                            sb.append("Car ").append(carId).append(" with high priority has entered the intersection");
                            System.out.println(sb.toString());
                            PriorityIntersection.carsWithHighPriority.put(carId);
                            sleep(2000);
                            var removedCar = PriorityIntersection.carsWithHighPriority.take();
                            sb.setLength(0);
                            sb = new StringBuilder();
                            sb.append("Car ").append(carId).append(" with high priority has exited the intersection");
                            System.out.println(sb.toString());
                            if (!PriorityIntersection.carsWithHighPriority.isEmpty()) {
                                PriorityIntersection.canPass.set(false);
                            } else {
                                PriorityIntersection.canPass.set(true);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        sb.setLength(0);
                        sb = new StringBuilder();
                        sb.append("Car ").append(carId).append(" with low priority is trying to enter the intersection...");
                        System.out.println(sb.toString());
                        try {
                            PriorityIntersection.carsWithLowPriority.put(carId);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (!PriorityIntersection.carsWithHighPriority.isEmpty()) {
                            PriorityIntersection.canPass.set(false);
                        } else {
                            PriorityIntersection.canPass.set(true);
                        }

                        while (!PriorityIntersection.canPass.compareAndSet(true, true)) {}

                        try {
                            synchronized (PriorityIntersection.lock) {
                                var removedCar = PriorityIntersection.carsWithLowPriority.take();
                                sb.setLength(0);
                                sb = new StringBuilder();
                                sb.append("Car ").append(removedCar).append(" with low priority has entered the intersection");
                                System.out.println(sb.toString());
                           }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            case "crosswalk" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    var carId = car.getId();
                    while (!Main.pedestrians.isFinished()) {
                        if (!Main.pedestrians.isPass()) {
                            var carMessage = Crosswalk.carMessages.getOrDefault(carId, "");
                            if (carMessage.matches(".*\\bred\\b.*")) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Car ").append(carId).append(" has now green light");
                                Crosswalk.carMessages.replace(carId, sb.toString());
                                System.out.println(sb.toString());
                            }
                        } else {
                            var carMessage = Crosswalk.carMessages.getOrDefault(carId, "");
                            if (carMessage.matches(".*\\bgreen\\b.*")) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Car ").append(carId).append(" has now red light");
                                Crosswalk.carMessages.replace(carId, sb.toString());
                                System.out.println(sb.toString());
                            }
                        }
                    }

                    if (!Main.pedestrians.isPass()) {
                        var carMessage = Crosswalk.carMessages.getOrDefault(carId, "");
                        if (carMessage.matches(".*\\bred\\b.*")) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Car ").append(carId).append(" has now green light");
                            Crosswalk.carMessages.replace(carId, sb.toString());
                            System.out.println(sb.toString());
                        }
                    } else {
                        var carMessage = Crosswalk.carMessages.getOrDefault(carId, "");
                        if (carMessage.matches(".*\\bgreen\\b.*")) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Car ").append(carId).append(" has now red light");
                            Crosswalk.carMessages.replace(carId, sb.toString());
                            System.out.println(sb.toString());
                        }
                    }
                }
            };
            case "simple_maintenance" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    
                }
            };
            case "complex_maintenance" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    
                }
            };
            case "railroad" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    
                }
            };
            default -> null;
        };
    }
}
