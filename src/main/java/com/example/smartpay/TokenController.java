package com.example.smartpay;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Autowired private TokenService tokenService;
    @Autowired private RateLimiterService rateLimiter;

    private String clientKey(HttpServletRequest request) {
        // Use IP address as the client identifier
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody Map<String, Object> request,
                                      HttpServletRequest httpRequest) throws Exception {
        String key = clientKey(httpRequest);

        if (!rateLimiter.isAllowed(key)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "RATE LIMIT EXCEEDED: Max 5 requests per minute. Try again in " +
                    rateLimiter.getSecondsUntilReset(key) + " seconds.");
            error.put("retryAfterSeconds", rateLimiter.getSecondsUntilReset(key));
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
        }

        Long senderId = Long.parseLong(request.get("senderId").toString());
        double amount = Double.parseDouble(request.get("amount").toString());
        return ResponseEntity.ok(tokenService.generateToken(senderId, amount));
    }

    @PostMapping("/settle")
    public ResponseEntity<?> settle(@RequestBody Map<String, Object> request,
                                    HttpServletRequest httpRequest) throws Exception {
        String key = clientKey(httpRequest);

        if (!rateLimiter.isAllowed(key)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "RATE LIMIT EXCEEDED: Max 5 requests per minute. Try again in " +
                    rateLimiter.getSecondsUntilReset(key) + " seconds.");
            error.put("retryAfterSeconds", rateLimiter.getSecondsUntilReset(key));
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
        }

        String token = request.get("token").toString();
        Long receiverId = Long.parseLong(request.get("receiverId").toString());
        return ResponseEntity.ok(tokenService.settleToken(token, receiverId));
    }
}