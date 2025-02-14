package com.wjfzk;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class EventGenerator implements Runnable {
    private final BlockingQueue<SkierLiftEvent> eventQueue;
    private final int totalRequests;

    public EventGenerator(BlockingQueue<SkierLiftEvent> eventQueue, int totalRequests) {
        this.eventQueue = eventQueue;
        this.totalRequests = totalRequests;
    }

    @Override
    public void run() {
        for (int i = 0; i < totalRequests; i++) {
            try {
                eventQueue.put(generateSkierLiftEvent());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private SkierLiftEvent generateSkierLiftEvent() {
        SkierLiftEvent event = new SkierLiftEvent();
        event.setSkierID(ThreadLocalRandom.current().nextInt(1, 100001));
        event.setResortID(ThreadLocalRandom.current().nextInt(1, 11));
        event.setLiftID(ThreadLocalRandom.current().nextInt(1, 41));
        event.setTime(ThreadLocalRandom.current().nextInt(1, 361));
        event.setSeasonID(2025);
        event.setDayID(1);
        return event;
    }
}

