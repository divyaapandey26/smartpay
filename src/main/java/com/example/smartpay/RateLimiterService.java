package com.example.smartpay;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private static final int MAX_REQUESTS = 5;              // requests
    private static final long WINDOW_MS = 60 * 1000;        // per 60 seconds

    private final Map<String, RequestInfo> requests = new ConcurrentHashMap<>();

    private static class RequestInfo {
        long windowStart;
        int count;

        RequestInfo(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }

    /**
     * Returns true if the request is allowed, false if rate-limited.
     */
    public boolean isAllowed(String clientKey) {
        long now = System.currentTimeMillis();

        RequestInfo info = requests.compute(clientKey, (key, existing) -> {
            if (existing == null || (now - existing.windowStart) > WINDOW_MS) {
                // New window
                return new RequestInfo(now, 1);
            }
            existing.count++;
            return existing;
        });

        return info.count <= MAX_REQUESTS;
    }

    /**
     * Returns how many seconds until the current window resets for a client.
     */
    public long getSecondsUntilReset(String clientKey) {
        RequestInfo info = requests.get(clientKey);
        if (info == null) return 0;
        long elapsed = System.currentTimeMillis() - info.windowStart;
        long remaining = WINDOW_MS - elapsed;
        return Math.max(0, remaining / 1000);
    }
}