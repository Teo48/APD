package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.*;
import com.apd.tema2.intersections.*;
import com.apd.tema2.utils.Constants;

import java.security.cert.CRLSelector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.LinkedBlockingQueue;

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
                        var simpleSemaphore = (SimpleIntersection) IntersectionFactory.getIntersection("simple_semaphore");;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Car ").append(car.getId()).append(" has reached the semaphore, now waiting...");
                        System.out.println(sb.toString());
                        simpleSemaphore .barrier.await();
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
                    var simpleNRoundabout = (SimpleNRoundAbout) IntersectionFactory.
                            getIntersection("simple_n_round_about");
                    var waitingTime = ((SimpleNRoundAbout) Main.intersection).getRoundAboutWaitingTime();
                    StringBuilder sb = new StringBuilder();
                    try {
                        sb.append("Car ").append(car.getId()).append(" has reached the roundabout, now waiting...");
                        System.out.println(sb.toString());
                        simpleNRoundabout.barrier.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        simpleNRoundabout.semaphore.acquire();
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
                    simpleNRoundabout.semaphore.release();

                }
            };
            case "simple_strict_1_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    var waitingTime = ((SimpleStrictRoundAbout) Main.intersection).getWaitingTime();
                    var laneId = car.getStartDirection();
                    var simpleStrict1CarRoundabout = (SimpleStrictRoundAbout)IntersectionFactory.
                            getIntersection("simple_strict_1_car_roundabout");
                    StringBuilder sb = new StringBuilder();
                    try {
                        simpleStrict1CarRoundabout.semaphore[laneId].acquire();
                        sb.append("Car ").append(car.getId()).append(" has reached the roundabout");
                        System.out.println(sb.toString());

                        sb.setLength(0);
                        sb = new StringBuilder();
                        sb.append("Car ").append(car.getId()).append(" has entered the roundabout from lane ").append(laneId);
                        System.out.println(sb.toString());
                        try {
                            simpleStrict1CarRoundabout.barrier.await();
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
                    simpleStrict1CarRoundabout.semaphore[laneId].release();
                }
            };
            case "simple_strict_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    var waitingTime = ((SimpleStrictRoundAbout) Main.intersection).getWaitingTime();
                    var laneId = car.getStartDirection();
                    var carId = car.getId();
                    var simpleStrictXCarRoundabout = (SimpleStrictRoundAbout)IntersectionFactory.
                            getIntersection("simple_strict_1_car_roundabout");
                    StringBuilder sb = new StringBuilder();
                    sb.append("Car ").append(carId).append(" has reached the roundabout, now waiting...");
                    System.out.println(sb.toString());
                    try {
                        simpleStrictXCarRoundabout.barrier.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        simpleStrictXCarRoundabout.semaphore[laneId].acquire();
                        sb.setLength(0);
                        sb = new StringBuilder();
                        sb.append("Car ").append(carId).append(" was selected to enter the roundabout from lane ").append(laneId);
                        System.out.println(sb.toString());
                        try {
                            simpleStrictXCarRoundabout.barrierStrictXRoundAbout.await();
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
                    simpleStrictXCarRoundabout.semaphore[laneId].release();
                }
            };
            case "simple_max_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    var waitingTime = ((SimpleStrictRoundAbout) Main.intersection).getWaitingTime();
                    var laneId = car.getStartDirection();
                    var carId = car.getId();
                    var simpleMaxXCarRoundabout = (SimpleStrictRoundAbout)IntersectionFactory.
                            getIntersection("simple_strict_1_car_roundabout");
                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("Car ").append(carId).append(" has reached the roundabout from lane ").append(laneId);
                    System.out.println(sb.toString());

                    try {
                        simpleMaxXCarRoundabout.semaphore[laneId].acquire();
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
                    simpleMaxXCarRoundabout.semaphore[laneId].release();
                }
            };
            case "priority_intersection" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    var priorityIntersection = (PriorityIntersection) IntersectionFactory.getIntersection("priority_intersection");
                    var carId = car.getId();
                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    StringBuilder sb = new StringBuilder();

                    if (car.getPriority() > 1) {
                        try {
                            sb.append("Car ").append(carId).append(" with high priority has entered the intersection");
                            System.out.println(sb.toString());
                            priorityIntersection.carsWithHighPriority.put(carId);
                            sleep(2000);
                            var removedCar = priorityIntersection.carsWithHighPriority.take();
                            sb.setLength(0);
                            sb = new StringBuilder();
                            sb.append("Car ").append(removedCar).append(" with high priority has exited the intersection");
                            System.out.println(sb.toString());
                            priorityIntersection.canPass.set(priorityIntersection.carsWithHighPriority.isEmpty());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        sb.setLength(0);
                        sb = new StringBuilder();
                        sb.append("Car ").append(carId).append(" with low priority is trying to enter the intersection...");
                        System.out.println(sb.toString());
                        try {
                            priorityIntersection.carsWithLowPriority.put(carId);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        priorityIntersection.canPass.set(priorityIntersection.carsWithHighPriority.isEmpty());

                        while (!priorityIntersection.canPass.compareAndSet(true, true)) {}

                        try {
                            synchronized (priorityIntersection.lock) {
                                var removedCar = priorityIntersection.carsWithLowPriority.take();
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
                    var crosswalk = (Crosswalk) IntersectionFactory.getIntersection("crosswalk");
                    var carId = car.getId();
                    while (!Main.pedestrians.isFinished()) {
                        if (!Main.pedestrians.isPass()) {
                            var carMessage = crosswalk.carMessages.getOrDefault(carId, Crosswalk.lightColor.NO_LIGHT);
                            if (carMessage == Crosswalk.lightColor.RED_LIGHT) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Car ").append(carId).append(" has now green light");
                                crosswalk.carMessages.replace(carId, Crosswalk.lightColor.GREEN_LIGHT);
                                System.out.println(sb.toString());
                            }
                        } else {
                            var carMessage = crosswalk.carMessages.getOrDefault(carId, Crosswalk.lightColor.NO_LIGHT);
                            if (carMessage == Crosswalk.lightColor.GREEN_LIGHT) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Car ").append(carId).append(" has now red light");
                                crosswalk.carMessages.replace(carId, Crosswalk.lightColor.RED_LIGHT);
                                System.out.println(sb.toString());
                            }
                        }
                    }

                    if (!Main.pedestrians.isPass()) {
                        var carMessage = crosswalk.carMessages.getOrDefault(carId, Crosswalk.lightColor.NO_LIGHT);
                        if (carMessage == Crosswalk.lightColor.RED_LIGHT) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Car ").append(carId).append(" has now green light");
                            crosswalk.carMessages.replace(carId, Crosswalk.lightColor.GREEN_LIGHT);
                            System.out.println(sb.toString());
                        }
                    } else {
                        var carMessage = crosswalk.carMessages.getOrDefault(carId, Crosswalk.lightColor.NO_LIGHT);
                        if (carMessage == Crosswalk.lightColor.GREEN_LIGHT) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Car ").append(carId).append(" has now red light");
                            crosswalk.carMessages.replace(carId, Crosswalk.lightColor.RED_LIGHT);
                            System.out.println(sb.toString());
                        }
                    }
                }
            };
            case "simple_maintenance" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    var simpleMaintenance = (SimpleMaintenance) IntersectionFactory.getIntersection("simple_maintenance");
                    var carId = car.getId();
                    var carLane = car.getStartDirection();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Car ").append(carId).append(" from side number ").append(carLane).append(" has reached the bottleneck");
                    System.out.println(sb.toString());
                    try {
                        simpleMaintenance.semaphore[carLane].acquire();
                        sb.setLength(0);
                        sb.append("Car ").append(carId).append(" from side number ").append(carLane).append(" has passed the bottleneck");
                        System.out.println(sb.toString());
                        try {
                           simpleMaintenance.barrier.await();
                        } catch (BrokenBarrierException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    simpleMaintenance.semaphore[carLane ^ 1].release();
                }
            };
            case "complex_maintenance" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    var complexM = (ComplexMaintenance) IntersectionFactory.getIntersection("complex_maintenance");
                    var carId = car.getId();
                    var carLane = car.getStartDirection();
                    synchronized (ComplexMaintenance.lock) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Car ").append(carId).append(" has come from the lane number ").append(carLane);
                        System.out.println(sb.toString());
                        try {
                            complexM.oldLaneCars[carLane].put(car);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        complexM.barrier.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (carId == 0) {
                        for (int i = 0 ; i < complexM.getNoLanes() ; ++i) {
                            var aux =
                                    complexM.newOldLanesMap.get(complexM.newLanes[i]);
                            try {
                                aux.put(complexM.oldLaneCars[i]);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            complexM.newOldLanesMap.put(complexM.newLanes[i], aux);
                        }
                    }

                    try {
                        complexM.barrier.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    synchronized (ComplexMaintenance.lock) {
                        var currentLane = complexM.newOldLanesMap.get(complexM.newLanes[carLane]);
                        while (!currentLane.isEmpty()) {
                            LinkedBlockingQueue<Car> aux = null;
                            try {
                                aux = currentLane.take();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            int cnt;

                            if (aux.size() < complexM.getMaxCars()) {
                                cnt = aux.size();
                            } else {
                                cnt = complexM.getMaxCars();
                            }

                            int laneID = 0;
                            while (cnt-- > 0) {
                                Car temp = null;
                                try {
                                    temp = aux.take();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                StringBuilder sb = new StringBuilder();
                                laneID = temp.getStartDirection();
                                sb.append("Car ").append(temp.getId()).append(" from the lane ").append(temp.getStartDirection()).
                                        append(" has entered lane number ").append(complexM.newLanes[temp.getStartDirection()]);
                                System.out.println(sb.toString());
                            }

                            if (aux.size() > 0) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("The initial lane ").append(laneID).
                                        append(" has no permits and is moved to the back of the new lane queue");
                                try {
                                    currentLane.put(aux);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(sb.toString());
                            } else {
                                StringBuilder sb = new StringBuilder();
                                sb.append("The initial lane ").append(laneID).
                                        append(" has been emptied and removed from the new lane queue");
                                System.out.println(sb.toString());
                            }
                        }
                    }
                }
            };

            case "railroad" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    var railroad = (Railroad) IntersectionFactory.getIntersection("railroad");
                    var carId = car.getId();
                    var carLane = car.getStartDirection();

                    StringBuilder sb = new StringBuilder();
                    try {
                        synchronized (Railroad.lock) {
                            sb.append("Car ").append(carId).append(" from side number ").append(carLane).
                                    append(" has stopped by the railroad");
                            System.out.println(sb.toString());
                            railroad.cars.put(car);
                       }
                        try {
                            railroad.barrier.await();
                        } catch (BrokenBarrierException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (carId == 0) {
                        System.out.println("The train has passed, cars can now proceed");
                    }

                    try {
                        railroad.barrier.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    synchronized (Railroad.lock) {
                        sb.setLength(0);
                        sb = new StringBuilder();
                        try {
                            var removedCar = railroad.cars.take();
                            sb.append("Car ").append(removedCar.getId()).append(" from side number ").
                                    append(removedCar.getStartDirection()).append(" has started driving");
                            System.out.println(sb.toString());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            default -> null;
        };
    }
}
