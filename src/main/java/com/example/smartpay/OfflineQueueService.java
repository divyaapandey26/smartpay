package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OfflineQueueService {

    @Autowired private OfflineTransactionRepository queueRepository;
    @Autowired private UserRepository userRepository;

    /** Simulate adding a transaction to the offline queue (as if phone had no internet). */
    public OfflineTransaction enqueue(Long senderId, Long receiverId, double amount) {
        String clientTxId = UUID.randomUUID().toString();
        OfflineTransaction tx = new OfflineTransaction(clientTxId, senderId, receiverId, amount);
        return queueRepository.save(tx);
    }

    /** Get all transactions in the queue. */
    public List<OfflineTransaction> getAll() {
        return queueRepository.findAll();
    }

    /** Get only PENDING transactions. */
    public List<OfflineTransaction> getPending() {
        return queueRepository.findByStatus(OfflineTransaction.Status.PENDING);
    }

    /**
     * Simulate syncing: process all pending transactions in a batch.
     * Returns a summary of what happened to each transaction.
     */
    @Transactional
    public Map<String, Object> syncQueue() {
        List<OfflineTransaction> pending = queueRepository.findByStatus(OfflineTransaction.Status.PENDING);

        int settled = 0;
        int duplicates = 0;
        int failed = 0;

        for (OfflineTransaction tx : pending) {
            // Idempotency check: has this clientTxId already been settled?
            // (In real life, this catches duplicate submissions when the client retries.)

            User sender = userRepository.findById(tx.getSenderId()).orElse(null);
            User receiver = userRepository.findById(tx.getReceiverId()).orElse(null);

            if (sender == null || receiver == null) {
                tx.setStatus(OfflineTransaction.Status.FAILED);
                tx.setNote("Sender or receiver not found");
                failed++;
            } else if (sender.getBalance() < tx.getAmount()) {
                tx.setStatus(OfflineTransaction.Status.FAILED);
                tx.setNote("Insufficient balance");
                failed++;
            } else if (tx.getSenderId().equals(tx.getReceiverId())) {
                tx.setStatus(OfflineTransaction.Status.FAILED);
                tx.setNote("Sender and receiver cannot be the same");
                failed++;
            } else {
                sender.setBalance(sender.getBalance() - tx.getAmount());
                receiver.setBalance(receiver.getBalance() + tx.getAmount());
                userRepository.save(sender);
                userRepository.save(receiver);

                tx.setStatus(OfflineTransaction.Status.SETTLED);
                tx.setNote("Settled successfully");
                settled++;
            }

            tx.setSyncedAt(LocalDateTime.now());
            queueRepository.save(tx);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalProcessed", pending.size());
        result.put("settled", settled);
        result.put("duplicates", duplicates);
        result.put("failed", failed);
        return result;
    }

    /** Clear the entire queue (for demo purposes). */
    public void clearQueue() {
        queueRepository.deleteAll();
    }
}