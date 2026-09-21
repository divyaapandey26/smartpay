package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/compare")
public class CompareController {

    @Autowired
    private PaymentIntelligenceService service;

    @GetMapping
    public Map<String, Object> compare(@RequestParam double amount) {
        return service.compare(amount);
    }

    @PostMapping("/check-overcharge")
    public Map<String, Object> checkOvercharge(@RequestBody OverchargeRequest request) {
        return service.checkOvercharge(
                request.getAmount(),
                request.getMethod(),
                request.getFeeCharged()
        );
    }
}