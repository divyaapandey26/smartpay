package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
            result.put("iqrOutlier", false);
            result.put("iqrNote", "N/A (insufficient history)");
            return result;
        }

        // Z-Score Analysis
        double mean = historicalExtras.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = historicalExtras.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0);
        double stdev = Math.sqrt(variance);
        double zScore = stdev > 0 ? (currentExtra - mean) / stdev : 0;

        // IQR Analysis
        List<Double> sorted = new ArrayList<>(historicalExtras);
        Collections.sort(sorted);
        double q1 = percentile(sorted, 25);
        double q3 = percentile(sorted, 75);
        double iqr = q3 - q1;
        double upperBound = q3 + 1.5 * iqr;
        boolean iqrOutlier = currentExtra > upperBound;

        // Combine z-score and IQR into a single 0-100 score
        double zComponent = Math.min(100, Math.abs(zScore) * 25);
        double iComponent = 0;
        if (iqrOutlier) iComponent = 100;
        else if (currentExtra > q3) iComponent = 50;
        else if (currentExtra > q1) iComponent = 25;

        double anomalyScore = (zComponent * 0.6) + (iComponent * 0.4);

        if (currentExtra > 50) anomalyScore = Math.min(100, anomalyScore + 10);
        if (currentExtra > 100) anomalyScore = Math.min(100, anomalyScore + 15);

        String severity;
        if (anomalyScore >= 70) severity = "HIGH";
        else if (anomalyScore >= 40) severity = "MEDIUM";
        else severity = "LOW";

        result.put("score", (int) Math.round(anomalyScore));
        result.put("severity", severity);
        result.put("deviation", String.format("%.2f", zScore) + " sigma");
        result.put("iqrOutlier", iqrOutlier);
        result.put("iqrNote", String.format("Q1=%.2f, Q3=%.2f, bound=%.2f", q1, q3, upperBound));
        return result;
    }

    private double percentile(List<Double> sorted, double p) {
        if (sorted.isEmpty()) return 0;
        if (sorted.size() == 1) return sorted.get(0);

        double rank = (p / 100.0) * (sorted.size() - 1);
        int lower = (int) Math.floor(rank);
        int upper = (int) Math.ceil(rank);

        if (lower == upper) return sorted.get(lower);

        double weight = rank - lower;
        return sorted.get(lower) * (1 - weight) + sorted.get(upper) * weight;
    }
}