package com.wjfzk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientLatencyAnalyzer {
     private static final int INITIAL_THREADS = 200;
  public static void latencyComputation(String logFile, int totalRequests) {
    List<Long> latencies = new ArrayList<>();
    long totalLatency = 0;
    long minLatency = Long.MAX_VALUE;
    long maxLatency = Long.MIN_VALUE;

    try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            continue;
        }

        long latency = Long.parseLong(parts[2]);
        latencies.add(latency);

        minLatency = Math.min(minLatency, latency);
        maxLatency = Math.max(maxLatency, latency);
        totalLatency += latency;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (latencies.isEmpty()) {
      System.out.println("No latency data found!");
      return;
    }

    Collections.sort(latencies);
    double meanLatency = totalLatency / (double) totalRequests;
    long medianLatency = latencies.get(latencies.size() / 2);
    long p99Latency = latencies.get((int) (latencies.size() * 0.99));
    double throughput = totalRequests / (totalLatency / 1000.0);

    // Call the method to print stats
    printLatency(meanLatency, medianLatency, minLatency, maxLatency, p99Latency, throughput);
  }


  private static void printLatency(double meanLatency, long medianLatency, long minLatency,
                                        long maxLatency, long p99Latency, double throughput) {
    System.out.println();
    System.out.println("======= Client 2 Output ======= ");
    System.out.printf("Mean Response Time: %.2f ms%n", meanLatency);
    System.out.printf("Median Response Time: %d ms%n", medianLatency);
    System.out.printf("Min Response Time: %d ms%n", minLatency);
    System.out.printf("Max Response Time: %d ms%n", maxLatency);
    System.out.printf("Response Time at 99th Percentile: %d ms%n", p99Latency);
    System.out.printf("Throughput per individual thread: %.2f requests/sec%n", throughput);
    System.out.printf("Estimated overall throughput with %d threads: %.2f requests/second%n",
            INITIAL_THREADS, throughput * INITIAL_THREADS);
  }
}
