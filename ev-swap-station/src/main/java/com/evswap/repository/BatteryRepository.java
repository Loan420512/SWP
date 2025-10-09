package com.evswap.repository;

import com.evswap.entity.Battery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BatteryRepository extends JpaRepository<Battery, Integer> {
    List<Battery> findByStatus(String status);
    List<Battery> findByBatteryNameContaining(String keyword);
}