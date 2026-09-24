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
    public LocalDateTime getCreatedAt() { return createdAt; }
}