package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offline")
public class OfflineQueueController {

    @Autowired private OfflineQueueService queueService;

    @PostMapping("/enqueue")
    public OfflineTransaction enqueue(@RequestBody Map<String, Object> req) {
        Long senderId = Long.parseLong(req.get("senderId").toString());
        Long receiverId = Long.parseLong(req.get("receiverId").toString());
        double amount = Double.parseDouble(req.get("amount").toString());
        return queueService.enqueue(senderId, receiverId, amount);
    }

    @GetMapping("/queue")
    public List<OfflineTransaction> getAll() {
        return queueService.getAll();
    }

    @PostMapping("/sync")
    public Map<String, Object> sync() {
        return queueService.syncQueue();
    }

    @PostMapping("/clear")
    public Map<String, Object> clear() {
        queueService.clearQueue();
        return Map.of("status", "cleared");
    }
}