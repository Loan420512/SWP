package com.evswap.repository;

import com.evswap.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = """
        SELECT COUNT(*) FROM Booking b
        WHERE b.UserID = :userId
          AND b.Status IN ('BOOKED','ARRIVED')
          AND ABS(DATEDIFF(MINUTE, b.TimeDate, :timeSlot)) <= :windowMin
        """, nativeQuery = true)
    long countOpenAround(Integer userId, LocalDateTime timeSlot, int windowMin);

    Page<Booking> findByUser_Id(Integer userId, Pageable pageable);
}
