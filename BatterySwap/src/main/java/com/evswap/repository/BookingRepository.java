package com.evswap.repository;

import com.evswap.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    boolean existsByUser_IdAndStatus(Integer userId, String status);
}
