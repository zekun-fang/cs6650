/**
 * projectName: CS6650Assignment1
 *
 * @author Zekun Fang
 * @version 1.0
 * description:
 * @date 2025/2/13
 */


import com.google.gson.Gson;
import com.wjfzk.SkierLiftEvent;
import com.wjfzk.SkierLiftGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;

public class SingleThreadLatencyTest {
    private static final String SERVER_URL = "http://localhost:8080/server_war_exploded";
    private static final int NUM_REQUESTS = 10000;

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        long totalLatency = 0;
        long minLatency = Long.MAX_VALUE;
        long maxLatency = Long.MIN_VALUE;

        for (int i = 0; i < NUM_REQUESTS; i++) {
            SkierLiftEvent event = SkierLiftGenerator.generateSkierLiftEvent();
            String json = gson.toJson(event);
            String eventUrl = String.format("%s/skiers/%d/seasons/%d/days/%d/skiers/%d",
                    SERVER_URL, event.getResortID(), event.getSeasonID(), event.getDayID(), event.getSkierID());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(eventUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            long startTime = System.currentTimeMillis();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                long latency = System.currentTimeMillis() - startTime;

                totalLatency += latency;
                minLatency = Math.min(minLatency, latency);
                maxLatency = Math.max(maxLatency, latency);

                if (i % 1000 == 0) {
                    System.out.println("Sent " + i + " requests...");
                }

            } catch (IOException | InterruptedException e) {
                System.err.println("Request failed: " + e.getMessage());
            }
        }

        double avgLatency = totalLatency / (double) NUM_REQUESTS;
        System.out.println("\n======= Single-Threaded Latency Test =======");
        System.out.printf("Total Requests: %d\n", NUM_REQUESTS);
        System.out.printf("Avg Response Time: %.2f ms\n", avgLatency);
        System.out.printf("Min Response Time: %d ms\n", minLatency);
        System.out.printf("Max Response Time: %d ms\n", maxLatency);
        System.out.printf("Estimated Single-Thread Throughput: %.2f requests/sec\n", 1000 / avgLatency);
    }
}

