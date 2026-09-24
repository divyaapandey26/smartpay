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

    @Autowired
    private AnomalyDetectorService anomalyDetector;

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
        if (lowest == upiFee) recommended = "UPI";
        else if (lowest == cashFee) recommended = "Cash";
        else recommended = "Split";

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
                reasons.add("Extra charge exceeds Rs 10 threshold - significant overcharge");
            }

            if (amount > 2000) {
                reasons.add("Transaction amount is above the Rs 2000 MDR threshold");
            }

            long previousSameMethod = alertRepository.findAll().stream()
                    .filter(a -> a.getMethod().equalsIgnoreCase(method))
                    .count();
            if (previousSameMethod >= 2) {
                reasons.add("This payment method has triggered " + previousSameMethod + " previous alerts - recurring pattern");
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
            Map<String, Object> anomaly = anomalyDetector.analyze(extra, method);

            Alert alert = new Alert(method, amount, expectedFee, feeCharged, extra,
                    "OVERCHARGE DETECTED: You were charged " + extra + " extra");
            alert.setReasons(String.join("|", reasons));
            alert.setAnomalyScore(((Number) anomaly.get("score")).intValue());
            alert.setSeverity((String) anomaly.get("severity"));
            alert.setDeviation((String) anomaly.get("deviation"));
            alertRepository.save(alert);

            result.put("anomalyScore", anomaly.get("score"));
            result.put("severity", anomaly.get("severity"));
            result.put("deviation", anomaly.get("deviation"));
        }

        return result;
    }
}