package com.example.smartpay;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long appId;
    private String merchantName;
    private double cashbackPercent;
    private double maxCashback;
    private double minAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    public Offer() {}

    public Offer(Long appId, String merchantName, double cashbackPercent, double maxCashback, double minAmount, LocalDate startDate, LocalDate endDate, boolean active) {
        this.appId = appId;
        this.merchantName = merchantName;
        this.cashbackPercent = cashbackPercent;
        this.maxCashback = maxCashback;
        this.minAmount = minAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    public Long getId() { return id; }
    public Long getAppId() { return appId; }
    public String getMerchantName() { return merchantName; }
    public double getCashbackPercent() { return cashbackPercent; }
    public double getMaxCashback() { return maxCashback; }
    public double getMinAmount() { return minAmount; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isActive() { return active; }

    public boolean isCurrentlyValid() {
        LocalDate today = LocalDate.now();
        return active && !today.isBefore(startDate) && !today.isAfter(endDate);
    }
}