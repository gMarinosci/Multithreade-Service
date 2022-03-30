package dv512.gm222hj;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/*
 * File:	MultithreadedService.java
 * Course: 	20HT - Operating Systems - 1DV512
 * Author: 	Gabriele Marinosci - gm222hj
 * Date: 	January 2022
 */


// You can implement additional fields and methods in code below, but
// you are not allowed to rename or remove any of it!

// Additionally, please remember that you are not allowed to use any third-party libraries

public class MultithreadedService {

    ArrayList<Task> tasks = new ArrayList<>();
    ArrayList<Task> completedTasks = new ArrayList<>();
    ArrayList<Task> interruptedTasks = new ArrayList<>();
    ArrayList<Task> waitingTasks = new ArrayList<>();

    public class Task implements Runnable {

        int id;
        long burstTime;
        long sleepTime;
        long startTime;
        long finishTime;
        boolean isCompleted;
        boolean isInterrupted;



        public Task(int id, long burstTime, long sleepTime, long startTime) {
            this.burstTime = burstTime;
            this.id = id;
            this.sleepTime = sleepTime;
            this.startTime = startTime;
            this.isCompleted = false;
            this.isInterrupted = false;
        }

        public void run() {

            int totalSleep = 0;

            this.isInterrupted = true;

            while (totalSleep < burstTime) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    continue;
                }
                totalSleep += sleepTime;
                finishTime = System.currentTimeMillis();
            }

            this.isCompleted = true;
            this.isInterrupted = false;
        }
    }

    // Random number generator that must be used for the simulation
	Random rng;

    // ... add further fields, methods, and even classes, if necessary
    

	public MultithreadedService (long rngSeed) {
        this.rng = new Random(rngSeed);
    }


	public void reset() {this.tasks = new ArrayList<>();
      this.completedTasks = new ArrayList<>();
      this.interruptedTasks = new ArrayList<>();
      this.waitingTasks = new ArrayList<>();
    }

    public void runNewSimulation(final long totalSimulationTimeMs,
        final int numThreads, final int numTasks,
        final long minBurstTimeMs, final long maxBurstTimeMs, final long sleepTimeMs) {
        reset();

        int taskCounter = 0;
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < totalSimulationTimeMs) {

            if (pool.getTaskCount() < numTasks - 1) {
                Task newTask = new Task(taskCounter, randomBurstTime(minBurstTimeMs, maxBurstTimeMs), sleepTimeMs, System.currentTimeMillis());
                tasks.add(newTask);
                pool.execute(newTask);
                taskCounter += 1;
            }
        }

        pool.shutdownNow();
        sortTasks();
    }

    public void sortTasks() {
      for (Task task: this.tasks) {
          if (task.isCompleted) {
              this.completedTasks.add(task);
          } else if (task.isInterrupted) {
              this.interruptedTasks.add(task);
          } else {
              this.waitingTasks.add(task);
          }
      }
    }

    public long randomBurstTime(long min, long max) {
        return this.rng.nextLong(max - min) + min;
    }

    public void printResults() {

        System.out.println("Completed tasks:");

        for (Task t: completedTasks) {
            System.out.println("Task ID: " + t.id + " Burst time: " + t.burstTime + " Start Time: " + t.startTime + " Finish time: " + t.finishTime);
        }
        
        System.out.println("\nInterrupted tasks:");

        for (Task t: interruptedTasks) {
            System.out.println("Task ID: " + t.id);
        }
        
        System.out.println("\nWaiting tasks:");

        for (Task t: waitingTasks) {
            System.out.println("Task ID: " + t.id);
        }
	}

    public static void main(String args[]) {

		final long rngSeed = 19990616;

        // Do not modify the code below — instead, complete the implementation
        // of other methods!
        MultithreadedService service = new MultithreadedService(rngSeed);
        
        final int numSimulations = 3;
        final long totalSimulationTimeMs = 15*1000L; // 15 seconds
        
        final int numThreads = 4;
        final int numTasks = 30;
        final long minBurstTimeMs = 1*1000L; // 1 second  
        final long maxBurstTimeMs = 10*1000L; // 10 seconds
        final long sleepTimeMs = 100L; // 100 ms

        for (int i = 0; i < numSimulations; i++) {
            System.out.println("Running simulation #" + i);

            service.runNewSimulation(totalSimulationTimeMs,
                numThreads, numTasks,
                minBurstTimeMs, maxBurstTimeMs, sleepTimeMs);

            System.out.println("Simulation results:"
					+ "\n" + "----------------------");	
            service.printResults();

            System.out.println("\n");
        }

        System.out.println("----------------------");
        System.out.println("Exiting...");
        
        // If your program has not completed after the message printed above,
        // it means that some threads are not properly stopped! -> this issue will affect the grade
    }
}
