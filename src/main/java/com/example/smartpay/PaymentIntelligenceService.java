package com.example.smartpay;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentIntelligenceService {

    public Map<String, Object> compare(double amount) {
        double upiFee;
        if (amount <= 2000) {
            upiFee = 0;
        } else {
            upiFee = Math.min(amount * 0.004, 300);
        }

        double cashFee = 0;
        double splitFee = 0;

        double lowest = Math.min(upiFee, Math.min(cashFee, splitFee));
        String recommended;
        if (lowest == upiFee) {
            recommended = "UPI";
        } else if (lowest == cashFee) {
            recommended = "Cash";
        } else {
            recommended = "Split";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("amount", amount);
        result.put("upiFee", upiFee);
        result.put("cashFee", cashFee);
        result.put("splitFee", splitFee);
        result.put("recommended", recommended);
        result.put("savings", upiFee - lowest);

        return result;
    }

    public Map<String, Object> checkOvercharge(double amount, String method, double feeCharged) {
        double expectedFee;
        if (method.equalsIgnoreCase("UPI")) {
            if (amount <= 2000) expectedFee = 0;
            else expectedFee = Math.min(amount * 0.004, 300);
        } else {
            expectedFee = 0;
        }

        double extra = feeCharged - expectedFee;
        boolean overcharged = extra > 0.01;

        Map<String, Object> result = new HashMap<>();
        result.put("method", method);
        result.put("amount", amount);
        result.put("expectedFee", expectedFee);
        result.put("feeCharged", feeCharged);
        result.put("extraCharged", extra);
        result.put("overcharged", overcharged);
        result.put("message", overcharged
                ? "OVERCHARGE DETECTED: You were charged " + extra + " extra"
                : "Fee is correct.");

        return result;
    }
}