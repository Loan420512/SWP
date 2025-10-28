package com.evswap.repository;

import com.evswap.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findFirstByBookingIdAndTransactionTypeOrderByTransactionTimeDesc(
            Long bookingId, String transactionType
    );
}
