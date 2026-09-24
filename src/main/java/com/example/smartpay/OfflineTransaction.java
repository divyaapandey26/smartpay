package com.example.smartpay;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "offline_transactions")
public class OfflineTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientTxId;      // UUID generated on client side
    private Long senderId;
    private Long receiverId;
    private double amount;

    @Enumerated(EnumType.STRING)
    private Status status;           // PENDING, SETTLED, DUPLICATE, FAILED

    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime syncedAt;

    public enum Status { PENDING, SETTLED, DUPLICATE, FAILED }

    public OfflineTransaction() {}

    public OfflineTransaction(String clientTxId, Long senderId, Long receiverId, double amount) {
        this.clientTxId = clientTxId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getClientTxId() { return clientTxId; }
    public Long getSenderId() { return senderId; }
    public Long getReceiverId() { return receiverId; }
    public double getAmount() { return amount; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getSyncedAt() { return syncedAt; }
    public void setSyncedAt(LocalDateTime syncedAt) { this.syncedAt = syncedAt; }
}