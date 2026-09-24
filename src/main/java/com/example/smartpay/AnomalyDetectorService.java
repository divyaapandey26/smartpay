package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnomalyDetectorService {

    @Autowired
    private AlertRepository alertRepository;

    public Map<String, Object> analyze(double currentExtra, String method) {
        List<Alert> history = alertRepository.findAll();

        List<Double> historicalExtras = history.stream()
                .filter(a -> a.getMethod().equalsIgnoreCase(method))
                .map(Alert::getExtraCharged)
                .filter(v -> v > 0)
                .toList();

        Map<String, Object> result = new HashMap<>();

        if (historicalExtras.size() < 3) {
            double score = Math.min(100, currentExtra * 5);
            result.put("score", (int) Math.round(score));
            result.put("severity", score > 50 ? "HIGH" : score > 25 ? "MEDIUM" : "LOW");
            result.put("deviation", "N/A (insufficient history)");
            result.put("mean", "N/A");
            result.put("stdev", "N/A");
            return result;
        }

        double mean = historicalExtras.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        double variance = historicalExtras.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0);
        double stdev = Math.sqrt(variance);

        double zScore = stdev > 0 ? (currentExtra - mean) / stdev : 0;

        double anomalyScore = Math.min(100, Math.abs(zScore) * 25);

        if (currentExtra > 50) anomalyScore = Math.min(100, anomalyScore + 10);
        if (currentExtra > 100) anomalyScore = Math.min(100, anomalyScore + 15);

        String severity;
        if (anomalyScore >= 70) severity = "HIGH";
        else if (anomalyScore >= 40) severity = "MEDIUM";
        else severity = "LOW";

        result.put("score", (int) Math.round(anomalyScore));
        result.put("severity", severity);
        result.put("deviation", String.format("%.2f", zScore) + " sigma");
        result.put("mean", String.format("%.2f", mean));
        result.put("stdev", String.format("%.2f", stdev));

        return result;
    }
}