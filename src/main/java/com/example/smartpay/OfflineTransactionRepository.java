package com.example.smartpay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfflineTransactionRepository extends JpaRepository<OfflineTransaction, Long> {
    Optional<OfflineTransaction> findByClientTxId(String clientTxId);
    List<OfflineTransaction> findByStatus(OfflineTransaction.Status status);
}