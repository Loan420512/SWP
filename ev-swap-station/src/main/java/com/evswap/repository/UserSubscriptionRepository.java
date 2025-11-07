package com.evswap.repository;

import com.evswap.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Integer> {

    @Query("""
        SELECT s FROM UserSubscription s
        WHERE s.user.id = :userId
          AND s.status = 'ACTIVE'
          AND s.endDate >= :now
        ORDER BY s.endDate DESC
    """)
    Optional<UserSubscription> findActiveByUserId(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    Optional<UserSubscription> findFirstByUserIdAndStatusOrderByEndDateDesc(Integer userId, String status);

    List<UserSubscription> findAllByStatusAndEndDateBefore(String status, LocalDateTime now);

}
