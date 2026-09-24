package com.example.smartpay;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CostOptimizerService {

    public Map<String, Object> optimize(double totalAmount, int transactionCount) {
        List<Map<String, Object>> strategies = new ArrayList<>();

        // Strategy A: Single batch
        // Fee: 0.5%, 1 API call, fast, low risk
        Map<String, Object> single = buildStrategy(
                "Single Batch",
                "Settle all transactions in one batch",
                totalAmount * 0.005,
                1,
                5,
                "LOW",
                transactionCount
        );
        strategies.add(single);

        // Strategy B: Medium batches (10 per batch)
        // Fee: 0.3%, more API calls, medium speed
        int mediumBatches = (int) Math.ceil(transactionCount / 10.0);
        Map<String, Object> medium = buildStrategy(
                "Medium Batches (10 per batch)",
                "Split into batches of 10 transactions",
                totalAmount * 0.003,
                mediumBatches,
                30,
                "MEDIUM",
                transactionCount
        );
        strategies.add(medium);

        // Strategy C: Small batches (5 per batch)
        // Fee: 0.2%, most API calls, slow, higher risk
        int smallBatches = (int) Math.ceil(transactionCount / 5.0);
        Map<String, Object> small = buildStrategy(
                "Fine Batches (5 per batch)",
                "Split into batches of 5 transactions — lowest fee",
                totalAmount * 0.002,
                smallBatches,
                60,
                "HIGH",
                transactionCount
        );
        strategies.add(small);

        // Find best strategy using weighted scoring
        Map<String, Object> best = null;
        double bestScore = -1;
        for (Map<String, Object> s : strategies) {
            double score = (double) s.get("compositeScore");
            if (score > bestScore) {
                bestScore = score;
                best = s;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalAmount", totalAmount);
        result.put("transactionCount", transactionCount);
        result.put("strategies", strategies);
        result.put("recommended", best.get("name"));
        result.put("recommendedFee", best.get("totalFee"));
        result.put("recommendedScore", bestScore);

        double worstFee = strategies.stream()
                .mapToDouble(s -> (double) s.get("totalFee"))
                .max()
                .orElse(0);
        result.put("savings", worstFee - (double) best.get("totalFee"));

        return result;
    }

    private Map<String, Object> buildStrategy(String name, String description,
                                              double totalFee, int apiCalls,
                                              int processingSeconds, String risk,
                                              int transactionCount) {
        Map<String, Object> s = new HashMap<>();
        s.put("name", name);
        s.put("description", description);
        s.put("totalFee", Math.round(totalFee * 100.0) / 100.0);
        s.put("feePerTransaction", Math.round((totalFee / Math.max(1, transactionCount)) * 100.0) / 100.0);
        s.put("apiCalls", apiCalls);
        s.put("processingSeconds", processingSeconds);
        s.put("risk", risk);

        // Calculate composite score (0-100)
        double feeScore = Math.max(0, 100 - (totalFee / 100.0) * 10);
        double speedScore = Math.max(0, 100 - processingSeconds * 1.5);
        double riskScore = risk.equals("LOW") ? 100 : risk.equals("MEDIUM") ? 60 : 30;

        double composite = (feeScore * 0.5) + (speedScore * 0.3) + (riskScore * 0.2);
        s.put("compositeScore", Math.round(composite * 10.0) / 10.0);
        s.put("feeScore", Math.round(feeScore));
        s.put("speedScore", Math.round(speedScore));
        s.put("riskScore", Math.round(riskScore));
        return s;
    }
}