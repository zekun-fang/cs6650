package com.wjfzk;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpWorker implements Callable<Void> {
    private static final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl;
    private final AtomicInteger successfulRequests;
    private final AtomicInteger failedRequests;
    private final int requestPerThread;
    private final BlockingQueue<SkierLiftEvent> eventQueue;
    private final AtomicInteger remainingRequests;
    private static final String LOG_FILE = "request_logs.csv";

    public HttpWorker(String baseUrl, AtomicInteger successfulRequests, AtomicInteger failedRequests,
                      int requestPerThread, BlockingQueue<SkierLiftEvent> eventQueue, AtomicInteger remainingRequests) {
        this.baseUrl = baseUrl;
        this.successfulRequests = successfulRequests;
        this.failedRequests = failedRequests;
        this.requestPerThread = requestPerThread;
        this.eventQueue = eventQueue;
        this.remainingRequests = remainingRequests;
    }

    @Override
    public Void call() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            for (int i = 0; i < requestPerThread && remainingRequests.getAndDecrement() > 0; i++) {
                SkierLiftEvent event = eventQueue.poll(1, TimeUnit.SECONDS);
                if (event == null) break;

                String eventUrl = String.format("%s/skiers/%d/seasons/%d/days/%d/skiers/%d",
                        baseUrl, event.getResortID(), event.getSeasonID(), event.getDayID(), event.getSkierID());

                Gson gson = new Gson();
                String json = gson.toJson(event);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(eventUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                int retries = 0;
                while (retries < 5) {
                    long startTime = System.currentTimeMillis();
                    try {
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        long latency = System.currentTimeMillis() - startTime;
                        double throughput = 1000.0 / latency;

                        logRequestData(writer, startTime, latency, response.statusCode(), throughput);

                        if (response.statusCode() == 201) {
                            successfulRequests.incrementAndGet();
                            break;
                        } else {
                            retries++;
                        }
                    } catch (Exception e) {
                        retries++;
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                }
                if (retries >= 5) {
                    failedRequests.incrementAndGet();
                }
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    private synchronized void logRequestData(PrintWriter writer, long startTime, long latency, int statusCode, double throughput) {
        writer.printf("%d,POST,%d,%d,%.2f%n", startTime, latency, statusCode, throughput);
        writer.flush();
    }
}
