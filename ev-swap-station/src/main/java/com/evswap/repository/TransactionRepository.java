package com.evswap.repository;

import com.evswap.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findFirstByBookingIdAndTransactionTypeOrderByTransactionTimeDesc(
            Long bookingId, String transactionType
    );

    List<Transaction> findAllByStatusAndTransactionTypeAndTransactionTimeBefore(
            String status, String transactionType, LocalDateTime threshold);

    List<Transaction> findByUserIdAndPackagePlanIdAndStatus(
            Integer userId, Integer packagePlanId, String status);

}
