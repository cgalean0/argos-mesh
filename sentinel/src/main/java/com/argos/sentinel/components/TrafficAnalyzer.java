package com.argos.sentinel.components;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Component;

@Component
public class TrafficAnalyzer {

    // IP -> List of timestamps of yours last peticions
    private final Map<String, Deque<Long>> windowMap = new ConcurrentHashMap<>();
    
    // Configuration: 50 peticions for 10 seconds
    private static final int LIMIT = 50;
    private static final long WINDOW_SIZE_MS = 10000; 

    public boolean shouldBlock(String ip) {
        long now = System.currentTimeMillis();
        
    
        Deque<Long> timestamps = windowMap.computeIfAbsent(ip, k -> new ConcurrentLinkedDeque<>());

        synchronized (timestamps) {
            timestamps.addLast(now);

            // Remove that exceed that's time limit
            while (!timestamps.isEmpty() && (now - timestamps.peekFirst() > WINDOW_SIZE_MS)) {
                timestamps.removeFirst();
            }

            return timestamps.size() > LIMIT;
        }
    }
}