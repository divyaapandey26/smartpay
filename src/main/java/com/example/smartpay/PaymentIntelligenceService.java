package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentIntelligenceService {

    @Autowired
    private AlertRepository alertRepository;

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

        // Generate reasons for the anomaly
        List<String> reasons = new ArrayList<>();
        if (overcharged) {
            if (expectedFee == 0 && feeCharged > 0) {
                reasons.add("Fee charged on a transaction that should have been free");
            } else {
                double percentOver = (extra / expectedFee) * 100;
                if (percentOver > 50) {
                    reasons.add("Actual fee is " + Math.round(percentOver) + "% higher than the expected fee");
                } else {
                    reasons.add("Fee exceeds the RBI-mandated MDR limit");
                }
            }

            if (extra > 10) {
                reasons.add("Extra charge exceeds ₹10 threshold — significant overcharge");
            }

            if (amount > 2000) {
                reasons.add("Transaction amount is above the ₹2000 MDR threshold");
            }

            long previousSameMethod = alertRepository.findAll().stream()
                    .filter(a -> a.getMethod().equalsIgnoreCase(method))
                    .count();
            if (previousSameMethod >= 2) {
                reasons.add("This payment method has triggered " + previousSameMethod + " previous alerts — recurring pattern");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("method", method);
        result.put("amount", amount);
        result.put("expectedFee", expectedFee);
        result.put("feeCharged", feeCharged);
        result.put("extraCharged", extra);
        result.put("overcharged", overcharged);
        result.put("reasons", reasons);
        result.put("message", overcharged
                ? "OVERCHARGE DETECTED: You were charged " + extra + " extra"
                : "Fee is correct.");

        if (overcharged) {
            Alert alert = new Alert(method, amount, expectedFee, feeCharged, extra,
                    "OVERCHARGE DETECTED: You were charged " + extra + " extra");
            alert.setReasons(String.join("|", reasons));
            alertRepository.save(alert);
        }

        return result;
    }
}