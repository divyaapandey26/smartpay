package com.example.smartpay;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String method;
    private double amount;
    private double expectedFee;
    private double feeCharged;
    private double extraCharged;
    private String message;

    @Column(length = 1000)
    private String reasons;

    private int anomalyScore;
    private String severity;
    private String deviation;

    private boolean iqrOutlier;

    @Column(length = 200)
    private String iqrNote;

    private LocalDateTime createdAt;

    public Alert() {}

    public Alert(String method, double amount, double expectedFee, double feeCharged, double extraCharged, String message) {
        this.method = method;
        this.amount = amount;
        this.expectedFee = expectedFee;
        this.feeCharged = feeCharged;
        this.extraCharged = extraCharged;
        this.message = message;
        this.reasons = "";
        this.anomalyScore = 0;
        this.severity = "LOW";
        this.deviation = "N/A";
        this.iqrOutlier = false;
        this.iqrNote = "";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getMethod() { return method; }
    public double getAmount() { return amount; }
    public double getExpectedFee() { return expectedFee; }
    public double getFeeCharged() { return feeCharged; }
    public double getExtraCharged() { return extraCharged; }
    public String getMessage() { return message; }
    public String getReasons() { return reasons; }
    public void setReasons(String reasons) { this.reasons = reasons; }
    public int getAnomalyScore() { return anomalyScore; }
    public void setAnomalyScore(int anomalyScore) { this.anomalyScore = anomalyScore; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getDeviation() { return deviation; }
    public void setDeviation(String deviation) { this.deviation = deviation; }
    public boolean isIqrOutlier() { return iqrOutlier; }
    public void setIqrOutlier(boolean iqrOutlier) { this.iqrOutlier = iqrOutlier; }
    public String getIqrNote() { return iqrNote; }
    public void setIqrNote(String iqrNote) { this.iqrNote = iqrNote; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}