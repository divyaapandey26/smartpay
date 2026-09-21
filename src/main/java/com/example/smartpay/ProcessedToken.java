package com.example.smartpay;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_tokens")
public class ProcessedToken {

    @Id
    private String tokenId;

    private Long senderId;
    private double amount;
    private LocalDateTime processedAt;

    public ProcessedToken() {}

    public ProcessedToken(String tokenId, Long senderId, double amount) {
        this.tokenId = tokenId;
        this.senderId = senderId;
        this.amount = amount;
        this.processedAt = LocalDateTime.now();
    }

    public String getTokenId() { return tokenId; }
    public void setTokenId(String tokenId) { this.tokenId = tokenId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}