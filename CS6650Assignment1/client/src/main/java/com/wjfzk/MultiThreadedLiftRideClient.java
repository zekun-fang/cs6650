package com.wjfzk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadedLiftRideClient {
  private static final String SERVER_URL = "http://35.86.138.226:8080/server_war/";
  private static final int TOTAL_REQUESTS = 200000;
  private static final int INITIAL_THREADS = 200;
  private static final int REQUESTS_PER_THREAD = 1000;
  private static final AtomicInteger successfulCount = new AtomicInteger(0);
  private static final AtomicInteger failedCount = new AtomicInteger(0);
  private static final BlockingQueue<SkierLiftEvent> eventQueue = new LinkedBlockingQueue<>();
  private static final ExecutorService executor = Executors.newCachedThreadPool();

  public static void main(String[] args) throws InterruptedException {
    Thread eventProducerThread = new Thread(new EventGenerator(eventQueue, TOTAL_REQUESTS));
    eventProducerThread.start();

    long startTime = System.currentTimeMillis();
    AtomicInteger remainingRequests = new AtomicInteger(TOTAL_REQUESTS);
    List<Future<?>> futures = new CopyOnWriteArrayList<>();

    for (int i = 0; i < INITIAL_THREADS; i++) {
      futures.add(executor.submit(new HttpWorker(SERVER_URL, successfulCount, failedCount, REQUESTS_PER_THREAD, eventQueue, remainingRequests)));
    }

    while (remainingRequests.get() > 0) {
      futures.removeIf(Future::isDone);
      while (futures.size() < INITIAL_THREADS && remainingRequests.get() > 0) {
        futures.add(executor.submit(new HttpWorker(SERVER_URL, successfulCount, failedCount, REQUESTS_PER_THREAD, eventQueue, remainingRequests)));
      }
      Thread.sleep(50); // Prevent busy waiting
    }

    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.MINUTES);
    eventProducerThread.join();

    long endTime = System.currentTimeMillis();
    long responseTime = endTime - startTime;

    System.out.println("======= Client 1 Output =======");
    System.out.println("Number of Threads: " + INITIAL_THREADS);
    System.out.println("Successful requests: " + successfulCount.get());
    System.out.println("Failed requests: " + failedCount.get());
    System.out.println("Total response time: " + responseTime + " ms");
    System.out.println("Throughput: " + (TOTAL_REQUESTS / (responseTime / 1000.0)) + " requests per second");

    ClientLatencyAnalyzer.latencyComputation("request_logs.csv", TOTAL_REQUESTS);
  }
}

